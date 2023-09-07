package com.example.in2000_team1.datalayer

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression

class MultipleLinearRegression {

    var intercept: Double = 0.0
    var temperatureSlope: Double = 0.0
    var rainSlope: Double = 0.0
    var windSlope: Double = 0.0

    // Train a multiple linear regression model to predict the electricity price for the next day based on weather data
    fun trainLinearRegression(
        temperature: DoubleArray,
        rain: DoubleArray,
        wind: DoubleArray,
        electricityPrice: DoubleArray
    ) {
        // Ensure all arrays have the same length
        if (temperature.size != electricityPrice.size || rain.size != electricityPrice.size || wind.size != electricityPrice.size) {
            throw IllegalArgumentException("All arrays must have the same length.")
        }

        // Prepare the training data: construct a matrix with n rows and 3 columns
        val n = temperature.size
        val x = Array(n) { DoubleArray(3) }
        for (i in 0 until n) {
            x[i][0] = temperature[i]
            x[i][1] = rain[i]
            x[i][2] = wind[i]
        }

        // Train the model using OLS(ordinary least squares) multiple linear regression
        val ols = OLSMultipleLinearRegression()
        ols.newSampleData(electricityPrice, x)

        // Extract the model parameters
        val parameters = ols.estimateRegressionParameters()
        intercept = parameters[0]
        temperatureSlope = parameters[1]
        rainSlope = parameters[2]
        windSlope = parameters[3]
    }

    fun predictPrice(
        nextDayTemperature: Double,
        nextDayRain: Double,
        nextDayWind: Double
    ): Double {
        // Use the model to predict the electricity price for the next day
        // with the formula: y = b0 + b1*x1 + b2*x2 + . . . + bn*xn
        return intercept + temperatureSlope * nextDayTemperature + rainSlope * nextDayRain + windSlope * nextDayWind
    }

}
