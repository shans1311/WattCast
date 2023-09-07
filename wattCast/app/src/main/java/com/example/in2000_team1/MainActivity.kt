package com.example.in2000_team1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.in2000_team1.ui.MainViewModel
import com.example.in2000_team1.ui.composables.Navigation
import com.example.in2000_team1.ui.theme.IN2000_team1Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var startDestination = "home"

        // Check if this is the first tie launching the app on the device
        if (viewModel.isFirstLaunch(this)) {
            startDestination = "map"

            viewModel.saveFirstLaunchFlag(this, false)
        }

        // Load the user's appliances from SharedPreferences
        viewModel.loadAppliances(this).forEach { appliance ->
            viewModel.addAppliance(this, appliance.name, appliance.usageWatts, appliance.durationMinutes)
        }

        // Load the user's location from SharedPreferences
        val initialLocation = viewModel.loadLocation(this)
        viewModel.changeLocation(this, initialLocation.priceCode, callUpdate = false)

        setContent {
            IN2000_team1Theme {
                Navigation(viewModel = viewModel, startDestination = startDestination)
            }
        }

        lifecycleScope.launch {
            viewModel.update()
        }

    }
}



