package com.example.taapp.TestAkhir

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.taapp.Help.Help
import com.example.taapp.R
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HelpFragmentTest {

    @Before
    fun setUp() {
        Intents.init()
        launchFragmentInContainer<Help>(themeResId = R.style.Theme_TaApp)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testInstagramIntentLaunched() {
        val expectedUri = Uri.parse("https://www.instagram.com/iotinspace?igsh=anV2bmE3NGpzMno5")
        onView(withId(R.id.instagram)).perform(click())

        Intents.intended(allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(expectedUri)
        ))
    }

    @Test
    fun testWhatsAppIntentLaunched() {
        val expectedUri = Uri.parse("https://wa.me/+6281282416460")
        onView(withId(R.id.wa)).perform(click())

        Intents.intended(allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(expectedUri)
        ))
    }
}
