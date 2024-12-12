package com.example.taapp.LoginRegister

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.MainActivity
import com.example.taapp.R

class Login1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_login1)

        // Get references to the EditTexts and buttons
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.pass)
        val signUpButton = findViewById<Button>(R.id.signup)
        val createText = findViewById<TextView>(R.id.create) // Assuming the ID is 'create'

        // Set onClickListener for the Sign Up button
        signUpButton.setOnClickListener {
            // Get the input text from email and password fields
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if both email and password fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // If both fields are filled, proceed to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the current activity so user cannot go back
            } else {
                // Optionally show a message or error if fields are empty
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

    // Override the back button to navigate to LoginActivity
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, StartActivity::class.java) // Navigate to LoginActivity
        startActivity(intent)
        finish() // Close Login1Activity to prevent the user from going back to it
    }
}
