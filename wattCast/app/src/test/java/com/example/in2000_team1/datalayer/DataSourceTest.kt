package com.example.in2000_team1.datalayer

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Test

class DataSourceTest {

    private val dataSource = DataSource()

    @Test
    fun fetchWeatherDataForLast60Days_returnsCorrectData() = runBlocking {
        val elementId = "air_temperature"
        val sources = "SN18700"
        val weatherData = dataSource.fetchWeatherDataForLast60Days(elementId, sources)

        // Check that the returned array has 60 elements
        assertEquals(60, weatherData.size)

        // Check that all elements in the array are between -40 and 40 degrees Celsius
        assertTrue(weatherData.all { it >= -40 && it <= 40 })
    }

    @Test
    fun getAveragePricesForLast60Days_returnsCorrectData() = runBlocking {
        val priceArea = "NO1"
        val averagePrices = dataSource.getAveragePricesForLast60Days(priceArea)

        // Check that the returned array has 60 elements
        assertEquals(60, averagePrices.size)

        // Check that all elements in the array are positive
        assertTrue(averagePrices.all { it >= 0 })
    }

    @Test
    fun getCurrentElectricityPrice_returnsCorrectData() = runBlocking {
        val priceArea = "NO1"
        val currentPrice = dataSource.getCurrentElectricityPrice(priceArea)

        // Check that the returned price is positive
        assertTrue(currentPrice >= 0)
    }

    @Test
    fun getCurrentTemperature_returnsCorrectData() = runBlocking {
        val lat = 59.91273
        val lon = 10.74609
        val currentTemp = dataSource.getCurrentTemperature(lat, lon)

        // Check that the returned temperature is between -40 and 40 degrees Celsius
        assertTrue(currentTemp >= -40 && currentTemp <= 40)
    }

    @Test
    fun getForecastForNext7Days_returnsCorrectData() = runBlocking {
        val lat = 59.91273
        val lon = 10.74609
        val dailyWeatherDataList = dataSource.getForecastForNext7Days(lat, lon)

        // Check that the returned list has 7 elements
        assertEquals(7, dailyWeatherDataList.size)

        // Check that all mean air temperatures are between -40 and 40 degrees Celsius
        assertTrue(dailyWeatherDataList.all { it.airTemperature >= -40 && it.airTemperature <= 40 })

        // Check that all precipitation sums are non-negative
        assertTrue(dailyWeatherDataList.all { it.precipitationSum >= 0 })

        // Check that all mean wind speeds are non-negative
        assertTrue(dailyWeatherDataList.all { it.windSpeed >= 0 })
    }

}