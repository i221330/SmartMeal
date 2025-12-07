package com.example.smartmeal.services

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartmeal.ActivityHome
import com.example.smartmeal.R
import com.example.smartmeal.SmartMealApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message received from: ${message.from}")

        // Handle data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${message.data}")
            handleDataMessage(message.data)
        }

        // Handle notification payload
        message.notification?.let {
            Log.d(TAG, "Message notification: ${it.title} - ${it.body}")
            showNotification(
                title = it.title ?: "SmartMeal",
                body = it.body ?: "",
                type = message.data["type"] ?: "general"
            )
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return
        val title = data["title"] ?: "SmartMeal"
        val message = data["message"] ?: ""

        showNotification(title, message, type)
    }

    private fun showNotification(title: String, body: String, type: String) {
        val intent = Intent(this, ActivityHome::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = when (type) {
            "meal_reminder" -> SmartMealApplication.CHANNEL_MEAL_REMINDERS
            "shopping" -> SmartMealApplication.CHANNEL_SHOPPING_ALERTS
            "sync" -> SmartMealApplication.CHANNEL_SYNC_STATUS
            else -> SmartMealApplication.CHANNEL_MEAL_REMINDERS
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing notification permission", e)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // Send token to your server if you have one
        // For now, you can store it in SharedPreferences or send to Firestore
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Implement sending token to your backend server
        // or store in Firestore under user document
        Log.d(TAG, "Token sent to server: $token")
    }

    companion object {
        private const val TAG = "FCMService"
    }
}

