package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActivityShoppingList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        // TODO: Implement shopping list functionality later
        Toast.makeText(this, "Shopping List - Coming Soon!", Toast.LENGTH_SHORT).show()

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.homeNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityHome::class.java))
            finish()
        }
        findViewById<View>(R.id.pantryNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityPantry::class.java))
            finish()
        }
        findViewById<View>(R.id.plannerNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityMealPlanner::class.java))
            finish()
        }
        findViewById<View>(R.id.shoppingNav)?.setOnClickListener {
            // Already on shopping
        }
        findViewById<View>(R.id.profileNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityProfile::class.java))
            finish()
        }
    }
}

