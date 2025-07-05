package com.example.taapp.LoginRegister

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.taapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class AuthDialogFragment : DialogFragment() {

    interface AuthDialogListener {
        fun resendOtp(phone: String)
    }

    private var listener: AuthDialogListener? = null

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confPass: String
    private lateinit var iotCode: String
    private lateinit var phone: String
    private lateinit var verificationId: String

    private lateinit var otpInput: EditText
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var countdownText: TextView
    private lateinit var resendButton: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthDialogListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.activity_start_auth, null)

        otpInput = view.findViewById(R.id.otp_input)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        countdownText = view.findViewById(R.id.countdown_text)
        resendButton = view.findViewById(R.id.resend)

        val confirmButton = view.findViewById<Button>(R.id.confirm)

        arguments?.let {
            name = it.getString("name", "")
            email = it.getString("email", "")
            password = it.getString("password", "")
            confPass = it.getString("confpass", "")
            iotCode = it.getString("iotcode", "")
            phone = it.getString("phone", "")
            verificationId = it.getString("verificationId", "") // get verificationId from arguments
        }

        resendButton.setOnClickListener {
            resendButton.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            listener?.resendOtp(phone)
            startCountdownTimer(countdownText, loadingIndicator, resendButton)
        }

        confirmButton.setOnClickListener {
            val otp = otpInput.text.toString().trim()

            if (otp.isNotEmpty()) {
                verifyOtp(otp)
            } else {
                Toast.makeText(requireContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()
    }

    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // OTP verified, proceed with further actions
                    Toast.makeText(requireContext(), "Verification Successful", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    // OTP failed, show error
                    Toast.makeText(requireContext(), "Verification Failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startCountdownTimer(countdownText: TextView, loadingIndicator: ProgressBar, resendButton: Button) {
        object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = "Resend available in $secondsLeft seconds"
            }

            override fun onFinish() {
                loadingIndicator.visibility = View.GONE
                countdownText.text = "You can resend OTP now."
                resendButton.visibility = View.VISIBLE
            }
        }.start()
    }
}

