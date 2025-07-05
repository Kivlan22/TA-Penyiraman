package com.example.taapp.TestAkhir.Home

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.taapp.Home.DetailCuaca.HomeCuaca
import com.example.taapp.R
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeCuacaTest {

    private lateinit var scenario: ActivityScenario<HomeCuaca>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(HomeCuaca::class.java)
    }

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun testCuacaDataDitampilkan() {
        Thread.sleep(3000) // Menunggu respons API BMKG

        onView(withId(R.id.lokasiText))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))

        onView(withId(R.id.provinsi))
            .check(matches(withText(containsString("Provinsi"))))

        onView(withId(R.id.kotkab))
            .check(matches(withText(containsString("Kota"))))

        onView(withId(R.id.kecamatan))
            .check(matches(withText(containsString("Kecamatan"))))

        onView(withId(R.id.desa))
            .check(matches(withText(containsString("Desa"))))

        onView(withId(R.id.suhu))
            .check(matches(withText(containsString("Suhu:"))))
    }

    @Test
    fun testInfoIconMenampilkanDialog() {
        onView(withId(R.id.infoIcon)).perform(click())

        onView(withId(R.id.blurOverlay)).check(matches(isDisplayed()))

        pressBack()

        Thread.sleep(1000)
        onView(withId(R.id.blurOverlay)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testLoadingSpinnerVisibility() {
        onView(withId(R.id.loadingSpinner)).check(matches(isDisplayed()))

        Thread.sleep(4000)

        onView(withId(R.id.loadingSpinner)).check(matches(not(isDisplayed())))
    }
}
