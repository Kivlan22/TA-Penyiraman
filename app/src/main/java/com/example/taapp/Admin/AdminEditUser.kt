package com.example.taapp.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.taapp.Admin.AdminDashboard

class AdminEditUser : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var iotCodeInput: TextInputEditText
    private lateinit var statusSwitch: Switch
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    // TextInputLayout references for error handling
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout

    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var userId: String

    // Constants for result handling
    companion object {
        const val EDIT_USER_REQUEST_CODE = 1001
        const val EXTRA_UPDATED_USER = "updated_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_user)

        initializeViews()
        loadUserData()
        setupButtons()
    }

    private fun initializeViews() {
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        iotCodeInput = findViewById(R.id.iotCodeInput)
        statusSwitch = findViewById(R.id.statusSwitch)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Initialize TextInputLayout references
        nameInputLayout = findViewById(R.id.nameInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        phoneInputLayout = findViewById(R.id.phoneInputLayout)
    }

    private fun loadUserData() {
        userId = intent.getStringExtra("userId") ?: ""

        // Validate userId
        if (userId.isEmpty()) {
            Toast.makeText(this, "Error: User ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userName = intent.getStringExtra("userName") ?: ""
        val userEmail = intent.getStringExtra("userEmail") ?: ""
        val userPhone = intent.getStringExtra("userPhone") ?: ""
        val userIotCode = intent.getStringExtra("userIotCode") ?: ""
        val userStatus = intent.getBooleanExtra("userStatus", true)

        nameInput.setText(userName)
        emailInput.setText(userEmail)
        phoneInput.setText(userPhone)
        iotCodeInput.setText(userIotCode)
        statusSwitch.isChecked = userStatus

        // Make IoT code read-only
        iotCodeInput.isFocusable = false
        iotCodeInput.isClickable = false
        iotCodeInput.isCursorVisible = false
    }

    private fun setupButtons() {
        saveButton.setOnClickListener {
            if (validateInputs()) {
                showSaveConfirmationDialog()
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous errors
        clearErrors()

        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()

        // Validate name
        if (name.isEmpty()) {
            nameInputLayout.error = "Name is required"
            isValid = false
        } else if (name.length < 2) {
            nameInputLayout.error = "Name must be at least 2 characters"
            isValid = false
        }

        // Validate email
        if (email.isEmpty()) {
            emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email"
            isValid = false
        }

        // Validate phone
        if (phone.isEmpty()) {
            phoneInputLayout.error = "Phone number is required"
            isValid = false
        } else if (phone.length < 10) {
            phoneInputLayout.error = "Please enter a valid phone number"
            isValid = false
        }

        return isValid
    }

    private fun clearErrors() {
        nameInputLayout.error = null
        emailInputLayout.error = null
        phoneInputLayout.error = null
    }

    private fun showSaveConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Save Changes")
            .setMessage("Are you sure you want to save the changes to this user?")
            .setPositiveButton("Save") { _, _ ->
                saveUserData()
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(true)
            .show()
    }

    private fun saveUserData() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val isActive = statusSwitch.isChecked

        // Disable save button to prevent multiple clicks
        saveButton.isEnabled = false

        // Simulate saving process with a delay
        android.os.Handler().postDelayed({
            // Create updated user object with all necessary data
            val updatedUser = User(
                id = userId,
                name = name,
                email = email,
                phone = phone,
                iotCode = intent.getStringExtra("userIotCode") ?: "",
                role = intent.getStringExtra("userRole") ?: "user",
                lastLogin = intent.getStringExtra("userLastLogin") ?: "",
                isActive = isActive,
                isOnline = intent.getBooleanExtra("userIsOnline", false),
                registrationDate = intent.getStringExtra("userRegistrationDate") ?: "",
                lastSeen = intent.getStringExtra("userLastSeen") ?: ""
            )

            // Show success message
            Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show()

            // Create result intent with updated user data
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_UPDATED_USER, updatedUser)
            setResult(RESULT_OK, resultIntent)

            // Finish current activity to return to AdminDashboard
            finish()

        }, 1000) // 1 second delay to simulate saving process

        // Optional: If you want to use Firebase for real implementation
        /*
        val userUpdates = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "isActive" to isActive,
            "lastUpdated" to System.currentTimeMillis()
        )

        database.child("Users").child(userId).updateChildren(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show()

                val updatedUser = User(
                    id = userId,
                    name = name,
                    email = email,
                    phone = phone,
                    iotCode = intent.getStringExtra("userIotCode") ?: "",
                    role = intent.getStringExtra("userRole") ?: "user",
                    lastLogin = intent.getStringExtra("userLastLogin") ?: "",
                    isActive = isActive,
                    isOnline = intent.getBooleanExtra("userIsOnline", false),
                    registrationDate = intent.getStringExtra("userRegistrationDate") ?: "",
                    lastSeen = intent.getStringExtra("userLastSeen") ?: ""
                )

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_UPDATED_USER, updatedUser)
                setResult(RESULT_OK, resultIntent)

                finish()
            }
            .addOnFailureListener { exception ->
                saveButton.isEnabled = true
                val errorMessage = exception.message ?: "Unknown error occurred"
                Toast.makeText(this, "Failed to update user data: $errorMessage", Toast.LENGTH_LONG).show()
            }
        */
    }
}