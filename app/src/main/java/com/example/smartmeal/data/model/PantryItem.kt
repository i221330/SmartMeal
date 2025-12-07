package com.example.smartmeal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val quantity: String,
    val imageUrl: String? = null,
    val category: String? = null,
    val expiryDate: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)

