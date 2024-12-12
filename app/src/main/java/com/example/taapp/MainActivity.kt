package com.example.taapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taapp.Camera.Kamera
import com.example.taapp.Home.Home
import com.example.taapp.Controlling.Controlling
import com.example.taapp.Profile.Profile
import com.example.taapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        enableEdgeToEdge()
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.help -> replaceFragment(Help())
                R.id.monitoring -> replaceFragment(Controlling())
                R.id.kamera -> replaceFragment(Kamera())
                R.id.profile -> replaceFragment(Profile())
                else -> false
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = R.id.home
    }

    // Helper method to replace fragments
    private fun replaceFragment(fragment: Fragment): Boolean {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frame_layout)

        // Check if the fragment is not already the current one
        if (currentFragment?.javaClass != fragment::class.java) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, fragment)
            fragmentTransaction.addToBackStack(null)  // Add to back stack
            fragmentTransaction.commit()
            return true
        }
        return false
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        // Initialize the view from the layout
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val btnPositive = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_positive)
        val btnNegative = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_negative)

        // Create the dialog
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Action for "Yes" button
        btnPositive.setOnClickListener {
            finishAffinity() // Close all activities and exit the app
            alertDialog.dismiss()
        }

        // Action for "No" button
        btnNegative.setOnClickListener {
            alertDialog.dismiss() // Dismiss the dialog
        }

        // Show the dialog
        alertDialog.show()
    }
}
