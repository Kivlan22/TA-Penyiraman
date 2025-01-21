package com.example.taapp.Camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    // URL stream untuk kamera
    private val cameraUrls = listOf(
        "https://livefeed.cameraiot.online/?action=stream1", // Kamera 1
        "https://livefeed.cameraiot.online/?action=stream2"  // Kamera 2
    )

    // Nama kamera
    private val cameraNames = listOf("Kamera 1", "Kamera 2")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = inflater.inflate(R.layout.fragment_kamera, container, false)

        // Inisialisasi WebView, Spinner, dan Toggle Switch
        videoWebView = binding.findViewById(R.id.videoWebView)
        cameraSpinner = binding.findViewById(R.id.cameraSpinner)
        toggleSwitch = binding.findViewById(R.id.toggleSwitch)

        // Matikan video dan set toggle switch ke OFF saat navigasi ke fragment
        toggleSwitch.isChecked = false
        videoWebView.visibility = View.GONE
        videoWebView.stopLoading()

        // Mengonfigurasi WebView
        setupWebView()

        // Mengatur adapter dan listener untuk Spinner
        setupSpinner()

        // Menambahkan listener untuk Toggle Switch
        setupToggleSwitch()

        return binding
    }

    private fun setupWebView() {
        // Mengaktifkan JavaScript untuk memuat konten video
        videoWebView.settings.javaScriptEnabled = true
        videoWebView.webViewClient = WebViewClient()  // Agar URL dibuka di dalam WebView

        // Memuat URL awal (default Kamera 1)
        videoWebView.loadUrl(cameraUrls[0])
    }

    private fun setupSpinner() {
        // Membuat adapter untuk Spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cameraNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Menghubungkan adapter ke Spinner
        cameraSpinner.adapter = adapter

        // Menambahkan listener untuk Spinner
        cameraSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Memuat URL berdasarkan pilihan kamera jika toggle switch ON
                if (toggleSwitch.isChecked) {
                    videoWebView.loadUrl(cameraUrls[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada aksi yang diperlukan
            }
        }
    }

    private fun setupToggleSwitch() {
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Toggle ON: Nyalakan WebView
                videoWebView.visibility = View.VISIBLE
                videoWebView.loadUrl(cameraUrls[cameraSpinner.selectedItemPosition])
            } else {
                // Toggle OFF: Matikan WebView
                videoWebView.visibility = View.GONE
                videoWebView.stopLoading()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hentikan WebView saat fragment dihancurkan
        videoWebView.destroy()
    }
}
