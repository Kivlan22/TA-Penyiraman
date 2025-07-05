package com.example.taapp.Camera

import android.graphics.Bitmap
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.taapp.R

class Kamera : Fragment() {

    private lateinit var videoWebView: WebView
    private lateinit var cameraSpinner: Spinner
    private lateinit var toggleSwitch: Switch

    private val cameraUrls = listOf(
        "https://livefeed.cameraiot.online/?action=stream1", // Kamera 1
        "https://livefeed2.cameraiot.online/?action=stream2"  // Kamera 2
    )

    private val cameraNames = listOf("Kamera 1", "Kamera 2")

    // Logging & monitoring variables
    private var pageLoadStartTime: Long = 0
    private var startRxBytes: Long = 0
    private var endRxBytes: Long = 0
    private var bufferingDetected = false
    private val bufferingTimeoutThreshold = 10000L // 10 detik

    // Per-second logging
    private val logHandler = Handler(Looper.getMainLooper())
    private var lastLoggedRxBytes: Long = 0
    private var logStartTime: Long = 0
    private var isLogging = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_kamera, container, false)

        videoWebView = binding.findViewById(R.id.videoWebView)
        cameraSpinner = binding.findViewById(R.id.cameraSpinner)
        toggleSwitch = binding.findViewById(R.id.toggleSwitch)

        toggleSwitch.isChecked = false
        videoWebView.visibility = View.GONE
        videoWebView.stopLoading()

        setupWebView()
        setupSpinner()
        setupToggleSwitch()

        return binding
    }

    private fun setupWebView() {
        videoWebView.settings.javaScriptEnabled = true

        videoWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                pageLoadStartTime = System.currentTimeMillis()
                startRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
                val cameraName = cameraNames[cameraSpinner.selectedItemPosition]

                Log.d("CameraLog", "Loading started: $cameraName at $pageLoadStartTime, URL: $url")

                bufferingDetected = false
                videoWebView.postDelayed({
                    if (!bufferingDetected && System.currentTimeMillis() - pageLoadStartTime > bufferingTimeoutThreshold) {
                        bufferingDetected = true
                        Log.w("CameraLog", "Potential buffering issue detected on $cameraName")
                    }
                }, bufferingTimeoutThreshold)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val cameraName = cameraNames[cameraSpinner.selectedItemPosition]
                val loadTime = System.currentTimeMillis() - pageLoadStartTime

                endRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
                val dataUsedKB = (endRxBytes - startRxBytes) / 1024.0

                Log.d("CameraLog", "Loading finished: $cameraName, Load Time: ${loadTime}ms, URL: $url")
                Log.d("CameraLog", "Estimated Data Used: %.2f KB".format(dataUsedKB))
            }
        }

        videoWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                val cameraName = cameraNames[cameraSpinner.selectedItemPosition]
                Log.d("CameraLog", "Progress $newProgress% on $cameraName")
            }
        }

        Log.d("CameraLog", "Initial load: Kamera 1, URL: ${cameraUrls[0]}")
        videoWebView.loadUrl(cameraUrls[0])
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cameraNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cameraSpinner.adapter = adapter

        cameraSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val cameraName = cameraNames[position]
                Log.d("CameraLog", "Camera selected: $cameraName (Position: $position)")

                if (toggleSwitch.isChecked) {
                    Log.d("CameraLog", "Reloading camera due to selection: $cameraName")
                    startRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
                    videoWebView.loadUrl(cameraUrls[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada aksi
            }
        }
    }

    private fun setupToggleSwitch() {
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            val selectedPosition = cameraSpinner.selectedItemPosition
            val selectedCamera = cameraNames[selectedPosition]

            if (isChecked) {
                Log.d("CameraLog", "Toggle ON: Loading $selectedCamera")
                videoWebView.visibility = View.VISIBLE
                startRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
                startRealtimeLogging()
                videoWebView.loadUrl(cameraUrls[selectedPosition])
            } else {
                Log.d("CameraLog", "Toggle OFF: Stopping $selectedCamera")
                videoWebView.visibility = View.GONE
                videoWebView.stopLoading()
                stopRealtimeLogging()
            }
        }
    }

    private val logRunnable = object : Runnable {
        override fun run() {
            if (!isLogging) return

            val currentTime = System.currentTimeMillis()
            val elapsedSeconds = (currentTime - logStartTime) / 1000

            val currentRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
            val usedKB = (currentRxBytes - lastLoggedRxBytes) / 1024.0
            lastLoggedRxBytes = currentRxBytes

            val cameraName = cameraNames[cameraSpinner.selectedItemPosition]

            Log.d("CameraLog", "[$elapsedSeconds s] $cameraName - Data Used: %.2f KB".format(usedKB))

            logHandler.postDelayed(this, 1000)
        }
    }

    private fun startRealtimeLogging() {
        isLogging = true
        logStartTime = System.currentTimeMillis()
        lastLoggedRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
        logHandler.post(logRunnable)
    }

    private fun stopRealtimeLogging() {
        isLogging = false
        logHandler.removeCallbacks(logRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CameraLog", "WebView destroyed")
        stopRealtimeLogging()
        videoWebView.destroy()
    }
}
