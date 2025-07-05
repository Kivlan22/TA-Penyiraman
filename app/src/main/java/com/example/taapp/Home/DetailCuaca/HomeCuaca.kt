package com.example.taapp.Home.DetailCuaca

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.taapp.Home.BMKGApiService
import com.example.taapp.Home.CuacaData
import com.example.taapp.R
import com.example.taapp.databinding.FragmentHomeCuacaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeCuaca : AppCompatActivity() {

    private lateinit var binding: FragmentHomeCuacaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeCuacaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWeather()

        binding.infoIcon.setOnClickListener {
            val menuCuaca = MenuCuaca()

            setBlurVisible(true)

            menuCuaca.setOnDismissListener {
                setBlurVisible(false)
                getWeather() // refresh data saat dialog ditutup
            }

            menuCuaca.show(supportFragmentManager, "MenuCuacaDialog")
        }
    }

    private fun setBlurVisible(visible: Boolean) {
        binding.blurOverlay.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun getWeather() {
        showLoading(true)

        val sharedPref = getSharedPreferences("cuaca_pref", MODE_PRIVATE)
        val adm4Code = sharedPref.getString("selected_adm4_code", "32.04.32.1001") ?: "32.04.32.1001"

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    BMKGApiService.instance.getWeatherForecast(adm4Code).execute().body()
                }

                if (response != null) {
                    val lokasi = response.lokasi
                    val cuacaList = response.data.firstOrNull()?.cuaca?.flatten() ?: emptyList()

                    binding.lokasiText.text = lokasi.kecamatan
                    binding.provinsi.text = "Provinsi: ${lokasi.provinsi}"
                    binding.kotkab.text = "Kota/Kabupaten: ${lokasi.kotkab}"
                    binding.kecamatan.text = "Kecamatan: ${lokasi.kecamatan}"
                    binding.desa.text = "Desa: ${lokasi.desa}"
                    binding.latitude.text = "Latitude: ${lokasi.lat}"
                    binding.longitude.text = "Longitude: ${lokasi.lon}"
                    binding.timezone.text = "Zona Waktu: ${lokasi.timezone}"
                    binding.suhu.text = "Suhu: ${cuacaList.firstOrNull()?.t ?: "-"} °C"

                    showCuacaList(cuacaList)
                } else {
                    Log.e("BMKG", "Response null")
                }
            } catch (e: Exception) {
                Log.e("BMKG", "Error fetching data", e)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingSpinner.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showCuacaList(list: List<CuacaData>) {
        binding.cuacaContainer.removeAllViews()
        val inflater = layoutInflater

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM HH:mm", Locale("id", "ID"))

        for (cuaca in list) {
            val itemView = inflater.inflate(R.layout.fragment_item_cuaca, binding.cuacaContainer, false)

            val waktuText = itemView.findViewById<TextView>(R.id.textTime)
            val descText = itemView.findViewById<TextView>(R.id.textCuaca)
            val iconImage = itemView.findViewById<ImageView>(R.id.imageCuaca)

            val formattedTime = try {
                val parsedDate = inputFormat.parse(cuaca.local_datetime)
                parsedDate?.let { outputFormat.format(it) } ?: "-"
            } catch (e: Exception) {
                "-"
            }

            waktuText.text = formattedTime
            descText.text = cuaca.weather_desc

            Glide.with(this)
                .load(cuaca.image)
                .placeholder(R.drawable.ic_placeholder_weather)
                .into(iconImage)

            binding.cuacaContainer.addView(itemView)
        }
    }
}
