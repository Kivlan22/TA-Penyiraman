package com.example.taapp.TestAwal

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taapp.LoginRegister.Login1Activity
import com.example.taapp.LoginRegister.RegisterActivity
import com.example.taapp.LoginRegister.StartActivity
import com.example.taapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartActivityTest {

    @Test
    fun clickRegisterButton_opensRegisterActivity() {
        init() // Inisialisasi intent tracking

        ActivityScenario.launch(StartActivity::class.java)

        // Tekan tombol Register
        onView(withId(R.id.register)).perform(click())

        // Cek apakah intent ke RegisterActivity terkirim
        intended(hasComponent(RegisterActivity::class.java.name))

        release()
    }

    @Test
    fun clickLoginButton_opensLoginActivity() {
        init()

        ActivityScenario.launch(StartActivity::class.java)

        // Tekan tombol Login
        onView(withId(R.id.login)).perform(click())

        // Cek apakah intent ke Login1Activity terkirim
        intended(hasComponent(Login1Activity::class.java.name))

        release()
    }
}
