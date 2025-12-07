package com.example.smartmeal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val displayName: String? = null,
    val profileImageUrl: String? = null,
    val isPremium: Boolean = false,
    val joinedDate: Long = System.currentTimeMillis(),
    val lastSyncTime: Long = 0L
)

