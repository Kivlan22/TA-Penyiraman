package com.example.taapp.LoginRegister

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taapp.R

class AuthActivity : AppCompatActivity() {

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confPass: String
    private lateinit var iotCode: String
    private lateinit var phone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_auth)

        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = findViewById<TextView>(R.id.dialog_message)

        dialogTitle.text = "Authentication Successful!"
        dialogMessage.text = "Your account has been successfully created. Please check your email for verification."

        val resendButton = findViewById<Button>(R.id.resend)
        val loadingIndicator = findViewById<ProgressBar>(R.id.loading_indicator)
        val countdownText = findViewById<TextView>(R.id.dialog_message)

        // Ambil data dari RegisterActivity
        intent?.let {
            name = it.getStringExtra("name") ?: ""
            email = it.getStringExtra("email") ?: ""
            password = it.getStringExtra("password") ?: ""
            confPass = it.getStringExtra("confpass") ?: ""
            iotCode = it.getStringExtra("iotcode") ?: ""
            phone = it.getStringExtra("phone") ?: ""
        }

        resendButton.setOnClickListener {
            resendButton.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            startCountdownTimer(countdownText, loadingIndicator, resendButton)
        }

        val confirmButton = findViewById<Button>(R.id.confirm)
        confirmButton.setOnClickListener {
            val intent = Intent(this, Login1Activity::class.java)
            startActivity(intent)
        }
    }

    private fun startCountdownTimer(countdownText: TextView, loadingIndicator: ProgressBar, resendButton: Button) {
        val countdownTimer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = "Resend available in $secondsLeft seconds"
            }

            override fun onFinish() {
                loadingIndicator.visibility = View.GONE
                countdownText.text = "Please check your SMS OTP for verification."
                resendButton.visibility = View.VISIBLE
            }
        }
        countdownTimer.start()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent().apply {
            putExtra("name", name)
            putExtra("email", email)
            putExtra("password", password)
            putExtra("confpass", confPass)
            putExtra("iotcode", iotCode)
            putExtra("phone", phone)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}
