package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.in2000_team1.ui.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigation(viewModel: MainViewModel, startDestination: String) {
    val navController = rememberNavController()
    val dataFetched = viewModel.dataFetched.collectAsState()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = "home") {
            if (viewModel.uiState.collectAsState().value.otherApiError){
                APIErrorScreen {
                    viewModel.update()
                }
            }
            else if (dataFetched.value) {
                MainScreen(
                    predictionsAndDays = viewModel.getNext7DaysWithPredictions(),
                    dayAndForecast = viewModel.getNext7DaysWithForecast(),
                    location = viewModel.uiState.collectAsState().value.location,
                    currentPrice = viewModel.uiState.collectAsState().value.currentPrice,
                    currentTemp = viewModel.uiState.collectAsState().value.currentTemperature,
                    appliances = viewModel.uiState.collectAsState().value.appliances,
                    viewModel = viewModel,
                    navController = navController
                )

            }
            else {
                // Display the loading screen while waiting for the data
                LoadingScreen()
            }

        }
        composable(route = "applianceList"){
            if (dataFetched.value) {
                ApplianceScreen(
                    appliances = viewModel.uiState.collectAsState().value.appliances,
                    currentPrice = viewModel.uiState.collectAsState().value.currentPrice,
                    lowestPrice = viewModel.getDayWithLowestPrice(),
                    navController = navController,
                    onApplianceRemoved = { applianceToRemove ->
                        viewModel.removeAppliance(context, applianceToRemove)
                    },
                    viewModel = viewModel
                )

            }
            else if (viewModel.uiState.collectAsState().value.otherApiError){
                APIErrorScreen {
                    viewModel.update()
                }
            }
            else {
                // Display the loading screen while waiting for the data
                LoadingScreen()
            }
        }
        composable(route = "addAppliance"){
            AddApplianceScreen(
                onAddAppliance = {
                    navController.popBackStack("applianceList", false)
                },
                context = context,
                viewModel = viewModel
            )
        }
        composable(route = "info"){
            InformationScreen(onInformation = {
                navController.navigate("home")
            })

        }
        composable(route = "map"){
            CityScreen(navController = navController, viewModel = viewModel)

        }
    }
}
//ha knapp her