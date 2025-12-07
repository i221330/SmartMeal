package com.example.smartmeal

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.smartmeal.data.model.User
import com.example.smartmeal.data.repository.PhpAuthRepository
import com.example.smartmeal.viewmodel.PhpAuthViewModel
import com.example.smartmeal.viewmodel.PhpAuthViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ActivityProfile : AppCompatActivity() {

    private val TAG = "ActivityProfile"

    private val viewModel: PhpAuthViewModel by viewModels {
        PhpAuthViewModelFactory(
            PhpAuthRepository(
                (application as SmartMealApplication).database.userDao()
            )
        )
    }

    private lateinit var repository: PhpAuthRepository
    private lateinit var sharedPreferences: SharedPreferences

    // UI Elements
    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var memberTextView: TextView
    private lateinit var joinedTextView: TextView
    private lateinit var notificationsSwitch: SwitchMaterial

    private var currentUser: User? = null
    private var selectedImageUri: Uri? = null

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                // Copy image to internal storage for persistence
                copyImageToInternalStorage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        repository = PhpAuthRepository(
            (application as SmartMealApplication).database.userDao()
        )

        sharedPreferences = getSharedPreferences("SmartMealPrefs", MODE_PRIVATE)

        initializeViews()
        setupListeners()
        setupBottomNavigation()
        loadUserData()
        loadNotificationPreference()
    }

    private fun initializeViews() {
        profileImageView = findViewById(R.id.profileImageView)
        nameTextView = findViewById(R.id.nameTextView)
        memberTextView = findViewById(R.id.memberTextView)
        joinedTextView = findViewById(R.id.joinedTextView)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
    }

    private fun setupListeners() {
        // Logout button
        findViewById<MaterialButton>(R.id.logoutButton)?.setOnClickListener {
            showLogoutConfirmation()
        }

        // Settings button - Edit name
        findViewById<View>(R.id.settingsButton)?.setOnClickListener {
            showEditNameDialog()
        }

        // Profile picture click - view full size or change
        profileImageView.setOnClickListener {
            showProfilePictureOptions()
        }

        // Notifications toggle
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference(isChecked)
            val message = if (isChecked) "Notifications enabled" else "Notifications disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                currentUser = repository.getCurrentUser()

                if (currentUser != null) {
                    Log.d(TAG, "═══════════════════════════════════════")
                    Log.d(TAG, "loadUserData: User loaded")
                    Log.d(TAG, "  - UID: ${currentUser!!.uid}")
                    Log.d(TAG, "  - Email: ${currentUser!!.email}")
                    Log.d(TAG, "  - Display Name: ${currentUser!!.displayName}")
                    Log.d(TAG, "  - Profile Image URL: ${currentUser!!.profileImageUrl}")
                    Log.d(TAG, "═══════════════════════════════════════")

                    // Check if we need to restore local profile image
                    checkAndRestoreProfileImage()

                    updateUI(currentUser!!)
                } else {
                    Log.w(TAG, "No user data found in database")
                    // Show default/placeholder data
                    showDefaultProfile()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user data", e)
                showDefaultProfile()
            }
        }
    }

    /**
     * Check if a profile image file exists locally but isn't in the database OR
     * if the database has a path that doesn't match the actual file location
     * This can happen after logout/login on the same device
     */
    private suspend fun checkAndRestoreProfileImage() = withContext(Dispatchers.IO) {
        try {
            val user = currentUser ?: return@withContext

            Log.d(TAG, "════════════════════════════════════════════════")
            Log.d(TAG, "checkAndRestoreProfileImage: Starting check")
            Log.d(TAG, "  - User ID: ${user.uid}")
            Log.d(TAG, "  - Current profileImageUrl in DB: '${user.profileImageUrl}'")

            val fileName = "profile_${user.uid}.jpg"
            val expectedFile = File(filesDir, fileName)
            Log.d(TAG, "  - Expected file path: ${expectedFile.absolutePath}")
            Log.d(TAG, "  - Expected file exists: ${expectedFile.exists()}")

            // Case 1: Database has a profile image URL
            if (!user.profileImageUrl.isNullOrEmpty()) {
                val currentFile = File(user.profileImageUrl)
                Log.d(TAG, "  - DB has profileImageUrl, checking file...")
                Log.d(TAG, "  - File path in DB: ${currentFile.absolutePath}")
                Log.d(TAG, "  - File exists: ${currentFile.exists()}")

                if (currentFile.exists()) {
                    Log.d(TAG, "  ✓ Profile image file exists at DB path, all good!")
                    return@withContext
                } else {
                    Log.w(TAG, "  ✗ File in DB doesn't exist, checking expected location...")

                    // Check if file exists at expected location
                    if (expectedFile.exists()) {
                        Log.d(TAG, "  ✓ Found file at expected location! Updating DB...")
                        val correctPath = expectedFile.absolutePath

                        // Update local database with correct path
                        val updatedUser = user.copy(profileImageUrl = correctPath)
                        repository.userDao().updateUser(updatedUser)
                        currentUser = updatedUser
                        Log.d(TAG, "  ✓ Updated local DB with correct path")

                        // Sync to server
                        syncProfileImageToServer(user.uid, correctPath)
                    } else {
                        Log.w(TAG, "  ✗ File not found at expected location either, clearing DB")
                        // Clear invalid URL from database
                        val updatedUser = user.copy(profileImageUrl = null)
                        repository.userDao().updateUser(updatedUser)
                        currentUser = updatedUser
                    }
                }
            } else {
                // Case 2: Database has no profile image URL, check if file exists
                Log.d(TAG, "  - DB has no profileImageUrl, checking for local file...")

                if (expectedFile.exists()) {
                    Log.d(TAG, "  ✓ Found local profile image file! Restoring to DB...")
                    val correctPath = expectedFile.absolutePath

                    // Update local database
                    val updatedUser = user.copy(profileImageUrl = correctPath)
                    repository.userDao().updateUser(updatedUser)
                    currentUser = updatedUser
                    Log.d(TAG, "  ✓ Profile image restored to local database")

                    // Sync to server
                    syncProfileImageToServer(user.uid, correctPath)
                } else {
                    Log.d(TAG, "  - No local profile image file found")
                }
            }

            Log.d(TAG, "checkAndRestoreProfileImage: Complete")
            Log.d(TAG, "════════════════════════════════════════════════")
        } catch (e: Exception) {
            Log.e(TAG, "Error checking/restoring profile image", e)
        }
    }

    private suspend fun syncProfileImageToServer(userId: String, imagePath: String) {
        try {
            Log.d(TAG, "Syncing profile image to server: userId=$userId, path=$imagePath")
            val result = repository.updateUserProfile(
                userId = userId,
                profileImageUrl = imagePath
            )
            if (result.isSuccess) {
                Log.d(TAG, "✓ Profile image URL synced to server successfully")
            } else {
                Log.w(TAG, "✗ Failed to sync profile image to server: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Exception syncing profile image to server", e)
        }
    }

    private fun updateUI(user: User) {
        // Display name
        nameTextView.text = user.displayName ?: user.email ?: "User"

        // Member status
        if (user.isPremium) {
            memberTextView.text = getString(R.string.premium_member)
            memberTextView.visibility = View.VISIBLE
        } else {
            memberTextView.text = "Free Member"
            memberTextView.visibility = View.VISIBLE
        }

        // Joined date
        val joinedYear = formatJoinedDate(user.joinedDate)
        joinedTextView.text = "Joined $joinedYear"

        // Profile image - prioritize database value
        Log.d(TAG, "updateUI: Starting profile image load")
        Log.d(TAG, "  - profileImageUrl from DB: '${user.profileImageUrl}'")
        if (!user.profileImageUrl.isNullOrEmpty()) {
            val file = File(user.profileImageUrl)
            Log.d(TAG, "  - File path: ${file.absolutePath}")
            Log.d(TAG, "  - File exists: ${file.exists()}")
            Log.d(TAG, "  - File can read: ${file.canRead()}")
            Log.d(TAG, "  - File length: ${file.length()} bytes")

            if (file.exists()) {
                Log.d(TAG, "  ✓ Loading profile image from file")
                loadProfileImage(user.profileImageUrl)
            } else {
                Log.w(TAG, "  ✗ Profile image file not found, using default")
                profileImageView.setImageResource(R.drawable.ic_profile_default)
            }
        } else {
            // Use default avatar
            Log.d(TAG, "  - No profile image URL in database, using default")
            profileImageView.setImageResource(R.drawable.ic_profile_default)
        }
    }

    private fun showDefaultProfile() {
        nameTextView.text = "Guest User"
        memberTextView.text = "Free Member"
        joinedTextView.text = "Joined 2025"
        profileImageView.setImageResource(R.drawable.ic_profile_default)
    }

    private fun formatJoinedDate(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            val format = SimpleDateFormat("yyyy", Locale.getDefault())
            format.format(date)
        } catch (e: Exception) {
            "2025"
        }
    }

    private fun loadProfileImage(imageUrl: String) {
        try {
            val requestBuilder = Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_default)
                .error(R.drawable.ic_profile_default)
                .circleCrop()

            // Skip cache for internal storage files to ensure fresh images
            if (imageUrl.startsWith("/data/")) {
                requestBuilder.skipMemoryCache(true)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
            }

            requestBuilder.into(profileImageView)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile image", e)
            profileImageView.setImageResource(R.drawable.ic_profile_default)
        }
    }

    private fun showProfilePictureOptions() {
        val options = arrayOf("View full size", "Change picture", "Remove picture")

        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewFullSizeImage()
                    1 -> openImagePicker()
                    2 -> removeProfilePicture()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun viewFullSizeImage() {
        // Simple implementation - show toast for now
        // Can be enhanced to open full screen image viewer
        Toast.makeText(this, "Full size view coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun openImagePicker() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening image picker", e)
            Toast.makeText(this, "Unable to open image picker", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProfilePicture() {
        profileImageView.setImageResource(R.drawable.ic_profile_default)

        // Remove from database and delete file
        lifecycleScope.launch {
            try {
                currentUser?.let { user ->
                    // Delete the image file from internal storage
                    val fileName = "profile_${user.uid}.jpg"
                    val file = File(filesDir, fileName)
                    if (file.exists()) {
                        file.delete()
                        Log.d(TAG, "Profile image file deleted")
                    }

                    // Update database
                    val updatedUser = user.copy(profileImageUrl = null)
                    repository.userDao().updateUser(updatedUser)
                    currentUser = updatedUser
                    Log.d(TAG, "Profile image removed from database")

                    // Sync to server
                    try {
                        val result = repository.updateUserProfile(
                            userId = user.uid,
                            profileImageUrl = ""  // Empty string to clear server-side
                        )
                        if (result.isSuccess) {
                            Log.d(TAG, "Profile image removal synced to server successfully")
                        } else {
                            Log.w(TAG, "Failed to sync profile image removal to server: ${result.exceptionOrNull()?.message}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error syncing profile image removal to server", e)
                    }
                }

                // Clear SharedPreferences
                sharedPreferences.edit().remove("profile_image_uri").apply()

            } catch (e: Exception) {
                Log.e(TAG, "Error removing profile image", e)
            }
        }

        Toast.makeText(this, "Profile picture removed", Toast.LENGTH_SHORT).show()
    }

    private fun copyImageToInternalStorage(sourceUri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Read image from source URI
                val inputStream = contentResolver.openInputStream(sourceUri)
                if (inputStream == null) {
                    Log.e(TAG, "Failed to open input stream for URI: $sourceUri")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ActivityProfile, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Create a file in internal storage
                val fileName = "profile_${currentUser?.uid ?: "default"}.jpg"
                val file = File(filesDir, fileName)

                // Delete old file if it exists to ensure fresh save
                if (file.exists()) {
                    file.delete()
                    Log.d(TAG, "Deleted old profile image file")
                    // Clear disk cache on IO thread
                    Glide.get(this@ActivityProfile).clearDiskCache()
                }

                // Copy the image
                val outputStream = FileOutputStream(file)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // Get the internal file URI
                val internalUri = file.absolutePath
                Log.d(TAG, "════════════════════════════════════════")
                Log.d(TAG, "Image saved to internal storage")
                Log.d(TAG, "  - Path: $internalUri")
                Log.d(TAG, "  - File exists: ${file.exists()}")
                Log.d(TAG, "  - File size: ${file.length()} bytes")
                Log.d(TAG, "════════════════════════════════════════")

                // Save to database FIRST (while still on IO thread)
                try {
                    currentUser?.let { user ->
                        Log.d(TAG, "Updating local database for user: ${user.uid}")
                        val updatedUser = user.copy(profileImageUrl = internalUri)
                        repository.userDao().updateUser(updatedUser)
                        currentUser = updatedUser
                        Log.d(TAG, "✓ Profile image URI saved to local database")

                        // Sync to server
                        try {
                            Log.d(TAG, "═══ SYNCING TO SERVER ═══")
                            Log.d(TAG, "  - User ID: ${user.uid}")
                            Log.d(TAG, "  - Image URL: $internalUri")
                            val result = repository.updateUserProfile(
                                userId = user.uid,
                                profileImageUrl = internalUri
                            )
                            if (result.isSuccess) {
                                Log.d(TAG, "✓✓✓ Profile image URL synced to server successfully!")
                                Log.d(TAG, "═══════════════════════════════════════")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@ActivityProfile, "✓ Profile saved and synced!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                                Log.e(TAG, "✗✗✗ Failed to sync profile image to server!")
                                Log.e(TAG, "  - Error: $errorMsg")
                                Log.e(TAG, "═══════════════════════════════════════")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@ActivityProfile, "⚠ Saved locally but sync failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "✗✗✗ Exception syncing profile image to server!")
                            Log.e(TAG, "  - Exception: ${e.javaClass.simpleName}")
                            Log.e(TAG, "  - Message: ${e.message}")
                            Log.e(TAG, "═══════════════════════════════════════")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@ActivityProfile, "⚠ Server sync error (saved locally)", Toast.LENGTH_SHORT).show()
                            }
                            // Don't fail the operation if server sync fails
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving profile image to database", e)
                }

                // Switch to main thread for UI updates
                withContext(Dispatchers.Main) {
                    // Save to SharedPreferences
                    sharedPreferences.edit().putString("profile_image_uri", internalUri).apply()

                    // Clear Glide cache for this file to force reload
                    Glide.get(this@ActivityProfile).clearMemory()

                    // Load the image
                    loadProfileImage(internalUri)
                    Toast.makeText(this@ActivityProfile, R.string.profile_picture_updated, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error copying image to internal storage", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ActivityProfile, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveProfileImageUri(uri: String) {
        // Save to SharedPreferences for immediate access
        sharedPreferences.edit().putString("profile_image_uri", uri).apply()

        // Save to database for persistence across logout/login
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                currentUser?.let { user ->
                    val updatedUser = user.copy(profileImageUrl = uri)
                    repository.userDao().updateUser(updatedUser)
                    currentUser = updatedUser
                    Log.d(TAG, "Profile image URI saved to database: $uri")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving profile image to database", e)
            }
        }
    }

    private fun loadNotificationPreference() {
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        notificationsSwitch.isChecked = notificationsEnabled
    }

    private fun saveNotificationPreference(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                viewModel.signOut()

                // Clear saved preferences
                sharedPreferences.edit().clear().apply()

                Toast.makeText(this@ActivityProfile, "Logged out successfully", Toast.LENGTH_SHORT).show()

                // Navigate to login
                val intent = Intent(this@ActivityProfile, ActivityLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finishAffinity()
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
                Toast.makeText(this@ActivityProfile, "Error logging out", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditNameDialog() {
        val editText = android.widget.EditText(this).apply {
            setText(currentUser?.displayName ?: nameTextView.text.toString())
            hint = "Enter your name"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setPadding(50, 40, 50, 40)
        }

        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Edit Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    updateUserName(newName)
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUserName(newName: String) {
        lifecycleScope.launch {
            try {
                currentUser?.let { user ->
                    Log.d(TAG, "═══════════════════════════════════════")
                    Log.d(TAG, "updateUserName: Starting name update")
                    Log.d(TAG, "  - User ID: ${user.uid}")
                    Log.d(TAG, "  - Old name: ${user.displayName}")
                    Log.d(TAG, "  - New name: $newName")

                    // Sync to server FIRST
                    Log.d(TAG, "  - Step 1: Syncing to server...")
                    val result = repository.updateUserProfile(
                        userId = user.uid,
                        displayName = newName
                    )

                    if (result.isSuccess) {
                        Log.d(TAG, "  ✓ Server update successful!")

                        // Update local database only after server confirms success
                        Log.d(TAG, "  - Step 2: Updating local database...")
                        val updatedUser = user.copy(displayName = newName)
                        repository.userDao().updateUser(updatedUser)
                        currentUser = updatedUser
                        Log.d(TAG, "  ✓ Local database updated")

                        withContext(Dispatchers.Main) {
                            nameTextView.text = newName
                            Toast.makeText(this@ActivityProfile, "Name updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        Log.d(TAG, "  ✓ Name update complete!")
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                        Log.e(TAG, "  ✗ Server update failed: $errorMsg")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ActivityProfile,
                                "Failed to update name: $errorMsg",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    Log.d(TAG, "═══════════════════════════════════════")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating user name", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ActivityProfile,
                        "Error updating name: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.homeNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityHome::class.java))
            finish()
        }
        findViewById<View>(R.id.pantryNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityPantry::class.java))
            finish()
        }
        findViewById<View>(R.id.plannerNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityMealPlanner::class.java))
            finish()
        }
        findViewById<View>(R.id.shoppingNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityShoppingList::class.java))
            finish()
        }
        findViewById<View>(R.id.profileNav)?.setOnClickListener {
            // Already on profile
        }
    }
}