package com.example.taapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taapp.Camera.Kamera
import com.example.taapp.Controlling.Controlling
import com.example.taapp.Help.Help
import com.example.taapp.Home.Home
import com.example.taapp.Profile.Profile1
import com.example.taapp.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Home())
        setupBottomNavigation()

        subscribeToFirebaseTopic()
        getFirebaseToken()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.help -> replaceFragment(Help())
                R.id.monitoring -> replaceFragment(Controlling())
                R.id.kamera -> replaceFragment(Kamera())
                R.id.profile -> replaceFragment(Profile1())
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = R.id.home
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment?.javaClass != fragment::class.java) {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
            return true
        }
        return false
    }

    private fun subscribeToFirebaseTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("human-detection")
            .addOnCompleteListener { task ->
                val status = if (task.isSuccessful) "Berhasil" else "Gagal"
                Log.d("FCM", "Subscribe ke topik human-detection: $status")
            }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Token: ${task.result}")
                } else {
                    Log.e("FCM", "Gagal mendapatkan token", task.exception)
                }
            }
    }

    @Deprecated("Use OnBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val btnPositive = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_positive)
        val btnNegative = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_negative)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnPositive.setOnClickListener {
            finishAffinity()
            alertDialog.dismiss()
        }

        btnNegative.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}