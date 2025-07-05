package com.example.taapp.Home.Log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.taapp.R

class LogMonitoringDialog : DialogFragment() {

    private lateinit var logListContainer: LinearLayout
    private lateinit var logListScrollView: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.log_monitoring, container, false)

        logListContainer = view.findViewById(R.id.logListContainer)
        logListScrollView = view.findViewById(R.id.logListScrollView)

        // Load initial logs
        updateLogUI(LogCollector.getLogs())

        // Listen for new logs
        LogCollector.setOnLogChangedListener { logs ->
            updateLogUI(logs)
        }

        return view
    }

    private fun updateLogUI(logs: List<String>) {
        logListContainer.removeAllViews()
        for (log in logs) {
            val logItem = TextView(requireContext()).apply {
                text = log
                textSize = 13f
                setPadding(8, 4, 8, 4)
            }
            logListContainer.addView(logItem)
        }

        // Scroll to bottom
        logListScrollView.post {
            logListScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogCollector.clearListener()
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog?.window?.setLayout(dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
    }
}
