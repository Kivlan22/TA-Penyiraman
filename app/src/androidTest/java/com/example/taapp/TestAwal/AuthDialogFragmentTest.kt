package com.example.taapp.TestAwal

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taapp.LoginRegister.AuthDialogFragment
import com.example.taapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthDialogFragmentTest {

    private val testName = "Tes BlackBox"
    private val testEmail = "blackbox@gmail.com"
    private val testPassword = "password123"
    private val testConfPass = "password123"
    private val testIotCode = "IoT123456"
    private val testPhone = "+6285890430473"
    private val testVerificationId = "654321"

    @Test
    fun confirmButtonWithoutOtp_showsToast() {
        val fragmentArgs = Bundle().apply {
            putString("name", testName)
            putString("email", testEmail)
            putString("password", testPassword)
            putString("confpass", testConfPass)
            putString("iotcode", testIotCode)
            putString("phone", testPhone)
            putString("verificationId", testVerificationId)
        }

        launchFragmentInContainer<AuthDialogFragment>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.Theme_TaApp
        )

        // Click confirm button without entering OTP
        onView(withId(R.id.confirm)).perform(click())

        // Expected: Toast "Please enter the OTP" shown (observe manually or via logcat)
    }

    @Test
    fun enterOtpAndClickConfirm() {
        val fragmentArgs = Bundle().apply {
            putString("name", testName)
            putString("email", testEmail)
            putString("password", testPassword)
            putString("confpass", testConfPass)
            putString("iotcode", testIotCode)
            putString("phone", testPhone)
            putString("verificationId", testVerificationId)
        }

        launchFragmentInContainer<AuthDialogFragment>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.Theme_TaApp
        )

        // Input a sample OTP (fake for test)
        onView(withId(R.id.otp_input)).perform(typeText("123456"), closeSoftKeyboard())

        // Click confirm button to verify OTP
        onView(withId(R.id.confirm)).perform(click())

        // Outcome depends on Firebase mock or real backend (observe manually)
    }

    @Test
    fun resendButton_click_startsCountdown() {
        val fragmentArgs = Bundle().apply {
            putString("name", testName)
            putString("email", testEmail)
            putString("password", testPassword)
            putString("confpass", testConfPass)
            putString("iotcode", testIotCode)
            putString("phone", testPhone)
            putString("verificationId", testVerificationId)
        }

        launchFragmentInContainer<AuthDialogFragment>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.Theme_TaApp
        )

        // Click resend button and observe countdown and UI change
        onView(withId(R.id.resend)).perform(click())

        // Observe progress bar visibility and countdown text manually
    }
}
