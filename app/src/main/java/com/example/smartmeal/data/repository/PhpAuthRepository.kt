package com.example.smartmeal.data.repository

import android.util.Log
import com.example.smartmeal.data.local.UserDao
import com.example.smartmeal.data.model.User
import com.example.smartmeal.network.ApiClient
import com.example.smartmeal.network.models.UserLoginRequest
import com.example.smartmeal.network.models.UserRegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.net.ConnectException
import java.net.SocketTimeoutException

class PhpAuthRepository(private val userDao: UserDao) {

    private val TAG = "PhpAuthRepository"
    private val TIMEOUT_MS = 10000L // 10 seconds timeout

    /**
     * Register user with PHP backend only
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Attempting signup: $email")
            Log.d(TAG, "API URL: ${ApiClient.BASE_URL}")

            val request = UserRegisterRequest(
                firebaseUid = "", // Not used
                email = email,
                password = password,
                phoneNumber = null,
                displayName = displayName,
                profileImageUrl = null
            )

            Log.d(TAG, "Making API call with timeout: ${TIMEOUT_MS}ms")

            val response = withTimeout(TIMEOUT_MS) {
                ApiClient.userService.registerUser(request)
            }

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d(TAG, "Signup successful: ${body.message}")

                // Create user object
                val user = User(
                    uid = body.user_id ?: body.email ?: email,
                    email = email,
                    displayName = displayName,
                    joinedDate = System.currentTimeMillis()
                )

                // Save to local database
                userDao.insertUser(user)
                Log.d(TAG, "User saved to local DB")

                Result.success(user)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Log.e(TAG, "Signup failed: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Signup timeout - server not responding", e)
            Result.failure(Exception("Connection timeout. Is XAMPP running?"))
        } catch (e: ConnectException) {
            Log.e(TAG, "Signup connection failed - cannot reach server", e)
            Result.failure(Exception("Cannot connect to server. Check XAMPP and network."))
        } catch (e: Exception) {
            Log.e(TAG, "Signup exception: ${e.javaClass.simpleName}", e)
            Result.failure(Exception("Signup error: ${e.message}"))
        }
    }

    /**
     * Login user with PHP backend only
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Attempting login: $email")
            Log.d(TAG, "API URL: ${ApiClient.BASE_URL}")

            val request = UserLoginRequest(
                email = email,
                password = password,
                firebaseUid = "" // Not used
            )

            Log.d(TAG, "Making API call with timeout: ${TIMEOUT_MS}ms")

            val response = withTimeout(TIMEOUT_MS) {
                ApiClient.userService.loginUser(request)
            }

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d(TAG, "Login successful: ${body.message}")

                val userData = body.user
                if (userData != null) {
                    val user = User(
                        uid = userData.user_id ?: userData.email ?: email,
                        email = userData.email ?: email,
                        displayName = userData.display_name,
                        phoneNumber = userData.phone_number,
                        profileImageUrl = userData.profile_image_url,
                        joinedDate = System.currentTimeMillis()
                    )

                    // Save to local database
                    userDao.insertUser(user)
                    Log.d(TAG, "User saved to local DB")

                    Result.success(user)
                } else {
                    Result.failure(Exception("Invalid response from server"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed"
                Log.e(TAG, "Login failed: $errorMsg")
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Login timeout - server not responding", e)
            Result.failure(Exception("Connection timeout. Is XAMPP running?"))
        } catch (e: ConnectException) {
            Log.e(TAG, "Login connection failed - cannot reach server", e)
            Result.failure(Exception("Cannot connect to server. Check XAMPP and network."))
        } catch (e: Exception) {
            Log.e(TAG, "Login exception: ${e.javaClass.simpleName}", e)
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    suspend fun signOut() {
        try {
            userDao.deleteAll()
            Log.d(TAG, "User signed out, local data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out", e)
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            userDao.getCurrentUser()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user", e)
            null
        }
    }

    fun isUserLoggedIn(): Boolean {
        // Will be checked via getCurrentUser() != null
        return false
    }

    fun userDao(): UserDao {
        return userDao
    }
}

