package com.example.in2000_team1


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.in2000_team1.ui.Location
import com.example.in2000_team1.datalayer.*
import com.example.in2000_team1.ui.MainViewModel
import com.example.in2000_team1.ui.composables.MainScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUpMainScreen() {
        composeTestRule.setContent {
            MainScreen(
                predictionsAndDays = mapOf(
                    "man" to 0.1f,
                    "tir" to 0.2f,
                    "ons" to 0.3f,
                    "tor" to 0.4f,
                    "fre" to 0.5f,
                    "lør" to 0.6f,
                    "søn" to 0.7f
                ),
                dayAndForecast = mapOf(
                    "man" to DailyWeatherData(date = "2023-04-26", airTemperature = 15.0, precipitationSum = 1.0, windSpeed = 5.0),
                    "tir" to DailyWeatherData(date = "2023-04-27", airTemperature = 16.0, precipitationSum = 0.5, windSpeed = 4.0),
                    "ons" to DailyWeatherData(date = "2023-04-28", airTemperature = 17.0, precipitationSum = 0.0, windSpeed = 3.0),
                    "tor" to DailyWeatherData(date = "2023-04-29", airTemperature = 18.0, precipitationSum = 2.0, windSpeed = 6.0),
                    "fre" to DailyWeatherData(date = "2023-04-30", airTemperature = 19.0, precipitationSum = 1.5, windSpeed = 5.5),
                    "lør" to DailyWeatherData(date = "2023-05-01", airTemperature = 20.0, precipitationSum = 0.2, windSpeed = 3.5),
                    "søn" to DailyWeatherData(date = "2023-05-02", airTemperature = 21.0, precipitationSum = 0.8, windSpeed = 4.5)
                ),
                location = Location.Oslo,
                currentPrice = 0.8,
                currentTemp = 22.0,
                appliances = mutableListOf(),
                viewModel = MainViewModel(),
                navController = rememberNavController()
            )
        }
    }

    // 1. Test if the current price and temperature are displayed correctly
    @Test
    fun mainScreenDisplaysCurrentPriceAndTemperature() {
        val currentPriceFormatted = String.format("%.0f", 100 * 0.8)
        composeTestRule.onNodeWithText("$currentPriceFormatted øre/kWh").assertIsDisplayed()
        composeTestRule.onNodeWithText("22.0 °C").assertIsDisplayed()
    }

    // Test if the appliances information is displayed correctly when appliances list is empty
    @Test
    fun mainScreenDisplaysApplianceInfoForEmptyList() {
        composeTestRule.onNodeWithText("Legg til apparater:").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add").assertIsDisplayed()
    }


    // Test if the location name is displayed
    @Test
    fun mainScreenDisplaysLocationName() {
        composeTestRule.onNodeWithText(Location.Oslo.name).assertIsDisplayed()
    }


}


