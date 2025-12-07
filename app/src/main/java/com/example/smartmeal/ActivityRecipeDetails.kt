package com.example.smartmeal

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActivityRecipeDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // TODO: Implement recipe details functionality later
        Toast.makeText(this, "Recipe Details - Coming Soon!", Toast.LENGTH_SHORT).show()

        setupBackButton()
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
}

