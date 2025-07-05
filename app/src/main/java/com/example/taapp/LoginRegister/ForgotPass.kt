package com.example.taapp.LoginRegister

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPass : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var pass1EditText: TextInputEditText
    private lateinit var pass2EditText: TextInputEditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_forget)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.email)
        pass1EditText = findViewById(R.id.newpass1)
        pass2EditText = findViewById(R.id.newpass2)

        val updateButton = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.update)

        updateButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val pass1 = pass1EditText.text.toString().trim()
            val pass2 = pass2EditText.text.toString().trim()

            if (email.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass1.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null && user.email == email) {
                // User sudah login & email cocok, langsung update password
                user.updatePassword(pass1)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password berhasil diperbarui", Toast.LENGTH_LONG).show()
                            finish() // atau arahkan kembali ke login
                        } else {
                            Toast.makeText(this, "Gagal update password: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Silakan login terlebih dahulu untuk mengubah password", Toast.LENGTH_LONG).show()
                // Bisa diarahkan ke halaman login
            }
        }
    }
}
