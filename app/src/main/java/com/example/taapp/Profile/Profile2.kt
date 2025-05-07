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
        setContentView(R.layout.fragment_profile2) // Ubah jika nama layout-nya berbeda

        auth = FirebaseAuth.getInstance()

        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val confPasswordInput = findViewById<TextInputEditText>(R.id.confPasswordInput)
        val iotCodeInput = findViewById<TextInputEditText>(R.id.iotCodeInput)
        val phoneInput = findViewById<TextInputEditText>(R.id.confPhone)

        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null) // Tambahkan ini jika kamu simpan password
        val confPassword = sharedPreferences.getString("confPassword", null) // Sama juga
        val iotCode = sharedPreferences.getString("iotCode", null)
        val phone = sharedPreferences.getString("phone", null)

        // Cek dan set data dari SharedPreferences jika ada
        if (name != null && email != null && phone != null && iotCode != null) {
            nameInput.setText(name)
            emailInput.setText(email)
            iotCodeInput.setText(iotCode)
            phoneInput.setText(phone)

            passwordInput.setText(password ?: "********")
            confPasswordInput.setText(confPassword ?: "********")
        } else {
            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid
                database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val nameFromFirebase = snapshot.child("name").value.toString()
                        val emailFromFirebase = snapshot.child("email").value.toString()
                        val phoneFromFirebase = snapshot.child("phone").value.toString()
                        val iotCodeFromFirebase = snapshot.child("iotCode").value.toString()

                        // Menyimpan data ke SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("name", nameFromFirebase)
                        editor.putString("email", emailFromFirebase)
                        editor.putString("phone", phoneFromFirebase)
                        editor.putString("iotCode", iotCodeFromFirebase)
                        editor.apply()

                        // Menampilkan data ke input fields
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

    // Fungsi untuk menampilkan dialog konfirmasi sebelum menyimpan perubahan
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
            // Aksi ketika "Yes" ditekan
            alertDialog.dismiss()
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()

            // Ambil data dari input fields dan update di Firebase
            updateUserData()
        }

        btnNo.setOnClickListener {
            // Menutup dialog jika "No" ditekan
            alertDialog.dismiss()
        }
    }

    // Fungsi untuk menyimpan data yang diubah ke Firebase
    private fun updateUserData() {
        val user = auth.currentUser
        if (user != null) {
            val nameInput = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val emailInput = findViewById<TextInputEditText>(R.id.emailInput).text.toString()
            val phoneInput = findViewById<TextInputEditText>(R.id.confPhone).text.toString()

            // Update data ke Firebase Authentication (untuk email)
            if (emailInput != user.email) {
                user.updateEmail(emailInput)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            // Update data di Firebase Database (untuk nama, telepon, dll)
            val userId = user.uid
            val userUpdates = mapOf(
                "name" to nameInput,
                "email" to emailInput, // Meskipun email sudah di-update di Authentication, kita juga perlu meng-update di Database
                "phone" to phoneInput
            )

            // Perbarui data pengguna di Firebase Database
            database.child("Users").child(userId).updateChildren(userUpdates).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Data successfully updated in Firebase", Toast.LENGTH_SHORT).show()

                    // Perbarui SharedPreferences setelah update berhasil
                    val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("name", nameInput)
                    editor.putString("email", emailInput)
                    editor.putString("phone", phoneInput)
                    editor.apply()
                } else {
                    Toast.makeText(this, "Failed to update data in Firebase: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
