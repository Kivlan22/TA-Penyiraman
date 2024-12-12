package com.example.taapp.LoginRegister

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var passField: EditText
    private lateinit var confPassField: EditText
    private lateinit var iotcode: EditText
    private lateinit var phoneField: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_register1)

        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Inisialisasi input field
        nameField = findViewById(R.id.nama)
        emailField = findViewById(R.id.email)
        passField = findViewById(R.id.pass)
        confPassField = findViewById(R.id.confpass)
        iotcode = findViewById(R.id.iotcode)
        phoneField = findViewById(R.id.conftelp)

        val signInButton = findViewById<Button>(R.id.signin)
        val loadingIndicator = findViewById<ProgressBar>(R.id.loading_indicator)
        val createText = findViewById<TextView>(R.id.create)

        signInButton.setOnClickListener {
            val isValid = validateFields()

            if (isValid) {
                loadingIndicator.visibility = ProgressBar.VISIBLE

                android.os.Handler().postDelayed({
                    loadingIndicator.visibility = ProgressBar.GONE

                    val intent = Intent(this, AuthActivity::class.java).apply {
                        putExtra("name", nameField.text.toString())
                        putExtra("email", emailField.text.toString())
                        putExtra("password", passField.text.toString())
                        putExtra("confpass", confPassField.text.toString())
                        putExtra("iotcode", iotcode.text.toString())
                        putExtra("phone", phoneField.text.toString())
                    }
                    startActivityForResult(intent, 100)
                }, 3000)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        createText.setOnClickListener {
            val intent = Intent(this, Login1Activity::class.java)
            startActivity(intent)
        }
    }

    private fun validateFields(): Boolean {
        return validateField(nameField, "Name is required") &&
                validateField(emailField, "Email is required") &&
                validateField(passField, "Password is required") &&
                validateField(confPassField, "Confirm Password is required") &&
                validateField(iotcode, "IoT Code is required") &&
                validateField(phoneField, "Phone number is required")
    }

    private fun validateField(field: EditText, errorMessage: String): Boolean {
        return if (field.text.isEmpty()) {
            field.error = errorMessage
            false
        } else {
            field.error = null
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            nameField.setText(data.getStringExtra("name"))
            emailField.setText(data.getStringExtra("email"))
            passField.setText(data.getStringExtra("password"))
            confPassField.setText(data.getStringExtra("confpass"))
            iotcode.setText(data.getStringExtra("iotcode"))
            phoneField.setText(data.getStringExtra("phone"))
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()
    }
}
