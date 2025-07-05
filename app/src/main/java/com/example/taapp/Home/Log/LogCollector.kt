package com.example.taapp.Home.Log

object LogCollector {

    private val logs = mutableListOf<String>()
    private var logChangedListener: ((List<String>) -> Unit)? = null

    // Menambahkan log baru ke dalam list
    fun addLog(log: String) {
        logs.add(log)
        logChangedListener?.invoke(logs) // Memberitahukan listener bahwa log telah berubah
    }

    // Mengambil daftar log
    fun getLogs(): List<String> = logs

    // Menghapus listener
    fun clearListener() {
        logChangedListener = null
    }

    // Menetapkan listener untuk mendengarkan perubahan log
    fun setOnLogChangedListener(listener: (List<String>) -> Unit) {
        logChangedListener = listener
    }
}
