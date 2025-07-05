package com.example.taapp.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity(), AuthDialogFragment.AuthDialogListener {

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
    private lateinit var Auth: TextView

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

        auth = FirebaseAuth.getInstance()

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confPasswordInput = findViewById(R.id.confPasswordInput)
        iotCodeInput = findViewById(R.id.iotCodeInput)
        phoneInput = findViewById(R.id.confPhone)
        registerButton = findViewById(R.id.registerButton)
        signInText = findViewById(R.id.signInText)
        Auth = findViewById(R.id.Auth)

        signInText.setOnClickListener {
            startActivity(Intent(this, Login1Activity::class.java))
            finish()
        }

        Auth.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confPassword = confPasswordInput.text.toString().trim()
            val iotCode = iotCodeInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()

            val dialog = AuthDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                    putString("email", email)
                    putString("password", password)
                    putString("confpass", confPassword)
                    putString("iotcode", iotCode)
                    putString("phone", phone)
                }
            }
            dialog.show(supportFragmentManager, "AuthDialog")
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
                sendOtpToPhone(phone)
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
                    val userMap = hashMapOf(
                        "id" to userId,
                        "name" to name,
                        "email" to email,
                        "iotCode" to iotCode,
                        "phone" to phone
                    )

                    // Menyimpan data pengguna ke Realtime Database
                    database.child("Users").child(userId).setValue(userMap).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                            Log.d("RegisterActivity", "Data saved to Firebase")

                            // Kirim OTP setelah registrasi
                            sendOtpToPhone(phone)
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

    private fun sendOtpToPhone(rawPhone: String) {
        val formattedPhone = when {
            rawPhone.startsWith("+62") -> rawPhone // Already in correct format
            rawPhone.startsWith("62") -> "+$rawPhone" // Add "+" prefix
            rawPhone.startsWith("0") -> "+62" + rawPhone.substring(1) // Convert "0" to "+62"
            else -> {
                Toast.makeText(this, "Format nomor tidak dikenali", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (formattedPhone.isEmpty()) {
            Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }


        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("RegisterActivity", "Verification completed: $credential")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("RegisterActivity", "Verification failed: ${e.message}", e)
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Log.e("RegisterActivity", "Invalid request")
                    } else if (e is FirebaseTooManyRequestsException) {
                        Log.e("RegisterActivity", "SMS quota exceeded")
                    }
                    Toast.makeText(this@RegisterActivity, "OTP verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d("RegisterActivity", "Code sent to: $formattedPhone, verificationId=$verificationId")

                    // Simpan verificationId untuk verifikasi kode OTP
                    val dialog = AuthDialogFragment().apply {
                        arguments = Bundle().apply {
                            putString("verificationId", verificationId)
                            putString("phone", formattedPhone)
                        }
                    }
                    dialog.show(supportFragmentManager, "OtpDialog")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun resendOtp(phone: String) {
        sendOtpToPhone(phone)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, StartActivity::class.java))
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
