
package com.example.in2000_team1.ui

import com.example.in2000_team1.datalayer.*


data class UiState (
    val currentPrice: Double = 0.0,
    val currentTemperature: Double = 0.0,
    val location: Location = Location.Oslo,
    val prediction: MutableList<Double> = mutableListOf(),
    val forecast: List<DailyWeatherData> = emptyList(),
    val appliances: MutableList<Appliance> = mutableListOf(),
    val currentTemperatureError: Boolean = false,
    val otherApiError: Boolean = false
)
