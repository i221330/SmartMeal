package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ActivityOnboarding1 : AppCompatActivity() {

    private val TAG = "ActivityOnboarding1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_onboarding1)
            Log.d(TAG, "Activity created successfully")

            setupNextButton()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "Error loading screen: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupNextButton() {
        try {
            val nextButton = findViewById<MaterialButton>(R.id.nextButton)

            if (nextButton != null) {
                Log.d(TAG, "Next button found")
                nextButton.setOnClickListener {
                    Log.d(TAG, "Next button clicked")
                    try {
                        val intent = Intent(this, ActivityOnboarding2::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        Log.d(TAG, "Started ActivityOnboarding2")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting ActivityOnboarding2", e)
                        Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.w(TAG, "Next button not found, using tap-anywhere fallback")
                Toast.makeText(this, "Tap screen to continue", Toast.LENGTH_SHORT).show()
                findViewById<View>(android.R.id.content)?.setOnClickListener {
                    try {
                        startActivity(Intent(this, ActivityOnboarding2::class.java))
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in fallback navigation", e)
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupNextButton", e)
            Toast.makeText(this, "Button setup error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}