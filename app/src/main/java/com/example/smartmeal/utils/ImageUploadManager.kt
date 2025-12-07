package com.example.smartmeal.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageUploadManager(private val context: Context) {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    /**
     * Upload image to Firebase Storage
     * @param imageUri URI of the image to upload
     * @param folder Folder name: "pantry", "meals", "profiles"
     * @param userId Current user's ID
     * @return Result with download URL or error
     */
    suspend fun uploadImage(
        imageUri: Uri,
        folder: String,
        userId: String
    ): Result<String> {
        return try {
            // Compress image before uploading
            val compressedImage = compressImage(imageUri)

            val fileName = "${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child("$folder/$userId/$fileName")

            // Upload compressed image
            val uploadTask = storageRef.putBytes(compressedImage).await()

            // Get download URL
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Upload image from file path
     */
    suspend fun uploadImageFromPath(
        imagePath: String,
        folder: String,
        userId: String
    ): Result<String> {
        return try {
            val file = File(imagePath)
            if (!file.exists()) {
                return Result.failure(Exception("File not found"))
            }

            val uri = Uri.fromFile(file)
            uploadImage(uri, folder, userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete image from Firebase Storage
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            if (imageUrl.isEmpty() || !imageUrl.startsWith("http")) {
                return Result.success(Unit)
            }

            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Compress image to reduce upload size and time
     */
    private fun compressImage(uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Calculate scaled dimensions
        val maxSize = 1024 // Max width/height in pixels
        val width = bitmap.width
        val height = bitmap.height

        val scaledBitmap = if (width > maxSize || height > maxSize) {
            val scale = maxSize.toFloat() / maxOf(width, height)
            val scaledWidth = (width * scale).toInt()
            val scaledHeight = (height * scale).toInt()
            Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
        } else {
            bitmap
        }

        // Compress to JPEG
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }
        bitmap.recycle()

        return outputStream.toByteArray()
    }

    /**
     * Get storage reference for a specific path
     */
    fun getStorageReference(path: String): StorageReference {
        return storage.reference.child(path)
    }

    /**
     * Save image to local cache
     */
    fun saveToLocalCache(uri: Uri, fileName: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val cacheDir = File(context.cacheDir, "images")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val FOLDER_PANTRY = "pantry"
        const val FOLDER_MEALS = "meals"
        const val FOLDER_PROFILES = "profiles"
        const val FOLDER_RECIPES = "recipes"
    }
}

