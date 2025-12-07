package com.example.smartmeal.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartmeal.data.model.PantryItem

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllPantryItems(): LiveData<List<PantryItem>>

    @Query("SELECT * FROM pantry_items WHERE isDeleted = 0 ORDER BY name ASC")
    suspend fun getAllPantryItemsList(): List<PantryItem>

    @Query("SELECT * FROM pantry_items WHERE id = :id")
    suspend fun getPantryItemById(id: String): PantryItem?

    @Query("SELECT * FROM pantry_items WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedItems(): List<PantryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItem(item: PantryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PantryItem>)

    @Update
    suspend fun updatePantryItem(item: PantryItem)

    @Query("UPDATE pantry_items SET isDeleted = 1, lastUpdated = :timestamp WHERE id = :id")
    suspend fun softDeletePantryItem(id: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deletePantryItem(item: PantryItem)

    @Query("DELETE FROM pantry_items")
    suspend fun deleteAll()

    @Query("UPDATE pantry_items SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}

