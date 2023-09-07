package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.in2000_team1.datalayer.*
import com.example.in2000_team1.ui.Location
import com.example.in2000_team1.ui.MainViewModel
import com.example.in2000_team1.ui.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MainScreen(
    predictionsAndDays: Map<String, Float>,
    dayAndForecast: Map<String, DailyWeatherData>,
    location: Location,
    currentPrice: Double,
    currentTemp: Double,
    appliances: MutableList<Appliance>,
    viewModel: MainViewModel,
    navController: NavController
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    // Display the main content when the data is fetched
    var menuOpen by remember { mutableStateOf(false) }

    // Define the SwipeRefresh composable for refreshing data when the user swipes down
    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing), onRefresh = { viewModel.update() }) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(PrimaryColor)
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val fontSize = when (LocalConfiguration.current.screenHeightDp) {
                in 0..700 -> 25.sp // Small screens
                else -> 40.sp
            }

            Column(modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .weight(1.5f)) {
                // Location name, with font size decided by screen size
                Text(
                    text = location.name,
                    color = AccentColor,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    modifier = Modifier.fillMaxWidth()

                )
            }
            Column(modifier = Modifier.weight(7.5f)) {


                // The two information boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Box with the "right now" information
                    Box(Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .aspectRatio(1f)
                        .fillMaxWidth(0.5f)
                        .background(color = SecondaryColor1, shape = RoundedCornerShape(16.dp))) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Akkurat nå:", color = Color.White, fontSize = 14.sp)
                            Text(
                                "${String.format("%.0f", 100 * currentPrice)} øre/kWh",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontFamily = OpenSansBold
                            )
                            if (!viewModel.uiState.collectAsState().value.currentTemperatureError) {
                                Text(
                                    "$currentTemp °C",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = OpenSansBold
                                )
                            } else {
                                Text(
                                    "Temp: N/A",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = OpenSansBold
                                )
                            }
                        }
                    }

                    // Box for the appliances. Can be clicked to navigate to the appliance list
                    Box(Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .aspectRatio(1f)
                        .fillMaxWidth(0.5f)
                        .background(color = SecondaryColor2, shape = RoundedCornerShape(16.dp))
                        .clickable { navController.navigate("applianceList") }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
                                    .align(Alignment.Center),
                        ) {
                                if (appliances.isEmpty()) {

                                        Text("Legg til apparater:", color = Color.White, fontSize = 12.sp, )
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Add",

                                            modifier = Modifier.size(48.dp),
                                            tint = Color.White
                                        )


                                } else {
                                    val appliancePriceNow = String.format("%.2f", viewModel.calculateApplianceCost(appliances.last(), currentPrice))

                            Box(modifier = Modifier
                                .align(Alignment.End)
                                .padding(bottom = 12.dp)) {

                                Icon(
                                    Icons.Rounded.MoreVert,
                                    contentDescription = "To Appliance Page",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(top = 8.dp, end = 8.dp),
                                    tint = Color.White
                                )
                            }

                            Column(modifier = Modifier,horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = ParagraphStyle(lineHeight = 40.sp)) {
                                                withStyle(style = SpanStyle(color = Color.White, fontSize= 32.sp, fontWeight = FontWeight.SemiBold)) {
                                                    append(appliancePriceNow)
                                                }
                                                withStyle(style = SpanStyle(color = Color.White, fontSize=16.sp, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)){
                                                    append("Kr")
                                                }

                                            }

                                        },
                                    )
                                }

                                Box(){
                                    Text(
                                        text = "Pris nå",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Light,
                                        textAlign = TextAlign.Justify
                                    )
                                }
                            }

                            Column(modifier = Modifier
                                .align(Alignment.Start)
                                .fillMaxSize()
                                .padding(start = 8.dp),
                            verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = appliances.last().name,
                                    color = Color.White,
                                    fontFamily = OpenSansBold,
                                    fontSize = 12.sp
                                )

                                Text(
                                    text = "${appliances.last().durationMinutes}min",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }

                    }
                }
            }


                // The two charts
                PriceChart(predictions = predictionsAndDays)
                WeatherChart(dayAndForecast = dayAndForecast)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(!menuOpen) {
                    HamburgerMenuButton {
                        menuOpen = true
                    }
                }
            }
        }
    }


    if (menuOpen) {
        MenuOverlay(navController = navController, onClose = { menuOpen = false })
    }

}











