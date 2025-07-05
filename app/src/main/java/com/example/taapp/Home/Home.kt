package com.example.taapp.Home

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taapp.R
import com.example.taapp.Home.DetailCuaca.HomeCuaca
import com.example.taapp.Home.Log.LogCollector
import com.example.taapp.Home.Log.LogMonitoringDialog
import com.example.tugas2_mobile_kivlanhakeemarrouf_1103213073_tk4506.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Home : Fragment() {

    private lateinit var kelembabanTextView1: TextView
    private lateinit var kelembabanTextView2: TextView
    private lateinit var kelembabanTextView3: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var toggleSwitch: Switch
    private lateinit var detailCuaca: ImageView
    private lateinit var infoIcon1: ImageView

    private val handler = Handler()
    private val interval: Long = 500
    private var isRunning = false
    private var apiCallInProgress = false

    private var activeToast: Toast? = null
    private var lastErrorTime: Long = 0

    private var startTimeSensor1: Long = 0
    private var startTimeSensor2: Long = 0
    private var startTimeSensor3: Long = 0
    private var startTimeBMKG: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        kelembabanTextView1 = view.findViewById(R.id.kelembaban1)
        kelembabanTextView2 = view.findViewById(R.id.kelembaban2)
        kelembabanTextView3 = view.findViewById(R.id.kelembaban3)
        temperatureTextView = view.findViewById(R.id.temperature)
        toggleSwitch = view.findViewById(R.id.toggleSwitch)
        detailCuaca = view.findViewById(R.id.infoIcon)
        infoIcon1 = view.findViewById(R.id.infoIcon1)

        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) startRefreshingData()
            else stopRefreshingData()
        }

        detailCuaca.setOnClickListener {
            val intent = Intent(requireContext(), HomeCuaca::class.java)
            startActivity(intent)
        }

        infoIcon1.setOnClickListener {
            val dialog = LogMonitoringDialog()
            dialog.show(parentFragmentManager, "LogMonitoringDialog")
        }


        return view
    }

    private fun startRefreshingData() {
        isRunning = true
        apiCallInProgress = true
        val runnable = object : Runnable {
            override fun run() {
                if (isRunning && apiCallInProgress) {
                    getSensor1Data()
                    getSensor2Data()
                    getAvgSensor3Data()
                    getTemperatureFromBMKG()
                    handler.postDelayed(this, interval)
                }
            }
        }
        handler.post(runnable)
    }

    private fun stopRefreshingData() {
        isRunning = false
        apiCallInProgress = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun getSensor1Data() {
        startTimeSensor1 = System.currentTimeMillis()
        RetrofitClient.apiServiceSensor1.getSensor1Humidity().enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                val endTime = System.currentTimeMillis()
                val humidity = response.body()
                if (response.isSuccessful && humidity != null) {
                    val duration = endTime - startTimeSensor1
                    Log.d("API_TIMING", "Sensor1 response in $duration ms")
                    LogCollector.addLog("Sensor1 response in $duration ms")  // Menambahkan log ke LogCollector
                    animateHumidityChange(kelembabanTextView1, humidity)
                    updateKelembabanTextView(kelembabanTextView1, humidity)
                } else {
                    showError("Data tidak valid untuk Sensor 1")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                val endTime = System.currentTimeMillis()
                val errorMsg = "Sensor1 failed after ${endTime - startTimeSensor1} ms: ${t.message}"
                Log.e("API_TIMING", errorMsg)
                LogCollector.addLog(errorMsg)  // Menambahkan log ke LogCollector
                showError("Error: ${t.message} for Sensor 1")
            }
        })
    }

    private fun getSensor2Data() {
        startTimeSensor2 = System.currentTimeMillis()
        RetrofitClient.apiServiceSensor2.getSensor2Humidity().enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                val endTime = System.currentTimeMillis()
                val humidity = response.body()
                if (response.isSuccessful && humidity != null) {
                    val duration = endTime - startTimeSensor2
                    val logMessage = "Sensor2 response in $duration ms"
                    Log.d("API_TIMING", logMessage)
                    LogCollector.addLog(logMessage)  // Menambahkan log ke LogCollector
                    animateHumidityChange(kelembabanTextView2, humidity)
                    updateKelembabanTextView(kelembabanTextView2, humidity)
                } else {
                    showError("Data tidak valid untuk Sensor 2")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                val endTime = System.currentTimeMillis()
                val errorMsg = "Sensor2 failed after ${endTime - startTimeSensor2} ms: ${t.message}"
                Log.e("API_TIMING", errorMsg)
                LogCollector.addLog(errorMsg)  // Menambahkan log ke LogCollector
                showError("Error: ${t.message} for Sensor 2")
            }
        })
    }


    private fun getAvgSensor3Data() {
        startTimeSensor3 = System.currentTimeMillis()
        RetrofitClient.apiServiceSensor3.getAvgSensorHumidity().enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                val endTime = System.currentTimeMillis()
                val humidity = response.body()
                if (response.isSuccessful && humidity != null) {
                    val duration = endTime - startTimeSensor3
                    val logMessage = "Sensor3 (avg) response in $duration ms"
                    Log.d("API_TIMING", logMessage)
                    LogCollector.addLog(logMessage)  // Menambahkan log ke LogCollector
                    animateHumidityChange(kelembabanTextView3, humidity)
                    updateKelembabanTextView(kelembabanTextView3, humidity)
                } else {
                    showError("Data tidak valid untuk Avg Sensor 3")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                val endTime = System.currentTimeMillis()
                val errorMsg = "Sensor3 (avg) failed after ${endTime - startTimeSensor3} ms: ${t.message}"
                Log.e("API_TIMING", errorMsg)
                LogCollector.addLog(errorMsg)  // Menambahkan log ke LogCollector
                showError("Error: ${t.message} for Avg Sensor 3")
            }
        })
    }



    private fun getTemperatureFromBMKG() {
        startTimeBMKG = System.currentTimeMillis()
        val adm4Code = "32.04.32.1001"
        BMKGApiService.instance.getWeatherForecast(adm4Code)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    val endTime = System.currentTimeMillis()
                    val duration = endTime - startTimeBMKG

                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        val temp = weatherResponse?.data
                            ?.firstOrNull()
                            ?.cuaca
                            ?.firstOrNull()
                            ?.firstOrNull()
                            ?.t

                        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                        if (temp != null) {
                            temperatureTextView.text = "$temp°C"
                            val logMessage = "BMKG temperature response in $duration ms"
                            Log.d("API_TIMING", logMessage)
                            LogCollector.addLog(logMessage)  // Menambahkan log ke LogCollector
                            Log.d("BMKG_TEMPERATURE", "[$currentTime] Suhu saat ini: $temp°C")
                        } else {
                            showError("Data suhu tidak tersedia dalam respons BMKG.")
                            Log.e("BMKG_TEMPERATURE", "[$currentTime] Suhu tidak ditemukan di respons.")
                        }
                    } else {
                        showError("Gagal mengambil data cuaca dari BMKG (kode ${response.code()}).")
                        val errorMsg = "Response tidak sukses: ${response.code()}"
                        Log.e("BMKG_TEMPERATURE", errorMsg)
                        LogCollector.addLog(errorMsg)  // Menambahkan log ke LogCollector
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    val endTime = System.currentTimeMillis()
                    val errorMsg = "BMKG temperature failed after ${endTime - startTimeBMKG} ms: ${t.message}"
                    Log.e("API_TIMING", errorMsg)
                    LogCollector.addLog(errorMsg)  // Menambahkan log ke LogCollector
                    showError("Gagal menghubungi API BMKG: ${t.message}")
                }
            })
    }




    private fun animateHumidityChange(textView: TextView, humidity: Double) {
        val currentText = textView.text.toString()
        val currentHumidity = currentText.substringBefore("%").toDoubleOrNull()
        val startHumidity = currentHumidity ?: humidity

        val animator = ValueAnimator.ofFloat(startHumidity.toFloat(), humidity.toFloat())
        animator.duration = 500
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            textView.text = String.format("%.2f%%", animatedValue)
        }
        animator.start()
    }

    private fun updateKelembabanTextView(textView: TextView, humidity: Double) {
        textView.text = String.format("%.2f%%", humidity)
    }

    private fun showError(message: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastErrorTime >= 30000) {
            activeToast?.cancel()
            activeToast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            activeToast?.show()
            lastErrorTime = currentTime
        }
    }

    private fun animateTextAppear(textView: TextView) {
        when (textView) {
            kelembabanTextView1 -> {
                val alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
                alphaAnimator.duration = 1500

                val translationXAnimator = ObjectAnimator.ofFloat(textView, "translationX", -1000f, 0f)
                translationXAnimator.duration = 1500

                alphaAnimator.start()
                translationXAnimator.start()
            }
            kelembabanTextView2 -> {
                val alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
                alphaAnimator.duration = 1500

                val translationXAnimator = ObjectAnimator.ofFloat(textView, "translationX", 1000f, 0f)
                translationXAnimator.duration = 1500

                alphaAnimator.start()
                translationXAnimator.start()
            }
        }
    }
}
