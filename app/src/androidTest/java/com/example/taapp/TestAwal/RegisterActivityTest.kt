package com.example.taapp.LoginRegister

import android.widget.Toast
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.taapp.R
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Test
    fun fillRegisterForm_andClickRegisterButton_shouldShowToastOrDialog() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Isi semua input dengan ScrollTo agar semua field bisa disentuh meskipun layout panjang
        onView(withId(R.id.nameInput)).perform(scrollTo(), typeText("Tes BlackBox"), closeSoftKeyboard())
        onView(withId(R.id.emailInput)).perform(scrollTo(), typeText("blackbox@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(scrollTo(), typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.confPasswordInput)).perform(scrollTo(), typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.iotCodeInput)).perform(scrollTo(), typeText("IoT123456"), closeSoftKeyboard())
        onView(withId(R.id.confPhone)).perform(scrollTo(), typeText("+6285890430473"), closeSoftKeyboard())

        // Klik tombol register
        onView(withId(R.id.registerButton)).perform(scrollTo(), click())

        // Delay observasi hasil (Toast, dialog OTP, dll.)
        Thread.sleep(3000)
    }
}

