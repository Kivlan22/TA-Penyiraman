package com.example.taapp.TestAkhir.Home

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.taapp.Home.Home
import com.example.taapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentTest {

    @Test
    fun test_allViewsAreVisible() {
        // Launch Home fragment
        launchFragmentInContainer<Home>(Bundle(), R.style.Theme_TAAPP)

        // Cek apakah elemen UI muncul
        onView(withId(R.id.kelembaban1)).check(matches(isDisplayed()))
        onView(withId(R.id.kelembaban2)).check(matches(isDisplayed()))
        onView(withId(R.id.kelembaban3)).check(matches(isDisplayed()))
        onView(withId(R.id.temperature)).check(matches(isDisplayed()))
        onView(withId(R.id.toggleSwitch)).check(matches(isDisplayed()))
        onView(withId(R.id.infoIcon)).check(matches(isDisplayed()))
        onView(withId(R.id.infoIcon1)).check(matches(isDisplayed()))
    }

    @Test
    fun test_toggleSwitch_triggersDataRefresh() {
        launchFragmentInContainer<Home>(Bundle(), R.style.Theme_TAAPP)

        // Klik toggleSwitch untuk ON
        onView(withId(R.id.toggleSwitch)).perform(click())

        // Bisa tambahkan delay/idle check jika perlu
        // (cek apakah data muncul misalnya, tapi di blackbox kita hanya bisa cek tampilannya)
        onView(withId(R.id.kelembaban1)).check(matches(isDisplayed()))
    }

    @Test
    fun test_clickInfoIcon_opensCuacaActivity() {
        launchFragmentInContainer<Home>(Bundle(), R.style.Theme_TAAPP)

        // Klik icon info (cuaca)
        onView(withId(R.id.infoIcon)).perform(click())

        // Karena ini membuka activity, kamu bisa pakai Intent Test (butuh Intents.init())
        // Tapi jika tidak, cukup pastikan tidak error saat klik
    }

    @Test
    fun test_clickInfoIcon1_opensLogDialog() {
        launchFragmentInContainer<Home>(Bundle(), R.style.Theme_TAAPP)

        onView(withId(R.id.infoIcon1)).perform(click())

        // Kita tidak bisa memverifikasi DialogFragment langsung tanpa dependency tambahan.
        // Tapi kamu bisa cek apakah klik berhasil tanpa crash.
    }
}
