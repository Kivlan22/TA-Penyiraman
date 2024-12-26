package com.example.taapp.Camera

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taapp.R
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class Kamera : Fragment() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kamera, container, false)

        playerView = view.findViewById(R.id.playerView)

        // Inisialisasi player hanya jika belum ada player yang terbuat
        if (!::player.isInitialized) {
            player = ExoPlayer.Builder(requireContext()).build()
            playerView.player = player

            // Set media item (video URL)
            val mediaItem = MediaItem.fromUri(
                Uri.parse("https://eofficev2.bekasikota.go.id/backupcctv/m3/Samping_RS_Mitra_Keluarga.m3u8")
            )
            player.setMediaItem(mediaItem)
            player.prepare()
        }

        // Play video jika belum diputar
        if (!player.isPlaying) {
            player.play()
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        // Pause video saat fragment dipause
        if (player.isPlaying) {
            player.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        // Lanjutkan video saat fragment resume
        if (!player.isPlaying) {
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Melepaskan player saat fragment dihancurkan
        player.release()
    }
}
