package com.example.taapp.TestAwal

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taapp.LoginRegister.StartActivity
import com.example.taapp.SplashActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    @Test
    fun splash_redirectsToStartActivity() {
        // ✅ Inisialisasi monitoring intent (harus sebelum Activity diluncurkan)
        init()

        // ✅ Meluncurkan SplashActivity untuk diuji
        ActivityScenario.launch(SplashActivity::class.java)

        // ⏱️ Tunggu 3.5 detik agar proses delay Splash selesai
        Thread.sleep(3500)

        // ✅ Verifikasi bahwa intent ke StartActivity benar-benar terjadi
        intended(hasComponent(StartActivity::class.java.name))

        // 🔚 Hentikan monitoring intent setelah selesai
        release()
    }
}
