package com.example.smartmeal.data.repository

import androidx.lifecycle.LiveData
import com.example.smartmeal.data.local.ShoppingDao
import com.example.smartmeal.data.model.ShoppingItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ShoppingRepository(
    private val shoppingDao: ShoppingDao,
    private val firestore: FirebaseFirestore
) {

    val allShoppingItems: LiveData<List<ShoppingItem>> = shoppingDao.getAllShoppingItems()

    suspend fun getAllShoppingItemsList(): List<ShoppingItem> {
        return shoppingDao.getAllShoppingItemsList()
    }

    suspend fun getShoppingItemById(id: String): ShoppingItem? {
        return shoppingDao.getShoppingItemById(id)
    }

    suspend fun insertShoppingItem(item: ShoppingItem) {
        shoppingDao.insertShoppingItem(item.copy(isSynced = false))
    }

    suspend fun updateShoppingItem(item: ShoppingItem) {
        shoppingDao.updateShoppingItem(item.copy(
            lastUpdated = System.currentTimeMillis(),
            isSynced = false
        ))
    }

    suspend fun updateCompletionStatus(id: String, isCompleted: Boolean) {
        shoppingDao.updateCompletionStatus(id, isCompleted)
    }

    suspend fun deleteShoppingItem(id: String) {
        shoppingDao.softDeleteShoppingItem(id)
    }

    suspend fun clearCompletedItems() {
        shoppingDao.clearCompletedItems()
    }

    suspend fun syncToCloud(userId: String) {
        val unsyncedItems = shoppingDao.getUnsyncedItems()

        unsyncedItems.forEach { item ->
            try {
                if (item.isDeleted) {
                    firestore.collection("users")
                        .document(userId)
                        .collection("shopping")
                        .document(item.id)
                        .delete()
                        .await()
                } else {
                    firestore.collection("users")
                        .document(userId)
                        .collection("shopping")
                        .document(item.id)
                        .set(item)
                        .await()
                }

                shoppingDao.markAsSynced(item.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun syncFromCloud(userId: String) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("shopping")
                .get()
                .await()

            val cloudItems = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ShoppingItem::class.java)?.copy(isSynced = true)
            }

            shoppingDao.insertAll(cloudItems)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

