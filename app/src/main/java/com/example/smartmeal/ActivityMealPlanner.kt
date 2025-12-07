package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActivityMealPlanner : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        // TODO: Implement meal planner functionality later
        Toast.makeText(this, "Meal Planner - Coming Soon!", Toast.LENGTH_SHORT).show()

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
            // Already on planner
        }
        findViewById<View>(R.id.shoppingNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityShoppingList::class.java))
            finish()
        }
        findViewById<View>(R.id.profileNav)?.setOnClickListener {
            startActivity(Intent(this, ActivityProfile::class.java))
            finish()
        }
    }
}

