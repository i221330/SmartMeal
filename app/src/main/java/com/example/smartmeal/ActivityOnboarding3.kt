package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ActivityOnboarding3 : AppCompatActivity() {

    private val TAG = "ActivityOnboarding3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_onboarding3)
            Log.d(TAG, "Activity created successfully")

            setupButtons()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "Error loading screen: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupButtons() {
        try {
            val finishButton = findViewById<MaterialButton>(R.id.nextButton)
            val backButton = findViewById<MaterialButton>(R.id.backButton)

            if (finishButton != null) {
                Log.d(TAG, "Finish button found")
                finishButton.setOnClickListener {
                    Log.d(TAG, "Finish button clicked")
                    completeOnboarding()
                }
            } else {
                Log.w(TAG, "Finish button not found")
                Toast.makeText(this, "Tap screen to complete", Toast.LENGTH_SHORT).show()
                findViewById<View>(android.R.id.content)?.setOnClickListener {
                    completeOnboarding()
                }
            }

            backButton?.setOnClickListener {
                Log.d(TAG, "Back button clicked")
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupButtons", e)
            Toast.makeText(this, "Button setup error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun completeOnboarding() {
        try {
            Log.d(TAG, "Completing onboarding")

            // Mark onboarding as complete
            val prefs = getSharedPreferences("smartmeal_prefs", MODE_PRIVATE)
            prefs.edit().putBoolean("onboarding_complete", true).apply()
            Log.d(TAG, "Onboarding marked complete")

            // Go to home with clear task flags
            val intent = Intent(this, ActivityHome::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Log.d(TAG, "Started ActivityHome")

            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error completing onboarding", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}