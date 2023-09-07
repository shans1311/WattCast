package com.example.in2000_team1.datalayer

//These are data structures for storing the results from the APIs

// Data class for storing weather data for the forecasts, per day
data class DailyWeatherData(
    val date: String,
    val airTemperature: Double,
    val precipitationSum: Double,
    val windSpeed: Double
)

// Data class for the user's appliances
data class Appliance(
    val name: String,
    val usageWatts: Int,
    val durationMinutes: Int
)

// Data class for the hvakosterstrommen.no API
data class Price(
    val NOK_per_kWh: Double,
    val EUR_per_kWh: Double,
    val EXR: Double,
    val time_start: String,
    val time_end: String
)

// Data class for the Locationforecast API, and also for the NowCast API. Both from api.met.no
data class Forecast(
    val properties: Properties
){
    data class Properties(
        val timeseries: List<TimeSeries>
    ){
        data class TimeSeries(
            val time: String,
            val data: Data
        ){
            data class Data(
                val instant: Instant,
                val next_1_hours: Next_1_Hours,
                val next_6_hours: Next_6_Hours
            ){
                data class Next_1_Hours(
                    val details: Details
                )
                data class Next_6_Hours(
                    val details: Details

                )
                data class Instant(
                    val details: Details
                )

                data class Details(
                    val air_temperature: Double,
                    val precipitation_amount: Double,
                    val wind_speed: Double
                )

            }
        }
    }
}

// Data class for the responses from the Frost API
data class ObservationResponse(
    val data: List<Data>,
    val queryTime: Double
) {
    data class Data(
        val referenceTime: String,
        val sourceId: String,
        val observations: List<Observation>
    ) {
        data class Observation(
            val elementId: String,
            val timeOffset: String?,
            val value: Double,
            val unit: String
        )
    }
}