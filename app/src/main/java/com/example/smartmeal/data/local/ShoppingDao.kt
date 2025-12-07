package com.example.smartmeal.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartmeal.data.model.ShoppingItem

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items WHERE isDeleted = 0 ORDER BY isCompleted ASC, name ASC")
    fun getAllShoppingItems(): LiveData<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE isDeleted = 0 ORDER BY isCompleted ASC, name ASC")
    suspend fun getAllShoppingItemsList(): List<ShoppingItem>

    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getShoppingItemById(id: String): ShoppingItem?

    @Query("SELECT * FROM shopping_items WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedItems(): List<ShoppingItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingItem>)

    @Update
    suspend fun updateShoppingItem(item: ShoppingItem)

    @Query("UPDATE shopping_items SET isCompleted = :isCompleted, lastUpdated = :timestamp, isSynced = 0 WHERE id = :id")
    suspend fun updateCompletionStatus(id: String, isCompleted: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE shopping_items SET isDeleted = 1, lastUpdated = :timestamp WHERE id = :id")
    suspend fun softDeleteShoppingItem(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE shopping_items SET isDeleted = 1, lastUpdated = :timestamp WHERE isCompleted = 1")
    suspend fun clearCompletedItems(timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAll()

    @Query("UPDATE shopping_items SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}

