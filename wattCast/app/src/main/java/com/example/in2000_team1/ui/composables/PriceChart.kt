package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_team1.ui.theme.*
import kotlin.math.round


@Composable
fun PriceChart(predictions: Map<String, Float>) {
    // Multiply the predictions by 100 and round to 0 decimals
    val roundedPredictions = predictions.mapValues { (round(it.value * 100)) }

    val maxPrediction = roundedPredictions.values.maxOrNull() ?: 0f // Find maximum prediction value
    val minPrediction = roundedPredictions.values.minOrNull() ?: 0f // Find minimum prediction value


    // Get the screenheight, so we can display a smaller chart on a shorter screen
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val barHeight = when (screenHeight) {
        in 0..700 -> 88.dp // Small screens
        else -> 112.dp
    }

    val boxHeight = when (screenHeight) {
        in 0..700 -> 96.dp // Small screens
        else -> 112.dp
    }

    val barWidth = 27.dp

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ){
            Text("Estimat for fremtidige strømpriser - øre/kWh",
                color = TextColor2,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // We display a bar for each prediction and day
                    for ((day, prediction) in roundedPredictions) {
                        val scaledHeight = (prediction - minPrediction) / (maxPrediction - minPrediction)
                        Column(modifier = Modifier
                            .width(barWidth)
                            .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(boxHeight)
                                    .width(barWidth)
                                    .background(Color.Transparent)
                            ) {
                                Column(modifier = Modifier.align(Alignment.BottomCenter), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(String.format("%.0f", prediction), textAlign = TextAlign.Center, color = TextColor2, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(topEnd = 6.dp, topStart = 6.dp))
                                            .height(barHeight * scaledHeight + 10.dp) // Calculate the height of the bar
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = OrangeGradient
                                                )
                                            )
                                    ) {}
                                }
                            }
                            Text(day,
                                textAlign = TextAlign.Center,
                                color = TextColor2,
                                fontSize = 12.sp,
                            )
                        }

                    }
                }
            }
        }
    }


}






