package com.example.taapp.Home

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taapp.R
import com.example.tugas2_mobile_kivlanhakeemarrouf_1103213073_tk4506.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : Fragment() {

    private lateinit var kelembabanTextView1: TextView // Kelembaban Sensor 1
    private lateinit var kelembabanTextView2: TextView // Kelembaban Sensor 2
    private lateinit var kelembabanTextView3: TextView // Kelembaban Avg Sensor 3
    private lateinit var toggleSwitch: Switch // Switch untuk menyalakan/mematikan API
    private val handler = Handler() // Handler untuk menangani pembaruan berulang
    private val interval: Long = 500 // Interval waktu dalam milidetik (5 detik)
    private var isRunning = false // Flag untuk menghentikan atau melanjutkan simulasi

    private var activeToast: Toast? = null // Variabel untuk menyimpan referensi Toast yang aktif
    private var lastErrorTime: Long = 0 // Waktu terakhir pesan kesalahan ditampilkan

    private var apiCallInProgress = false // Flag untuk mengecek apakah API sedang dipanggil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi TextView untuk menampilkan kelembaban
        kelembabanTextView1 = view.findViewById(R.id.kelembaban1)
        kelembabanTextView2 = view.findViewById(R.id.kelembaban2)
        kelembabanTextView3 = view.findViewById(R.id.kelembaban3)

        // Inisialisasi Switch untuk mengontrol pemanggilan API
        toggleSwitch = view.findViewById(R.id.toggleSwitch)

        // Set listener untuk switch
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startRefreshingData() // Mulai mengambil data API jika switch ON
            } else {
                stopRefreshingData() // Hentikan pengambilan data API jika switch OFF
            }
        }

        return view
    }

    // Fungsi untuk mulai mengambil data kelembaban setiap interval
    private fun startRefreshingData() {
        isRunning = true // Tandakan bahwa pemanggilan API sedang berjalan
        apiCallInProgress = true // Tandakan bahwa API sedang dipanggil
        val runnable = object : Runnable {
            override fun run() {
                if (isRunning && apiCallInProgress) {
                    getSensor1Data() // Ambil data kelembaban Sensor 1
                    getSensor2Data() // Ambil data kelembaban Sensor 2
                    getAvgSensor3Data() // Ambil data kelembaban Avg Sensor 3
                    handler.postDelayed(this, interval) // Menjalankan lagi setelah interval
                }
            }
        }
        handler.post(runnable) // Menjalankan tugas pertama
    }

    // Fungsi untuk menghentikan pemanggilan API dan reset TextView
    private fun stopRefreshingData() {
        isRunning = false // Tandakan bahwa pemanggilan API dihentikan
        apiCallInProgress = false // Tandakan bahwa pemanggilan API dihentikan
        handler.removeCallbacksAndMessages(null) // Hentikan semua tugas yang tertunda
    }

    // Fungsi untuk mengambil data kelembaban Sensor 1 dari API
    private fun getSensor1Data() {
        RetrofitClient.apiServiceSensor1.getSensor1Humidity().enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    val humidity = response.body()
                    if (humidity != null) {
                        animateHumidityChange(kelembabanTextView1, humidity) // Animasi perubahan kelembaban
                        updateKelembabanTextView(kelembabanTextView1, humidity) // Update kelembaban TextView
                    } else {
                        showError("Data tidak valid untuk Sensor 1")
                    }
                } else {
                    showError("Failed to get data for Sensor 1, Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                showError("Error: ${t.message} for Sensor 1")
            }
        })
    }

    // Fungsi untuk mengambil data kelembaban Sensor 2 dari API
    private fun getSensor2Data() {
        RetrofitClient.apiServiceSensor2.getSensor2Humidity().enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    val humidity = response.body()
                    if (humidity != null) {
                        animateHumidityChange(kelembabanTextView2, humidity) // Animasi perubahan kelembaban
                        updateKelembabanTextView(kelembabanTextView2, humidity) // Update kelembaban TextView
                    } else {
                        showError("Data tidak valid untuk Sensor 2")
                    }
                } else {
                    showError("Failed to get data for Sensor 2, Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                showError("Error: ${t.message} for Sensor 2")
            }
        })
    }

    // Fungsi untuk mengambil data kelembaban Avg Sensor 3 dari API
    private fun getAvgSensor3Data() {
        RetrofitClient.apiServiceSensor3.getAvgSensorHumidity().enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    val humidity = response.body()
                    if (humidity != null) {
                        animateHumidityChange(kelembabanTextView3, humidity) // Animasi perubahan kelembaban
                        updateKelembabanTextView(kelembabanTextView3, humidity) // Update kelembaban TextView
                    } else {
                        showError("Data tidak valid untuk Avg Sensor 3")
                    }
                } else {
                    showError("Failed to get data for Avg Sensor 3, Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                showError("Error: ${t.message} for Avg Sensor 3")
            }
        })
    }

    // Fungsi untuk animasi perubahan kelembaban
    private fun animateHumidityChange(textView: TextView, humidity: Double) {
        val currentText = textView.text.toString()
        // Ambil nilai kelembaban yang sudah ditampilkan sebelumnya
        val currentHumidity = currentText.substringBefore("%").toDoubleOrNull()

        // Jika currentHumidity null atau tidak valid, gunakan nilai kelembaban baru
        val startHumidity = currentHumidity ?: humidity

        // Lakukan animasi perubahan dari startHumidity ke humidity baru
        val animator = ValueAnimator.ofFloat(startHumidity.toFloat(), humidity.toFloat())
        animator.duration = 500 // Durasi animasi dalam milidetik
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            textView.text = String.format("%.2f%%", animatedValue)  // Update kelembaban
        }
        animator.start() // Mulai animasi
    }

    // Fungsi untuk memperbarui TextView kelembaban
    private fun updateKelembabanTextView(textView: TextView, humidity: Double) {
        textView.text = String.format("%.2f%%", humidity)  // Update kelembaban TextView
    }

    // Fungsi untuk menampilkan pesan kesalahan
    private fun showError(message: String) {
        val currentTime = System.currentTimeMillis()
        // Cek apakah 30 detik sudah berlalu sejak pesan kesalahan terakhir ditampilkan
        if (currentTime - lastErrorTime >= 30000) {
            // Jika sudah lebih dari 30 detik, tampilkan Toast dan update waktu terakhir pesan ditampilkan
            activeToast?.cancel() // Batalkan Toast yang sedang aktif
            activeToast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            activeToast?.show()
            lastErrorTime = currentTime // Update waktu terakhir
        }
    }

    // Fungsi untuk animasi kelembabanTextView yang hilang dan muncul dari kiri
    private fun animateTextAppear(textView: TextView) {
        when (textView) {
            kelembabanTextView1 -> {
                // Animasi untuk kelembabanTextView1 (muncul dari kiri ke kanan)
                val alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
                alphaAnimator.duration = 1500 // Durasi animasi transparansi 1.5 detik

                val translationXAnimator = ObjectAnimator.ofFloat(textView, "translationX", -1000f, 0f)
                translationXAnimator.duration = 1500 // Durasi animasi pergerakan 1.5 detik

                alphaAnimator.start()
                translationXAnimator.start()
            }

            kelembabanTextView2 -> {
                // Animasi untuk kelembabanTextView2 (muncul dari kanan ke kiri)
                val alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
                alphaAnimator.duration = 1500 // Durasi animasi transparansi 1.5 detik

                val translationXAnimator = ObjectAnimator.ofFloat(textView, "translationX", 1000f, 0f)
                translationXAnimator.duration = 1500 // Durasi animasi pergerakan 1.5 detik

                alphaAnimator.start()
                translationXAnimator.start()
            }
        }
    }
}
