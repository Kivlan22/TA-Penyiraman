package com.example.taapp.LoginRegister

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.MainActivity
import com.example.taapp.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class Login1Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_login1)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get references to the EditTexts and buttons
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.pass)
        val signUpButton = findViewById<Button>(R.id.signup)
        val createText = findViewById<TextView>(R.id.create) // Assuming the ID is 'create'

        // Set onClickListener for the Sign In button (Login)
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if both email and password fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Show the "Remember Me" dialog
                showRememberDialog(email, password)
            } else {
                // Validate empty fields
                if (email.isEmpty()) {
                    emailEditText.error = "Email is required"
                }
                if (password.isEmpty()) {
                    passwordEditText.error = "Password is required"
                }
            }
        }

        // Set onClickListener for the "Create" button to navigate to RegisterActivity
        createText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java) // Navigate to RegisterActivity
            startActivity(intent)
        }
    }

    // Function to show the "Remember Me" dialog
    private fun showRememberDialog(email: String, password: String) {
        val dialogView = layoutInflater.inflate(R.layout.custom_remember, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = builder.create()

        val btnYes = dialogView.findViewById<MaterialButton>(R.id.btn_positive)
        val btnNo = dialogView.findViewById<MaterialButton>(R.id.btn_negative)

        // Action for "Yes" button
        btnYes.setOnClickListener {
            showLoading() // Show loading spinner
            saveAccountInfo(email, password) // Save account info if Yes

            // Check if there is an active internet connection
            if (isInternetAvailable()) {
                // If internet is available, proceed with login
                loginUser(email, password)
                dialog.dismiss() // Close dialog
            } else {
                // If no internet connection, show a message
                Toast.makeText(this, "No internet connection available.", Toast.LENGTH_SHORT).show()
                hideLoading() // Hide loading spinner
                dialog.dismiss() // Close dialog
            }
        }

        // Action for "No" button
        btnNo.setOnClickListener {
            loginUser(email, password) // Proceed with login without saving account
            dialog.dismiss() // Close dialog
        }

        dialog.show() // Show dialog
    }

    // Function to save account info to SharedPreferences
    private fun saveAccountInfo(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save email and password (you might want to encrypt password for security)
        editor.putString("saved_email", email)
        editor.putString("saved_password", password) // In practice, store securely
        editor.apply()
    }

    // Function to login the user using Firebase Authentication
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If login is successful, navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close the current activity so user cannot go back
                } else {
                    // If login fails, show a toast with the error message
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Check for saved credentials and automatically log in if available
    override fun onStart() {
        super.onStart()

        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("saved_email", null)
        val savedPassword = sharedPreferences.getString("saved_password", null)

        // If saved email and password exist, login automatically
        if (savedEmail != null && savedPassword != null) {
            loginUser(savedEmail, savedPassword)
        }
    }

    // Override the back button to navigate to StartActivity
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, StartActivity::class.java) // Navigate to StartActivity
        startActivity(intent)
        finish() // Close Login1Activity to prevent the user from going back to it

        // Animation for slide transition
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Function to check if the device is connected to the internet
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Function to show the loading spinner
    private fun showLoading() {
        val loadingLayout = findViewById<FrameLayout>(R.id.loadingLayout)
        loadingLayout.visibility = View.VISIBLE
    }

    // Function to hide the loading spinner
    private fun hideLoading() {
        val loadingLayout = findViewById<FrameLayout>(R.id.loadingLayout)
        loadingLayout.visibility = View.GONE
    }
}
