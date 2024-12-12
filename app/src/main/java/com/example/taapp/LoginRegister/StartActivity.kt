package com.example.taapp.LoginRegister

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R

    class StartActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_start)

            // Referensi tombol
            val registerButton = findViewById<Button>(R.id.register)
            val loginButton = findViewById<Button>(R.id.login)

            // Navigasi ke RegisterActivity
            registerButton.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            // Navigasi ke Login1Activity
            loginButton.setOnClickListener {
                val intent = Intent(this, Login1Activity::class.java)
                startActivity(intent)
            }
        }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        // Inisialisasi view dari layout
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val btnPositive = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_positive)
        val btnNegative = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_negative)

        // Buat dialog
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Aksi tombol Yes
        btnPositive.setOnClickListener {
            finishAffinity()  // Menutup semua activity dan keluar dari aplikasi
            alertDialog.dismiss()
        }

        // Aksi tombol No
        btnNegative.setOnClickListener {
            alertDialog.dismiss()  // Tutup dialog
        }

        // Tampilkan dialog
        alertDialog.show()
    }
}
