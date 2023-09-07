package com.example.in2000_team1.datalayer

import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DataSource {
    val apiKey = "e6e9b41a-7976-449c-9b5d-5551171aac89"

    fun createFrostApiService(): FrostApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gw-uio.intark.uh-it.no/in2000/frostapi/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(FrostApiService::class.java)
    }

    fun createPriceApiService(): PriceApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.hvakosterstrommen.no/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(PriceApiService::class.java)
    }
    fun createForecastApiService(): ForecastApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gw-uio.intark.uh-it.no/in2000/weatherapi/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(ForecastApiService::class.java)
    }

    fun createNowCastApiService(): NowCastApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gw-uio.intark.uh-it.no/in2000/weatherapi/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(NowCastApiService::class.java)
    }


    // This function retrieves the weather data for a specific element ID for the last 60 days
    suspend fun fetchWeatherDataForLast60Days(elementId: String, sources: String, apiService: FrostApiService = createFrostApiService()): DoubleArray = withContext(Dispatchers.IO) {
        // Define the sources, today's date, and the date range for the last 60 days
        val today = LocalDate.now().minusDays(1)
        val firstDay = today.minusDays(59)

        // Build the reference time string for the API call
        val referenceTime = "${firstDay.format(DateTimeFormatter.ISO_LOCAL_DATE)}/${today.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)}"

        // Call the API to retrieve the weather observations for the specified element ID and date range
        val response = apiService.getObservations(sources, elementId, referenceTime, apiKey)
        if (!response.isSuccessful) throw IllegalStateException("Failed to fetch weather data for $elementId for last 60 days: ${response.code()} - ${response.message()}")

        // Extract the weather observation data for each date in the date range
        val observationResponse = response.body() ?: throw IllegalStateException("Failed to fetch weather data for $elementId for last 60 days: ${response.code()} - ${response.message()}")
        val dataByDate = observationResponse.data.groupBy { it.referenceTime.split("T")[0] } // group by date. Split by T because of the date format in the API

        // Create an array to store the weather data for each day in the date range
        val daysInPeriod = ChronoUnit.DAYS.between(firstDay, today).toInt() + 1
        val weatherData = DoubleArray(daysInPeriod)

        // Retrieve the weather data for each day in the date range
        for (day in 0 until daysInPeriod) {
            val dateString = firstDay.plusDays(day.toLong()).toString()
            val data = dataByDate[dateString]
            val weatherValue = data?.flatMap { it.observations }?.find { it.elementId == elementId }?.value
            if (weatherValue != null) weatherData[day] = weatherValue
        }

        // Return the array of weather data
        weatherData
    }


    // This function retrieves the average prices for a specific price area for the last 60 days
    suspend fun getAveragePricesForLast60Days(priceArea: String, apiService: PriceApiService = createPriceApiService()): DoubleArray = withContext(Dispatchers.IO) {

        // Define the number of days in the period
        val daysInPeriod = 60

        // Create a list of deferred prices using the `async` function
        val deferredPrices = List(daysInPeriod) { day ->
            async {
                // Calculate the date for each day in the period by subtracting the number of days from today
                val date = LocalDate.now().minusDays(daysInPeriod - day.toLong())

                // Use the API service to retrieve the prices for the specified date and price area.
                val prices = apiService.getPrices(date.year,
                    date.monthValue.toString().padStart(2, '0'), // Ensure month is zero-padded with at least 2 digits
                    date.dayOfMonth.toString().padStart(2, '0'), // Ensure day is zero-padded with at least 2 digits
                    priceArea
                )

                // Calculate the daily average by dividing the sum of prices by the number of prices
                prices.sumOf { it.NOK_per_kWh } / prices.size
            }
        }

        // Wait for all deferred computations to complete and retrieve the results as an array of doubles
        deferredPrices.awaitAll().toDoubleArray()
    }

    // This function retrieves the current electricity price for a given price area
    suspend fun getCurrentElectricityPrice(priceArea: String, apiService: PriceApiService = createPriceApiService()): Double = withContext(Dispatchers.IO) {
            // Get the current datetime
            val currentDateTime = LocalDateTime.now()

            // Get the prices for the current day and price area from the API service
            val prices = apiService.getPrices(
                currentDateTime.year,
                currentDateTime.monthValue.toString().padStart(2, '0'), // Ensure month is zero-padded with at least 2 digits
                currentDateTime.dayOfMonth.toString().padStart(2, '0'), // Ensure day is zero-padded with at least 2 digits
                priceArea
            )

            // Find the price for the current hour
            val currentHourPrice = prices.firstOrNull { price ->
                // Parse the start time string into a LocalDateTime object
                // ISO_OFFSET_DATE_TIME is the format of the time_start string in the API, which is a standard format for date-time with an offset from UTC.
                val priceDateTime = LocalDateTime.parse(price.time_start, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                // Check if the price start hour and day match the current hour and day
                priceDateTime.hour == currentDateTime.hour && priceDateTime.dayOfMonth == currentDateTime.dayOfMonth
            }

            // Return the NOK per kWh price if found, otherwise throw an exception
            currentHourPrice?.NOK_per_kWh ?: throw IllegalStateException("Failed to fetch current electricity price for $priceArea")
        }

    //Fetches the current temperature at a given location with the NowCast API
    suspend fun getCurrentTemperature(lat: Double, lon: Double, apiService: NowCastApiService = createNowCastApiService()): Double {
        //Make API call for the NowCast data
        val response = apiService.getNowCast(lat, lon, apiKey)
        if (!response.isSuccessful) throw IllegalStateException("Failed to fetch weather data for $lat, $lon : ${response.code()} - ${response.message()}")

        //Extract the air temperature from the
        val nowCast = response.body()
        val currentTemp = nowCast?.properties?.timeseries?.get(0)?.data?.instant?.details?.air_temperature ?: throw IllegalStateException("Current temperature is null")

        return currentTemp
    }


    // Fetches weather forecast for the next 7 days (excluding today),
    // and calculates daily mean air temperature, precipitation sum, and mean wind speed
    suspend fun getForecastForNext7Days(lat: Double, lon: Double, apiService: ForecastApiService = createForecastApiService()): List<DailyWeatherData> {
        // Make API call for the forecast data
        val response = apiService.getForecast(lat, lon, apiKey)
        if (!response.isSuccessful) throw IllegalStateException("Failed to fetch weather data for $lat, $lon : ${response.code()} - ${response.message()}")

        // Group forecast data by date
        val forecast = response.body()
        val dataByDate = forecast?.properties?.timeseries?.groupBy { it.time.split("T")[0] }

        // Calculate precipitation sums for each day, from 06.00-06.00 UTC
        val precipSums = sumPrecipitation(dataByDate)

        // Create a list to store daily weather data
        val dailyWeatherDataList = mutableListOf<DailyWeatherData>()

        // Loop through each day in the forecast (skipping the first day, today)
        dataByDate?.entries?.forEachIndexed { index, entry ->
            if (index == 0) return@forEachIndexed

            val date = entry.key
            val timeSeriesList = entry.value

            // Get precipitation sum for the current day
            val precipSum = precipSums[date] ?: 0.0

            // Extract temperature and wind speed data
            val airTemperatureList = timeSeriesList.filter { it.data.instant != null }.map { it.data.instant.details.air_temperature }
            val windSpeedList = timeSeriesList.filter { it.data.instant != null }.map { it.data.instant.details.wind_speed }

            // Calculate mean air temperature and mean wind speed
            val meanAirTemperature = if (airTemperatureList.isNotEmpty()) airTemperatureList.average() else 0.0
            val meanWindSpeed = if (windSpeedList.isNotEmpty()) windSpeedList.average() else 0.0

            // Add the calculated data to the list
            dailyWeatherDataList.add(DailyWeatherData(date, meanAirTemperature, precipSum, meanWindSpeed))
        }

        // Return the list of the next 7 days (not including today)
        return dailyWeatherDataList.take(7)
    }


    // This function calculates the total precipitation for each date in
    // the input Map dataByDate and returns a Map with the date as the key and the total precipitation as the value.
    // Since Frost API's sum(precipitation_amount P1D) sums the precipitation from 06.00 to 06.00 UTC,
    // we need to also sum precipitation from 06.00-06.00 UTC for the forecast data.
    private fun sumPrecipitation(dataByDate: Map<String, List<Forecast.Properties.TimeSeries>>?): MutableMap<String, Double> {

        // Initialize an empty MutableMap to store the sum of precipitation for each date.
        val sumMap = mutableMapOf<String, Double>()

        // Extract the list of dates from dataByDate.
        val dateList = dataByDate?.keys?.toList()

        // Iterate through each entry in dataByDate.
        dataByDate?.entries?.forEachIndexed { index, entry ->
            val date = entry.key
            val timeSeriesList = entry.value
            var sum = 0.0

            // Calculate the sum of precipitation for the current date, from 06.00 to 00.00
            timeSeriesList.forEach {
                val hour = it.time.split("T")[1].substring(0, 2).toInt()
                if ((hour == 6 || hour == 12 || hour == 18) && it.data.next_6_hours != null) {
                    sum += it.data.next_6_hours.details.precipitation_amount
                }
            }

            // If there's a next day in the data, add the precipitation for the next 6 hours to the sum
            if (index < dateList!!.size - 1) {
                val nextDay = dateList[index + 1]
                val nextDayTimeSeriesList = dataByDate[nextDay]
                val nextDayMidnightIndex = findNextDayMidnightIndex(nextDayTimeSeriesList)

                if (nextDayMidnightIndex != -1 && nextDayTimeSeriesList!![nextDayMidnightIndex].data.next_6_hours != null) {
                    sum += nextDayTimeSeriesList[nextDayMidnightIndex].data.next_6_hours.details.precipitation_amount
                }
            }

            // Store the sum of precipitation for the current date in sumMap.
            sumMap[date] = sum
        }

        // Return the sumMap with the total precipitation for each date.
        return sumMap
    }

    // This function returns the index of the midnight hour (00:00) in the
    // input timeSeriesList. If the midnight hour is not found, it returns -1.
    private fun findNextDayMidnightIndex(timeSeriesList: List<Forecast.Properties.TimeSeries>?): Int {
        timeSeriesList?.forEachIndexed { index, it ->
            val hour = it.time.split("T")[1].substring(0, 2).toInt()
            if (hour == 0) {
                return index
            }
        }
        return -1
    }
}