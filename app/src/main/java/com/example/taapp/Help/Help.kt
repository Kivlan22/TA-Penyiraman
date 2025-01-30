package com.example.taapp.Help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taapp.R
import android.widget.ImageView

class Help : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_help, container, false)

        // Instagram ImageView click listener
        val instagramImageView = view.findViewById<ImageView>(R.id.instagram)
        instagramImageView.setOnClickListener {
            val instagramUrl = "https://www.instagram.com/iotinspace?igsh=anV2bmE3NGpzMno5"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
            startActivity(intent)
        }

        // WhatsApp ImageView click listener
        val waImageView = view.findViewById<ImageView>(R.id.wa)
        waImageView.setOnClickListener {
            val phoneNumber = "+6281282416460"
            val waUrl = "https://wa.me/$phoneNumber"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
            startActivity(intent)
        }

        return view
    }
}
