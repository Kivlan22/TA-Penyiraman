package com.example.taapp.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confPasswordInput: EditText
    private lateinit var iotCodeInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var registerButton: Button
    private lateinit var signInText: TextView

    // Daftar kode IoT yang valid
    private val validIotCodes = listOf(
        "IoT123456", "IoT234567", "IoT345678", "IoT456789", "IoT567890", "IoT678901",
        "IoT789012", "IoT890123", "IoT901234", "IoT012345", "IoT135792", "IoT246813",
        "IoT357924", "IoT468135", "IoT579246", "IoT680357", "IoT791468", "IoT802579",
        "IoT913680", "IoT024791", "IoT135680", "IoT246791", "IoT357802", "IoT468913",
        "IoT579024"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_register1)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize UI elements
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confPasswordInput = findViewById(R.id.confPasswordInput)
        iotCodeInput = findViewById(R.id.iotCodeInput)
        phoneInput = findViewById(R.id.confPhone)
        registerButton = findViewById(R.id.registerButton)
        signInText = findViewById(R.id.signInText)

        // Set listener for the sign-in text
        signInText.setOnClickListener {
            val intent = Intent(this, Login1Activity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confPassword = confPasswordInput.text.toString().trim()
            val iotCode = iotCodeInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()

            if (validateInput(name, email, password, confPassword, iotCode, phone)) {
                registerUser(name, email, password, iotCode, phone)
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String, confPassword: String, iotCode: String, phone: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confPassword.isEmpty() || iotCode.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validasi kode IoT
        if (!validIotCodes.contains(iotCode)) {
            Toast.makeText(this, "Invalid IoT code", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(name: String, email: String, password: String, iotCode: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    Log.d("RegisterActivity", "User ID: $userId")

                    val userMap = hashMapOf(
                        "id" to userId,
                        "name" to name,
                        "email" to email,
                        "iotCode" to iotCode,
                        "phone" to phone
                    )
                    Log.d("RegisterActivity", "User Map: $userMap")

                    // Store user data in Firebase Realtime Database
                    database.child("Users").child(userId).setValue(userMap).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                            Log.d("RegisterActivity", "Data saved successfully. Moving to Login1Activity")

                            // Navigate to Login1Activity after successful registration
                            val intent = Intent(this, Login1Activity::class.java)
                            startActivity(intent)
                            finish() // Close RegisterActivity
                        } else {
                            Toast.makeText(this, "Failed to save data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.e("RegisterActivity", "Failed to save data: ${dbTask.exception?.message}")
                        }
                    }
                } else {
                    Log.e("RegisterActivity", "User ID is null after authentication.")
                }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegisterActivity", "Registration failed: ${task.exception?.message}")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()  // Close RegisterActivity

        // Menambahkan animasi slide ke kanan
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}

