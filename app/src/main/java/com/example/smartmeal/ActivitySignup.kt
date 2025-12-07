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

class ActivitySignup : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var signupButton: MaterialButton

    private val viewModel: PhpAuthViewModel by viewModels {
        PhpAuthViewModelFactory(
            PhpAuthRepository(
                (application as SmartMealApplication).database.userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initViews()
        setupObservers()
        setupListeners()

        // Clear any previous auth errors when activity is created
        viewModel.clearError()
    }

    private fun initViews() {
        nameInput = findViewById(R.id.nameEditText)
        emailInput = findViewById(R.id.emailEditText)
        passwordInput = findViewById(R.id.passwordEditText)
        confirmPasswordInput = findViewById(R.id.confirmPasswordEditText)
        signupButton = findViewById(R.id.createAccountButton)
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            android.util.Log.d("ActivitySignup", "Auth state changed: $state")

            // Only process if activity is not finishing
            if (isFinishing) return@observe

            when (state) {
                is PhpAuthState.Loading -> {
                    signupButton.isEnabled = false
                    signupButton.text = "Creating account..."
                }
                is PhpAuthState.Authenticated -> {
                    // Only proceed if we're still in this activity
                    if (!isFinishing) {
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                        try {
                            val intent = Intent(this, ActivityOnboarding1::class.java)
                            // Clear any previous activities
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            android.util.Log.e("ActivitySignup", "Error navigating to onboarding", e)
                            Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                is PhpAuthState.Error -> {
                    signupButton.isEnabled = true
                    signupButton.text = "Sign Up"
                    android.util.Log.e("ActivitySignup", "Signup error: ${state.message}")
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {
                    signupButton.isEnabled = true
                    signupButton.text = "Sign Up"
                }
            }
        }
    }

    private fun setupListeners() {
        signupButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            android.util.Log.d("ActivitySignup", "Signup button clicked - Name: $name, Email: $email")

            if (validateInputs(name, email, password, confirmPassword)) {
                try {
                    android.util.Log.d("ActivitySignup", "Calling viewModel.signUp")
                    viewModel.signUp(email, password, name)
                } catch (e: Exception) {
                    android.util.Log.e("ActivitySignup", "Exception during signup", e)
                    Toast.makeText(this, "Signup error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Handle "Already have account?" click
        findViewById<TextView>(R.id.signInTextView)?.setOnClickListener {
            finish() // Go back to login
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}