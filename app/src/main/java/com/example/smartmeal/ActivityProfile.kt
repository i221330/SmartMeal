package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smartmeal.data.repository.PhpAuthRepository
import com.example.smartmeal.viewmodel.PhpAuthViewModel
import com.example.smartmeal.viewmodel.PhpAuthViewModelFactory
import com.google.android.material.button.MaterialButton

class ActivityProfile : AppCompatActivity() {

    private val viewModel: PhpAuthViewModel by viewModels {
        PhpAuthViewModelFactory(
            PhpAuthRepository(
                (application as SmartMealApplication).database.userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupListeners()
        setupBottomNavigation()
    }

    private fun setupListeners() {
        findViewById<MaterialButton>(R.id.logoutButton)?.setOnClickListener {
            viewModel.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ActivityLogin::class.java))
            finishAffinity() // Clear all activities
        }
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
            startActivity(Intent(this, ActivityShoppingList::class.java))
            finish()
        }
        findViewById<View>(R.id.profileNav)?.setOnClickListener {
            // Already on profile
        }
    }
}