package com.example.taapp.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.widget.Button
import android.widget.EditText

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

        return true
    }

    private fun registerUser(name: String, email: String, password: String, iotCode: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val userMap = hashMapOf(
                        "id" to userId,
                        "name" to name,
                        "email" to email,
                        "iotCode" to iotCode,
                        "phone" to phone
                    )

                    database.child("Users").child(userId).setValue(userMap).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            // Show success toast
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                            // Start AuthActivity after successful registration
                            val intent = Intent(this, AuthActivity::class.java)
                            startActivity(intent)

                            // Optionally, finish the current activity to prevent going back to it
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
