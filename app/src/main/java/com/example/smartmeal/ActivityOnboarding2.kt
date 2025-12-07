package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ActivityOnboarding2 : AppCompatActivity() {

    private val TAG = "ActivityOnboarding2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_onboarding2)
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
            val nextButton = findViewById<MaterialButton>(R.id.nextButton)
            val backButton = findViewById<MaterialButton>(R.id.backButton)

            if (nextButton != null) {
                Log.d(TAG, "Next button found")
                nextButton.setOnClickListener {
                    Log.d(TAG, "Next button clicked")
                    try {
                        val intent = Intent(this, ActivityOnboarding3::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        Log.d(TAG, "Started ActivityOnboarding3")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting ActivityOnboarding3", e)
                        Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.w(TAG, "Next button not found")
                Toast.makeText(this, "Tap screen to continue", Toast.LENGTH_SHORT).show()
                findViewById<View>(android.R.id.content)?.setOnClickListener {
                    startActivity(Intent(this, ActivityOnboarding3::class.java))
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
}