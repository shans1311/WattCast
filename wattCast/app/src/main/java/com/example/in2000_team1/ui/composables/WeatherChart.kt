package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import com.example.in2000_team1.ui.theme.*
import com.example.in2000_team1.datalayer.*

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun WeatherChart(dayAndForecast: Map<String, DailyWeatherData>) {
    val maxTemp = dayAndForecast.values.maxOfOrNull { it.airTemperature } ?: 0.0
    val minTemp = dayAndForecast.values.minOfOrNull { it.airTemperature } ?: 0.0
    val maxRain = dayAndForecast.values.maxOfOrNull { it.precipitationSum } ?: 0.0
    val minRain = dayAndForecast.values.minOfOrNull { it.precipitationSum } ?: 0.0
    val maxWind = dayAndForecast.values.maxOfOrNull { it.windSpeed } ?: 0.0
    val minWind = dayAndForecast.values.minOfOrNull { it.windSpeed } ?: 0.0

    // Get the height of the screen so we can display a smaller graph on shorter screens
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val barHeight = when (screenHeight) {
        in 0..700 -> 80.dp // Small screens
        else -> 104.dp
    }

    val boxHeight = when (screenHeight) {
        in 0..700 -> 96.dp // Small screens
        else -> 112.dp
    }

    val barWidth = 24.dp

    val (overlayText, setOverlayText) = remember { mutableStateOf("") }
    val (showOverlay, setShowOverlay) = remember { mutableStateOf(false) }
    val (overlayPosition, setOverlayPosition) = remember { mutableStateOf(Offset.Zero) }
    val positions = remember { mutableStateMapOf<String, Offset>() }

    Box(){
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ){
                // Row for the dots and descriptive text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dotSize = 12.dp
                    val dotModifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)

                    Row() {
                        Box(
                            modifier = dotModifier
                                .background(TempColor)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Temperatur", color = TextColor2, textAlign = TextAlign.Center, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(24.dp))

                    Row() {
                        Box(
                            modifier = dotModifier
                                .background(WindColor)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vind",color = TextColor2, textAlign = TextAlign.Center, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Row() {
                        Box(
                            modifier = dotModifier
                                .background(RainColor)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nedbør", color = TextColor2, textAlign = TextAlign.Center, fontSize = 10.sp)
                    }

                }

                //Row for the bar graph
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // We display three bars for each forecast and day
                    for ((day, forecast) in dayAndForecast) {
                        val scaledTemp = (forecast.airTemperature - minTemp) / (maxTemp - minTemp)
                        val scaledRain = (forecast.precipitationSum - minRain) / (maxRain - minRain)
                        val scaledWind = (forecast.windSpeed - minWind) / (maxWind - minWind)


                        Column(
                            modifier = Modifier
                                .width(barWidth + 8.dp)
                                .padding(vertical = 8.dp)
                                .onGloballyPositioned { coordinates ->
                                    positions[day] = coordinates.positionInParent()
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(boxHeight)
                                    .width(barWidth)
                                    .background(Color.Transparent)
                                    .pointerInput(Unit) {
                                        // We display the info corresponding to the bars the user is tapping
                                        detectTapGestures(
                                            onPress = { offset ->
                                                val infoText = "Temp: ${String.format("%.2f", forecast.airTemperature)}°C\n" +
                                                        "Nedb: ${String.format("%.2f", forecast.precipitationSum)}mm\n" +
                                                        "Vind: ${String.format("%.2f", forecast.windSpeed)}m/s"
                                                setOverlayText(infoText)
                                                val newPosition = positions[day]!! + offset
                                                setOverlayPosition(newPosition)
                                                setShowOverlay(true)
                                                tryAwaitRelease()
                                                setShowOverlay(false)
                                            }
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(barHeight * scaledTemp.toFloat() + 10.dp) //Calculate the height of the bar
                                            .width(barWidth / 3)
                                            .clip(RoundedCornerShape(topEnd = 6.dp, topStart = 6.dp))
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = OrangeGradient
                                                )
                                            )
                                    ) {}
                                    Box(
                                        modifier = Modifier
                                            .height(barHeight * scaledWind.toFloat() + 10.dp) //Calculate the height of the bar
                                            .width(barWidth / 3)
                                            .clip(RoundedCornerShape(topEnd = 6.dp, topStart = 6.dp))
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = WindGradient
                                                )
                                            )
                                    ) {}
                                    Box(
                                        modifier = Modifier
                                            .height(barHeight * scaledRain.toFloat() + 10.dp) //Calculate the height of the bar
                                            .width(barWidth / 3)
                                            .clip(RoundedCornerShape(topEnd = 6.dp, topStart = 6.dp))
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = RainGradient
                                                )
                                            )
                                    ) {}
                                }
                            }
                            Text(day, color = TextColor2, textAlign = TextAlign.Center, fontSize = 12.sp)
                        }
                        if (day != dayAndForecast.keys.last()) {
                            Spacer(modifier = Modifier.width(0.dp))
                        }
                    }
                }
            }
        }

        // Display the overlay with the info is showOverlay is true
        if (showOverlay) {
            WeatherInfoOverlay(
                text = overlayText,
                modifier = Modifier.offset {
                    IntOffset(overlayPosition.x.roundToInt(), (overlayPosition.y.roundToInt()-500))
                }
            )
        }

    }


}

// The overlay with weather info
@Composable
fun WeatherInfoOverlay(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .zIndex(1f) // Add zIndex to ensure the overlay is above other content
            .padding(8.dp),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(4.dp),
        color = Color.Black // Add a background color here
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier
                .padding(12.dp)
        )
    }
}









