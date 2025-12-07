package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartmeal.worker.SyncWorker
import com.google.firebase.auth.FirebaseAuth

class ActivitySplash : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Schedule sync worker safely
        try {
            SyncWorker.schedule(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Wait 2 seconds then check auth state
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun navigateToNextScreen() {
        // Always logout on app launch so user must login every time
        auth.signOut()

        // Always go to login (user must login every time app opens)
        val intent = Intent(this, ActivityLogin::class.java)
        startActivity(intent)
        finish()
    }
}