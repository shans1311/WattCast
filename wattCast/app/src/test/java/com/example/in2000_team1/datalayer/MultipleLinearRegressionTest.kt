package com.example.in2000_team1.datalayer

import org.junit.Assert.*
import org.junit.Test

class MultipleLinearRegressionTest {

    //Tests that the intercepot and slopes are reasonable
    @Test
    fun testTrainLinearRegression() {
        // Create sample training data
        val temperature = doubleArrayOf(20.0, 22.0, 18.0, 21.0, 25.5, 27.8)
        val rain = doubleArrayOf(0.0, 1.0, 2.0, 0.5, 0.0, 0.0)
        val wind = doubleArrayOf(5.0, 8.0, 10.0, 6.0, 0.0, 0.0)
        val electricityPrice = doubleArrayOf(0.5, 0.6, 0.3, 0.8, 0.2, 0.1)


        // Train the model
        val mlr = MultipleLinearRegression()
        mlr.trainLinearRegression(temperature, rain, wind, electricityPrice)

        // Check that the model parameters are within a reasonable range
        assertTrue(mlr.intercept > -1 && mlr.intercept < 1)
        assertTrue(mlr.temperatureSlope > 0 && mlr.temperatureSlope < 1)
        assertTrue(mlr.rainSlope > -1 && mlr.rainSlope < 1)
        assertTrue(mlr.windSlope > -1 && mlr.windSlope < 1)
    }

    //Tests that the predicted price is a reasonable value
    @Test
    fun testPredictPrice() {
        // Create a trained model with sample data
        val temperature = doubleArrayOf(20.0, 22.0, 18.0, 21.0, 25.5, 27.8)
        val rain = doubleArrayOf(0.0, 1.0, 2.0, 0.5, 0.0, 0.0)
        val wind = doubleArrayOf(5.0, 8.0, 10.0, 6.0, 0.0, 0.0)
        val electricityPrice = doubleArrayOf(0.5, 0.6, 0.3, 0.8, 0.2, 0.1)
        val mlr = MultipleLinearRegression()
        mlr.trainLinearRegression(temperature, rain, wind, electricityPrice)

        // Test the predictPrice function with sample input values
        val nextDayTemperature = 19.0
        val nextDayRain = 0.5
        val nextDayWind = 7.0
        val predictedPrice = mlr.predictPrice(nextDayTemperature, nextDayRain, nextDayWind)
        println("PREDICTED PRICE: $predictedPrice")
        assertTrue(predictedPrice > 0 && predictedPrice < 1)
    }


    //Tests that the training of the model throws an exception when arrays have different lengths
    @Test(expected = IllegalArgumentException::class)
    fun testTrainLinearRegressionWithInvalidData() {
        // Create training data with different array lengths
        val temperature = doubleArrayOf(20.0, 22.0, 18.0, 21.0)
        val rain = doubleArrayOf(0.0, 1.0, 2.0)
        val wind = doubleArrayOf(5.0, 8.0, 10.0, 6.0)
        val electricityPrice = doubleArrayOf(0.5, 0.6, 0.3, 0.8)

        // Train the model with invalid data (arrays with different lengths)
        val regression = MultipleLinearRegression()
        regression.trainLinearRegression(temperature, rain, wind, electricityPrice)
    }
}
