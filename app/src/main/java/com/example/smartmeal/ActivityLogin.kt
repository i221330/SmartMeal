package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smartmeal.data.repository.PhpAuthRepository
import com.example.smartmeal.viewmodel.PhpAuthState
import com.example.smartmeal.viewmodel.PhpAuthViewModel
import com.example.smartmeal.viewmodel.PhpAuthViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ActivityLogin : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton

    private val viewModel: PhpAuthViewModel by viewModels {
        PhpAuthViewModelFactory(
            PhpAuthRepository(
                (application as SmartMealApplication).database.userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupObservers()
        setupListeners()

        // Clear any previous auth errors when activity is created
        viewModel.clearError()
    }

    private fun initViews() {
        emailInput = findViewById(R.id.emailEditText)
        passwordInput = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            android.util.Log.d("ActivityLogin", "Auth state changed: $state")

            // Only process if activity is not finishing
            if (isFinishing) return@observe

            when (state) {
                is PhpAuthState.Loading -> {
                    loginButton.isEnabled = false
                    loginButton.text = "Signing in..."
                }
                is PhpAuthState.Authenticated -> {
                    if (!isFinishing) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                        // Mark onboarding as complete for returning users
                        val prefs = getSharedPreferences("smartmeal_prefs", MODE_PRIVATE)
                        prefs.edit().putBoolean("onboarding_complete", true).apply()

                        // Go to home with clear task flags
                        val intent = Intent(this, ActivityHome::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                is PhpAuthState.Error -> {
                    loginButton.isEnabled = true
                    loginButton.text = "Login"
                    android.util.Log.e("ActivityLogin", "Login error: ${state.message}")
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {
                    loginButton.isEnabled = true
                    loginButton.text = "Login"
                }
            }
        }
    }

    private fun setupListeners() {
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateInputs(email, password)) {
                viewModel.signIn(email, password)
            }
        }

        // Handle "Don't have account?" click
        findViewById<TextView>(R.id.signUpTextView)?.setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}