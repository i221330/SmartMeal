package com.example.smartmeal

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.smartmeal.data.model.User
import com.example.smartmeal.data.repository.PhpAuthRepository
import com.example.smartmeal.viewmodel.PhpAuthViewModel
import com.example.smartmeal.viewmodel.PhpAuthViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActivityProfile : AppCompatActivity() {

    private val TAG = "ActivityProfile"

    private val viewModel: PhpAuthViewModel by viewModels {
        PhpAuthViewModelFactory(
            PhpAuthRepository(
                (application as SmartMealApplication).database.userDao()
            )
        )
    }

    private lateinit var repository: PhpAuthRepository
    private lateinit var sharedPreferences: SharedPreferences

    // UI Elements
    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var memberTextView: TextView
    private lateinit var joinedTextView: TextView
    private lateinit var notificationsSwitch: SwitchMaterial

    private var currentUser: User? = null
    private var selectedImageUri: Uri? = null

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                loadProfileImage(uri.toString())
                saveProfileImageUri(uri.toString())
                Toast.makeText(this, R.string.profile_picture_updated, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        repository = PhpAuthRepository(
            (application as SmartMealApplication).database.userDao()
        )

        sharedPreferences = getSharedPreferences("SmartMealPrefs", MODE_PRIVATE)

        initializeViews()
        setupListeners()
        setupBottomNavigation()
        loadUserData()
        loadNotificationPreference()
    }

    private fun initializeViews() {
        profileImageView = findViewById(R.id.profileImageView)
        nameTextView = findViewById(R.id.nameTextView)
        memberTextView = findViewById(R.id.memberTextView)
        joinedTextView = findViewById(R.id.joinedTextView)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
    }

    private fun setupListeners() {
        // Logout button
        findViewById<MaterialButton>(R.id.logoutButton)?.setOnClickListener {
            showLogoutConfirmation()
        }

        // Settings button - Edit name
        findViewById<View>(R.id.settingsButton)?.setOnClickListener {
            showEditNameDialog()
        }

        // Profile picture click - view full size or change
        profileImageView.setOnClickListener {
            showProfilePictureOptions()
        }

        // Notifications toggle
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference(isChecked)
            val message = if (isChecked) "Notifications enabled" else "Notifications disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                currentUser = repository.getCurrentUser()

                if (currentUser != null) {
                    updateUI(currentUser!!)
                } else {
                    Log.w(TAG, "No user data found in database")
                    // Show default/placeholder data
                    showDefaultProfile()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user data", e)
                showDefaultProfile()
            }
        }
    }

    private fun updateUI(user: User) {
        // Display name
        nameTextView.text = user.displayName ?: user.email ?: "User"

        // Member status
        if (user.isPremium) {
            memberTextView.text = getString(R.string.premium_member)
            memberTextView.visibility = View.VISIBLE
        } else {
            memberTextView.text = "Free Member"
            memberTextView.visibility = View.VISIBLE
        }

        // Joined date
        val joinedYear = formatJoinedDate(user.joinedDate)
        joinedTextView.text = "Joined $joinedYear"

        // Profile image
        val savedImageUri = sharedPreferences.getString("profile_image_uri", null)
        if (savedImageUri != null) {
            loadProfileImage(savedImageUri)
        } else if (!user.profileImageUrl.isNullOrEmpty()) {
            loadProfileImage(user.profileImageUrl)
        } else {
            // Use default avatar
            profileImageView.setImageResource(R.drawable.ic_profile_default)
        }
    }

    private fun showDefaultProfile() {
        nameTextView.text = "Guest User"
        memberTextView.text = "Free Member"
        joinedTextView.text = "Joined 2025"
        profileImageView.setImageResource(R.drawable.ic_profile_default)
    }

    private fun formatJoinedDate(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            val format = SimpleDateFormat("yyyy", Locale.getDefault())
            format.format(date)
        } catch (e: Exception) {
            "2025"
        }
    }

    private fun loadProfileImage(imageUrl: String) {
        try {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_default)
                .error(R.drawable.ic_profile_default)
                .circleCrop()
                .into(profileImageView)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile image", e)
            profileImageView.setImageResource(R.drawable.ic_profile_default)
        }
    }

    private fun showProfilePictureOptions() {
        val options = arrayOf("View full size", "Change picture", "Remove picture")

        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewFullSizeImage()
                    1 -> openImagePicker()
                    2 -> removeProfilePicture()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun viewFullSizeImage() {
        // Simple implementation - show toast for now
        // Can be enhanced to open full screen image viewer
        Toast.makeText(this, "Full size view coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun openImagePicker() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening image picker", e)
            Toast.makeText(this, "Unable to open image picker", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProfilePicture() {
        profileImageView.setImageResource(R.drawable.ic_profile_default)
        sharedPreferences.edit().remove("profile_image_uri").apply()
        Toast.makeText(this, "Profile picture removed", Toast.LENGTH_SHORT).show()
    }

    private fun saveProfileImageUri(uri: String) {
        sharedPreferences.edit().putString("profile_image_uri", uri).apply()
    }

    private fun loadNotificationPreference() {
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        notificationsSwitch.isChecked = notificationsEnabled
    }

    private fun saveNotificationPreference(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                viewModel.signOut()

                // Clear saved preferences
                sharedPreferences.edit().clear().apply()

                Toast.makeText(this@ActivityProfile, "Logged out successfully", Toast.LENGTH_SHORT).show()

                // Navigate to login
                val intent = Intent(this@ActivityProfile, ActivityLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finishAffinity()
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
                Toast.makeText(this@ActivityProfile, "Error logging out", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditNameDialog() {
        val editText = android.widget.EditText(this).apply {
            setText(currentUser?.displayName ?: nameTextView.text.toString())
            hint = "Enter your name"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setPadding(50, 40, 50, 40)
        }

        AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Edit Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    updateUserName(newName)
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUserName(newName: String) {
        lifecycleScope.launch {
            try {
                currentUser?.let { user ->
                    val updatedUser = user.copy(displayName = newName)
                    repository.userDao().updateUser(updatedUser)
                    currentUser = updatedUser
                    nameTextView.text = newName
                    Toast.makeText(this@ActivityProfile, "Name updated successfully", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "User name updated to: $newName")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating user name", e)
                Toast.makeText(this@ActivityProfile, "Error updating name", Toast.LENGTH_SHORT).show()
            }
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