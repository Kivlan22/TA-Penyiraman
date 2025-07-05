package com.example.taapp.Home.DetailCuaca

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.taapp.R

class MenuCuaca : DialogFragment() {

    private var onDismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_cuaca, container, false)

        val searchView = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteSearch)
        val locationContainer = view.findViewById<GridLayout>(R.id.locationListContainer)

        val lokasiMap = mapOf(
            "Kelurahan Baleendah" to "32.04.32.1001",
            "Kelurahan Andir" to "32.04.32.1002",
            "Kelurahan Manggahang" to "32.04.32.1003",
            "Kelurahan Jelekong" to "32.04.32.1004",
            "Desa Bojongmalaka" to "32.04.32.2005",
            "Desa Rancamanyar" to "32.04.32.2006",
            "Desa Malakasari" to "32.04.32.2007",
            "Kelurahan Wargamekar" to "32.04.32.1008"
        )

        val lokasiList = lokasiMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, lokasiList)
        searchView.setAdapter(adapter)

        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().lowercase()
                val filteredList = lokasiList.filter { it.lowercase().contains(keyword) }
                showLocationList(filteredList, locationContainer, lokasiMap)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        showLocationList(lokasiList, locationContainer, lokasiMap)

        return view
    }

    private fun showLocationList(
        list: List<String>,
        container: GridLayout,
        lokasiMap: Map<String, String>
    ) {
        container.removeAllViews()

        list.forEach { lokasi ->
            val textView = TextView(requireContext()).apply {
                text = lokasi
                setTextColor(Color.BLACK)
                textSize = 14f
                setPadding(24, 20, 24, 20)
                background = GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadius = 12f
                    setStroke(2, ContextCompat.getColor(context, android.R.color.darker_gray))
                }
                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setMargins(8, 8, 8, 8)
                }

                setOnClickListener {
                    val adm4 = lokasiMap[lokasi]
                    if (adm4 != null) {
                        val sharedPref = requireContext().getSharedPreferences("cuaca_pref", Context.MODE_PRIVATE)
                        sharedPref.edit()
                            .putString("selected_adm4_code", adm4)
                            .putString("selected_location_name", lokasi)
                            .apply()

                        Toast.makeText(requireContext(), "Lokasi dipilih: $lokasi", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Kode wilayah tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                    dismiss()
                }
            }

            container.addView(textView)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
