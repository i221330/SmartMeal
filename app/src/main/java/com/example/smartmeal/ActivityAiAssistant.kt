package com.example.smartmeal

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActivityAiAssistant : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        // TODO: Implement AI Assistant functionality later
        Toast.makeText(this, "AI Assistant - Coming Soon!", Toast.LENGTH_SHORT).show()

        setupBackButton()
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
}

