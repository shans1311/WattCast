package viewmodel

import com.example.in2000_team1.ui.MainViewModel
import org.junit.Assert.*
import com.example.in2000_team1.datalayer.*
import org.junit.Before

import org.junit.Test

class MainViewModelTest {
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel()
    }

    @Test
    fun testPredictNext7DaysPrices() {

        // Mock the data
        val mockForecastData = listOf(
            DailyWeatherData("2023-05-12", 15.0, 5.0, 3.0),
            DailyWeatherData("2023-05-13", 16.0, 4.0, 2.0),
            DailyWeatherData("2023-05-14", 17.0, 3.0, 1.0),
            DailyWeatherData("2023-05-15", 18.0, 2.0, 1.5),
            DailyWeatherData("2023-05-16", 19.0, 1.0, 2.0),
            DailyWeatherData("2023-05-17", 20.0, 0.5, 2.5),
            DailyWeatherData("2023-05-18", 21.0, 0.0, 3.0)
        )

        // Use a Random object to create variability in your mock data
        val random = java.util.Random()

        // Mock training data for the MLR model
        val mockTemperatureData = DoubleArray(60) { 15.0 + random.nextGaussian() * 5 }  // mean 15, std dev 5
        val mockRainData = DoubleArray(60) { 5.0 + random.nextGaussian() * 2 }  // mean 5, std dev 2
        val mockWindData = DoubleArray(60) { 2.5 + random.nextGaussian() * 1 }  // mean 2.5, std dev 1
        val mockPriceData = DoubleArray(60) { 1.2 + random.nextGaussian() * 0.2 }  // mean 1.2, std dev 0.2



        // Train the MLR model
        viewModel.trainMLR(mockTemperatureData, mockRainData, mockWindData, mockPriceData)

        // Call the function and get the result
        val result = viewModel.predictNext7DaysPrices(mockForecastData)

        // Print the result
        result.forEachIndexed { index, prediction ->
            println("Day ${index + 1}: $prediction")
        }

        // Assert that the result has 7 elements
        assertEquals(7, result.size)

    }

}
