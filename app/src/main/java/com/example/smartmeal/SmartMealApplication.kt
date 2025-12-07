package com.example.smartmeal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.smartmeal.data.local.SmartMealDatabase
import com.google.firebase.FirebaseApp

class SmartMealApplication : Application() {

    val database: SmartMealDatabase by lazy { SmartMealDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase safely
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
            // Continue without Firebase - app can still work locally
        }

        // Create notification channels
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Meal Reminders Channel
            val mealChannel = NotificationChannel(
                "meal_reminders",
                "Meal Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for meal preparation reminders"
            }

            // Shopping Alerts Channel
            val shoppingChannel = NotificationChannel(
                "shopping_alerts",
                "Shopping Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for shopping list updates"
            }

            // Sync Status Channel
            val syncChannel = NotificationChannel(
                "sync_status",
                "Sync Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Data synchronization status"
            }

            notificationManager.createNotificationChannel(mealChannel)
            notificationManager.createNotificationChannel(shoppingChannel)
            notificationManager.createNotificationChannel(syncChannel)
        }
    }

    companion object {
        const val CHANNEL_MEAL_REMINDERS = "meal_reminders"
        const val CHANNEL_SHOPPING_ALERTS = "shopping_alerts"
        const val CHANNEL_SYNC_STATUS = "sync_status"
    }
}

