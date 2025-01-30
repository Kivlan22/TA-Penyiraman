package com.example.taapp.Controlling

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.taapp.R
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Controlling : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var humidityTextView: TextView
    private lateinit var buttonOn: AppCompatButton
    private lateinit var buttonOff: AppCompatButton
    private lateinit var otomatisSwitch: Switch
    private lateinit var manualSwitch: Switch
    private lateinit var modeText: TextView  // Add reference to the modeText TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_controlling, container, false)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://script.google.com/macros/s/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        humidityTextView = view.findViewById(R.id.temperature)
        buttonOn = view.findViewById(R.id.on)
        buttonOff = view.findViewById(R.id.off)
        otomatisSwitch = view.findViewById(R.id.otomatisSwitch)
        manualSwitch = view.findViewById(R.id.manualSwitch)
        modeText = view.findViewById(R.id.modeText) // Initialize modeText TextView

        // Fetch initial humidity value
        fetchHumidity()

        // Set otomatisSwitch to ON initially
        otomatisSwitch.isChecked = true
        triggerOtomatisOn()

        // Set listener for OtomatisSwitch to handle state changes
        otomatisSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                // Show confirmation dialog before turning off
                showConfirmationDialog()
            } else {
                // Trigger API for Otomatis ON
                triggerOtomatisOn()
            }

            // Disable/Enable buttons based on otomatisSwitch state
            updateButtonState()

            // Update modeText when OtomatisSwitch is changed
            updateModeText()
        }

        // Set listener for ManualSwitch to handle state changes
        manualSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && otomatisSwitch.isChecked) {
                // If the manualSwitch is turned on while otomatisSwitch is still on,
                // show the warning dialog and reset manualSwitch to off
                showManualSwitchWarning()
                manualSwitch.isChecked = false // Ensure manualSwitch remains off
            } else if (!isChecked) {
                // When manualSwitch is turned off, automatically turn on OtomatisSwitch
                otomatisSwitch.isChecked = true
                triggerOtomatisOn() // Trigger the API for Otomatis ON
            }

            // Update the button state based on manualSwitch state
            updateButtonState()

            // Update modeText when ManualSwitch is changed
            updateModeText()
        }

        // Set click listener for the "On" button
        buttonOn.setOnClickListener {
            if (otomatisSwitch.isChecked) {
                // Show the warning when trying to turn on manual mode while otomatisSwitch is on
                showManualSwitchWarning()
            } else if (manualSwitch.isChecked) {
                triggerManualOn() // Changed to trigger setManualOn API
                // Update status text to "On"
                val statusText = view.findViewById<TextView>(R.id.text3)
                statusText?.text = "Status: Off"
            }
        }

// Set click listener for the "Off" button
        buttonOff.setOnClickListener {
            if (otomatisSwitch.isChecked) {
                // Show the warning when trying to turn off manual mode while otomatisSwitch is on
                showManualSwitchWarning()
            } else if (manualSwitch.isChecked) {
                triggerManualOff() // Changed to trigger setManualOff API
                // Update status text to "Off"
                val statusText = view.findViewById<TextView>(R.id.text3)
                statusText?.text = "Status: On"
            }
        }

        return view
    }

    // Function to update the modeText based on the current mode
    private fun updateModeText() {
        if (otomatisSwitch.isChecked) {
            modeText.text = "Mode Otomatis"
        } else if (manualSwitch.isChecked) {
            modeText.text = "Mode Manual"
        }
    }

    // Function to show custom warning dialog
    private fun showManualSwitchWarning() {
        val builder = AlertDialog.Builder(requireContext())

        // Inflate the custom warning layout
        val dialogView = layoutInflater.inflate(R.layout.custom_warning_manual, null)

        builder.setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        // Access the "Ok" button from the dialog layout
        val btnNegative = dialogView.findViewById<MaterialButton>(R.id.btn_negative)

        btnNegative.setOnClickListener {
            dialog.dismiss()  // Dismiss the dialog when "Ok" is clicked
        }

        dialog.show()
    }

    // Function to update button state
    private fun updateButtonState() {
        // If otomatisSwitch is ON, disable both "On" and "Off" buttons
        if (otomatisSwitch.isChecked) {
            buttonOn.isEnabled = false
            buttonOff.isEnabled = false
            // Update the status to reflect that the system is in otomatis mode
            val statusText = view?.findViewById<TextView>(R.id.text3)
            statusText?.text = "Status: ..." // or any appropriate status for automatic mode
        } else {
            // Otherwise, enable the buttons based on the manualSwitch state
            if (manualSwitch.isChecked) {
                buttonOn.isEnabled = true
                buttonOff.isEnabled = true
            } else {
                buttonOn.isEnabled = false
                buttonOff.isEnabled = false
            }
        }
    }

    // Fetch humidity value from API
    private fun fetchHumidity() {
        apiService.getAvgSensor().enqueue(object : Callback<SensorResponse> {
            override fun onResponse(call: Call<SensorResponse>, response: Response<SensorResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        humidityTextView.text = "${it}%" // Display the humidity value
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SensorResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to fetch humidity", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Trigger Otomatis ON API call
    private fun triggerOtomatisOn() {
        apiService.setOtomatisOn().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
            override fun onFailure(call: Call<Unit>, t: Throwable) {}
        })
    }

    // Trigger Otomatis OFF API call
    private fun triggerOtomatisOff() {
        apiService.setOtomatisOff().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
            override fun onFailure(call: Call<Unit>, t: Throwable) {}
        })
    }

    // Trigger Manual ON API call
    private fun triggerManualOn() {
        apiService.setManualOn().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
            override fun onFailure(call: Call<Unit>, t: Throwable) {}
        })
    }

    // Trigger Manual OFF API call
    private fun triggerManualOff() {
        apiService.setManualOff().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
            override fun onFailure(call: Call<Unit>, t: Throwable) {}
        })
    }

    // Show confirmation dialog for turning off OtomatisSwitch
    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.custom_warning_pump, null)

        builder.setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        val btnPositive = dialogView.findViewById<MaterialButton>(R.id.btn_positive)
        val btnNegative = dialogView.findViewById<MaterialButton>(R.id.btn_negative)

        btnPositive.setOnClickListener {
            otomatisSwitch.isChecked = false
            manualSwitch.isChecked = true
            triggerOtomatisOff()
            triggerManualOn()
            dialog.dismiss()
        }

        btnNegative.setOnClickListener {
            dialog.dismiss()
            otomatisSwitch.isChecked = true
        }

        dialog.show()
    }
}
