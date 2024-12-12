package com.example.taapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taapp.LoginRegister.StartActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkInternetAndProceed()
    }

    private fun checkInternetAndProceed() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isInternetAvailable()) {
                goToMainActivity()
            } else {
                Toast.makeText(
                    this,
                    "Tidak ada koneksi internet. Silakan periksa jaringan Anda.",
                    Toast.LENGTH_LONG
                ).show()
                checkInternetAndProceed()  // Coba lagi setelah beberapa saat
            }
        }, 3000L)
    }

    private fun goToMainActivity() {
        Intent(this, StartActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
