package com.example.taapp.LoginRegister

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.taapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class Login1ActivityTest {
    @Test
    fun testLoginFlow_withValidInput_showsLoadingAndAttemptsLogin() {
        ActivityScenario.launch(Login1Activity::class.java)

        // Langsung replaceText tanpa click() untuk stabilitas
        onView(withId(R.id.email))
            .perform(scrollTo(), replaceText("blackbox@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.pass))
            .perform(scrollTo(), replaceText("password123"), closeSoftKeyboard())

        onView(withId(R.id.signup))
            .perform(scrollTo(), click())

        // Tunggu dialog muncul, klik tombol "Yes"
        onView(withId(R.id.btn_positive))
            .perform(click())

        // Tunggu loading muncul (gunakan IdlingResource kalau bisa, sementara Thread.sleep untuk tes cepat)
        Thread.sleep(1000)

        onView(withId(R.id.loadingLayout))
            .check(matches(isDisplayed()))
    }
}
