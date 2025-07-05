package com.example.taapp.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.textfield.TextInputEditText
import android.app.AlertDialog

class Profile2 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile2)

        auth = FirebaseAuth.getInstance()

        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val confPasswordInput = findViewById<TextInputEditText>(R.id.confPasswordInput)
        val iotCodeInput = findViewById<TextInputEditText>(R.id.iotCodeInput)
        val phoneInput = findViewById<TextInputEditText>(R.id.confPhone)

        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            // Ambil data pengguna dari Firebase Realtime Database
            database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val nameFromFirebase = snapshot.child("name").value.toString()
                    val emailFromFirebase = snapshot.child("email").value.toString()
                    val phoneFromFirebase = snapshot.child("phone").value.toString()
                    val iotCodeFromFirebase = snapshot.child("iotCode").value.toString()

                    // Menampilkan data dari Firebase ke input fields
                    nameInput.setText(nameFromFirebase)
                    emailInput.setText(emailFromFirebase)
                    iotCodeInput.setText(iotCodeFromFirebase)
                    phoneInput.setText(phoneFromFirebase)

                    passwordInput.setText("********")
                    confPasswordInput.setText("********")
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Buat semua input bisa diedit, kecuali iotCodeInput
        listOf(nameInput, emailInput, passwordInput, confPasswordInput, phoneInput).forEach {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
            it.isClickable = true
        }

        // Khusus iotCodeInput tidak bisa diedit
        iotCodeInput.isFocusable = false
        iotCodeInput.isClickable = false

        // Setup Edit Button
        val editButton = findViewById<Button>(R.id.edit)
        editButton.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.custom_submit_edit, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val btnYes = dialogView.findViewById<Button>(R.id.btn_positive)
        val btnNo = dialogView.findViewById<Button>(R.id.btn_negative)

        btnYes.setOnClickListener {
            alertDialog.dismiss()
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            updateUserData()
        }

        btnNo.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun updateUserData() {
        val user = auth.currentUser
        if (user != null) {
            val nameInput = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val emailInput = findViewById<TextInputEditText>(R.id.emailInput).text.toString()
            val phoneInput = findViewById<TextInputEditText>(R.id.confPhone).text.toString()

            val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput).text.toString()
            val confPasswordInput = findViewById<TextInputEditText>(R.id.confPasswordInput).text.toString()

            if (passwordInput.isNotEmpty() || confPasswordInput.isNotEmpty()) {
                if (passwordInput == confPasswordInput) {
                    user.updatePassword(passwordInput)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }

            if (nameInput.isNotBlank() && emailInput.isNotBlank() && phoneInput.isNotBlank()) {
                val userId = user.uid
                val userUpdates = mapOf(
                    "name" to nameInput,
                    "email" to emailInput,
                    "phone" to phoneInput
                )

                database.child("Users").child(userId).updateChildren(userUpdates).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
