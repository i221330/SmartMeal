package com.example.smartmeal

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActivityRecipeList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        // TODO: Implement recipe list functionality later
        Toast.makeText(this, "Recipe List - Coming Soon!", Toast.LENGTH_SHORT).show()

        setupBackButton()
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
}

