package com.example.in2000_team1.ui


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_team1.datalayer.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Initialize the data source
    private val dataSource = DataSource()
    // Initialize the multiple linear regression model
    private val mlr = MultipleLinearRegression()

    // We keep track of whether the data has been fetched yet, so that we don't attempt to use it before
    // If the user changes location and we have to fetch again, this needs to be reset to false before the fetch
    private val _dataFetched = MutableStateFlow(false)
    val dataFetched: StateFlow<Boolean> = _dataFetched.asStateFlow()

    // Keeping track of whether or not we are refreshing, for the Swipe Refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    // Gson to convert to json string, used for SharedPrefereces to persist data between launches
    private val gson = Gson()

    
    // Makes calls to fetch data from APIs and to make price predictions. Stores the results in UiState
    @Suppress("UNCHECKED_CAST") // We can suppress this, because there will never be a problem related to it. If there was, there would be an earlier exception that is handled.
    fun update() {
        _dataFetched.value = false
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(currentTemperatureError = false)
                _uiState.value = _uiState.value.copy(otherApiError = false)
                // Get the required parameters from the selected location
                val frostApiCode = _uiState.value.location.frostApiCode
                val priceAreaCode = _uiState.value.location.priceCode
                val lat = _uiState.value.location.latitude
                val lon = _uiState.value.location.longitude

                // Set a timeout of 15 seconds
                val timeoutDuration = 15_000L

                // Fetch the historical data
                val temperature = viewModelScope.async {
                    try {
                        withTimeout(timeoutDuration) {
                            dataSource.fetchWeatherDataForLast60Days("mean(air_temperature P1D)", frostApiCode)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.value = _uiState.value.copy(otherApiError = true)
                        DoubleArray(60) { 0.0 }
                    }
                }

                val rain = viewModelScope.async {
                    try {
                        withTimeout(timeoutDuration) {
                            dataSource.fetchWeatherDataForLast60Days("sum(precipitation_amount P1D)", frostApiCode)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.value = _uiState.value.copy(otherApiError = true)
                        DoubleArray(60) { 0.0 }
                    }
                }

                val wind = viewModelScope.async {
                    try {
                        withTimeout(timeoutDuration) {
                            dataSource.fetchWeatherDataForLast60Days("mean(wind_speed P1D)", frostApiCode)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.value = _uiState.value.copy(otherApiError = true)
                        DoubleArray(60) { 0.0 }
                    }
                }

                val electricityPrice = viewModelScope.async {
                    try {
                        withTimeout(timeoutDuration) {
                            dataSource.getAveragePricesForLast60Days(priceAreaCode)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.value = _uiState.value.copy(otherApiError = true)
                        DoubleArray(60) { 0.0 }
                    }
                }

                // Fetch the forecast for the next 7 days
                val forecast = viewModelScope.async {
                    try {
                        withTimeout(timeoutDuration) {
                            dataSource.getForecastForNext7Days(lat, lon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.value = _uiState.value.copy(otherApiError = true)
                        List(7) { DailyWeatherData("", 0.0, 0.0, 0.0) }
                    }
                }

                // Fetch the current price
                val currentPrice = viewModelScope.async {
                    try {
                        withTimeout(timeoutDuration) {
                            dataSource.getCurrentElectricityPrice(priceAreaCode)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.value = _uiState.value.copy(otherApiError = true)
                        0.0
                    }
                }

                // Fetch the current temperature
                val currentTemperature = viewModelScope.async {
                    try {
                        withTimeout(timeoutDuration) {
                            dataSource.getCurrentTemperature(lat, lon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.value = _uiState.value.copy(currentTemperatureError = true)
                        0.0
                    }
                }


                // Wait for all the coroutines to complete, and store the data in results
                val results = awaitAll(temperature, rain, wind, electricityPrice, currentPrice, forecast, currentTemperature)

                var prediction = MutableList(7) {0.0}
                // Train the MLR model, but only if we could load the APIs
                if(!_uiState.value.otherApiError){
                    trainMLR(
                        results[0] as DoubleArray,
                        results[1] as DoubleArray,
                        results[2] as DoubleArray,
                        results[3] as DoubleArray
                    )
                    // Store the prediction for the next 7 days in a variable
                    prediction = predictNext7DaysPrices(results[5] as List<DailyWeatherData>)
                }


                // Pretty print the training data and current price, as well as the prediction
                printData(
                    results[0] as DoubleArray,
                    results[1] as DoubleArray,
                    results[2] as DoubleArray,
                    results[3] as DoubleArray,
                    results[4] as Double,
                    results[5] as List<DailyWeatherData>,
                    prediction
                )



                // Place the data into _uiState
                _uiState.value = _uiState.value.copy(
                    currentPrice = results[4] as Double,
                    currentTemperature = results[6] as Double,
                    prediction = prediction,
                    forecast = results[5] as List<DailyWeatherData>
                )
                _isRefreshing.value = false
                _dataFetched.value = true

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Trains the multiple linear regression model
    fun trainMLR(
        temperature: DoubleArray,
        rain: DoubleArray,
        wind: DoubleArray,
        electricityPrice: DoubleArray
    ){
        mlr.trainLinearRegression(temperature, rain, wind, electricityPrice)
    }

    fun calculateApplianceCost(appliance: Appliance, pricePerKwh: Double): Double {
        val applianceCalc = ApplianceCalc()

        return applianceCalc.calculateApplianceCost(appliance, pricePerKwh)
    }


    // Predicts the electricity price of the next 7 days, from tomorrow
    // If the forecast list in UiState is empty, the function will throw an IllegalStateException.
    fun predictNext7DaysPrices(forecast: List<DailyWeatherData>): MutableList<Double> {
        val predictionList = mutableListOf<Double>()
        if(forecast.isEmpty()){
            throw IllegalStateException("Cannot predict next 7 days prices with empty forecast data.")
        }
        forecast.forEach {
            predictionList.add(mlr.predictPrice(it.airTemperature, it.precipitationSum, it.windSpeed))
        }
        return predictionList
    }

    //Returns a map of the next 7 weekdays as strings paired with the prediction for that day
    fun getNext7DaysWithPredictions(): Map<String, Float> {
        // Get predictions from UI state
        val predictions = _uiState.value.prediction

        // Throw error if predictions are empty
        if(predictions.isEmpty()){
            throw IllegalStateException("Cannot get next 7 days with predictions when predictions are empty.")
        }
        // Define the weekdays
        val weekdays = listOf("man", "tir", "ons", "tor", "fre", "lør", "søn")
        // Get current date
        val today = LocalDate.now()
        // Initialize map for days with predictions
        val daysWithPredictions = mutableMapOf<String, Float>()

        // Loop through next 7 days and add predictions to the map
        for (i in 0..6) {
            val prediction = predictions[i]
            val day = today.plusDays(i.toLong())
            val weekday = weekdays[day.dayOfWeek.value % 7]
            daysWithPredictions[weekday] = prediction.toFloat()
        }

        return daysWithPredictions
    }

    // Returns a pair with the lowest price of the week, together with the name of the weekday
    fun getDayWithLowestPrice(): Pair<String, Double> {
        // Get the days and predictions
        val map = getNext7DaysWithPredictions()
        // Initialise lowest entry
        var lowestEntry: Map.Entry<String, Float>? = null

        // Iterate through the entries to find the lowest value
        for (entry in map.entries) {
            if (lowestEntry == null || entry.value < lowestEntry.value) {
                lowestEntry = entry
            }
        }

        // Throw an exception if the lowest entry is null
        if (lowestEntry == null) {
            throw IllegalStateException("Unable to find the lowest price of the week.")
        }

        // Return the lowest entry as a pair with the name and the value
        return Pair(lowestEntry.key, lowestEntry.value.toDouble())
    }


    // Returns a map with the nest 7 weekday names mapped to DailyWeatherData objects
    fun getNext7DaysWithForecast(): Map<String, DailyWeatherData>{
        // Get predictions from UI state
        val forecast7Days = _uiState.value.forecast

        // Throw error if predictions are empty
        if(forecast7Days.isEmpty()){
            throw IllegalStateException("Cannot get next 7 days with forecast when forecast is empty.")
        }
        // Define the weekdays
        val weekdays = listOf("man", "tir", "ons", "tor", "fre", "lør", "søn")
        // Get current date
        val today = LocalDate.now()
        // Initialize map for days with predictions
        val daysWithForecast = mutableMapOf<String, DailyWeatherData>()

        // Loop through next 7 days and add predictions to the map
        for (i in 0..6) {
            val forecast = forecast7Days[i]
            val day = today.plusDays(i.toLong())
            val weekday = weekdays[day.dayOfWeek.value % 7]
            daysWithForecast[weekday] = forecast
        }

        return daysWithForecast

    }

    // Save the first launch flag to SharedPreferences
    fun saveFirstLaunchFlag(context: Context, isFirstLaunch: Boolean) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstLaunch", isFirstLaunch)
        editor.apply()
    }

    // Returns whether or not this is the first launch of the app
    fun isFirstLaunch(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFirstLaunch", true)
    }

    // Save location to SharedPreferences
    fun saveLocation(context: Context, location: Location) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("location", location.name)
        editor.apply()
    }

    // Load location from SharedPreferences
    fun loadLocation(context: Context): Location {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val locationName = sharedPreferences.getString("location", Location.Oslo.name)
        return Location.valueOf(locationName!!)
    }

    //Change the user's location based on area code.
    // A boolean parameter decides whether or not to call the update function, which is not always needed to do.
    fun changeLocation(context: Context, priceCode: String, callUpdate: Boolean = true) {
        val newLocation = when (priceCode) {
            "NO1" -> Location.Oslo
            "NO2" -> Location.Kristiansand
            "NO3" -> Location.Trondheim
            "NO4" -> Location.Tromsø
            "NO5" -> Location.Bergen
            else -> throw IllegalArgumentException("Invalid price code")
        }

        _uiState.value = _uiState.value.copy(location = newLocation)

        // Save the location to SharedPreferences
        saveLocation(context, newLocation)

        if (callUpdate) {
            update()
        }
    }

    // Save appliances to SharedPreferences
    fun saveAppliances(context: Context, appliances: List<Appliance>) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val appliancesJson = gson.toJson(appliances)
        editor.putString("appliances", appliancesJson)
        editor.apply()
    }

    // Load appliances from SharedPreferences
    fun loadAppliances(context: Context): List<Appliance> {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val appliancesJson = sharedPreferences.getString("appliances", "[]")
        val type = object : TypeToken<List<Appliance>>() {}.type
        return gson.fromJson(appliancesJson, type)
    }

    // Add a new appliance
    fun addAppliance(context: Context, name: String, usageWatts: Int, durationMinutes: Int ) {
        val updatedAppliances = _uiState.value.appliances.toMutableList()

        val appliance = Appliance(
            name = name,
            usageWatts = usageWatts,
            durationMinutes = durationMinutes
        )

        // Check if the appliance already exists in the list
        val applianceExists = updatedAppliances.any {
            it.name == appliance.name &&
                    it.usageWatts == appliance.usageWatts &&
                    it.durationMinutes == appliance.durationMinutes
        }

        // Add the appliance only if it doesn't already exist in the list
        if (!applianceExists) {
            updatedAppliances.add(appliance)
            _uiState.value = _uiState.value.copy(appliances = updatedAppliances)
            saveAppliances(context, updatedAppliances)
        }
    }


    // Remove an appliance
    fun removeAppliance(context: Context, appliance: Appliance) {
        val updatedAppliances = _uiState.value.appliances.toMutableList()
        updatedAppliances.remove(appliance)
        _uiState.value = _uiState.value.copy(appliances = updatedAppliances)
        saveAppliances(context, updatedAppliances)
    }



    // Pretty-prints the weather- and price data from the APIs, as well as the forecast and prediction
    private fun printData(
        temperature: DoubleArray,
        rain: DoubleArray,
        wind: DoubleArray,
        electricityPrice: DoubleArray,
        currentPrice: Double,
        forecast: List<DailyWeatherData>,
        prediction: MutableList<Double>
    ) {
        println("Temperature data:")
        println(temperature.contentToString())
        println(temperature.size)

        println("Rain data:")
        println(rain.contentToString())
        println(rain.size)

        println("Wind data:")
        println(wind.contentToString())
        println(wind.size)

        println("Electricity price data:")
        println(electricityPrice.contentToString())
        println(electricityPrice.size)

        println("Current price:")
        println(currentPrice)

        println("Forecast:")
        println(forecast)

        println("7 day prediction:")
        println(prediction)
    }
}