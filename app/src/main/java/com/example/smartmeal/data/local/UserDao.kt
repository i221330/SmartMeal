package com.example.smartmeal.data.local

import androidx.room.*
import com.example.smartmeal.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET lastSyncTime = :timestamp WHERE uid = :uid")
    suspend fun updateLastSyncTime(uid: String, timestamp: Long)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}

