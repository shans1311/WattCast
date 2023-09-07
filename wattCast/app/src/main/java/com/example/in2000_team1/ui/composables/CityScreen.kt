package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.in2000_team1.R
import com.example.in2000_team1.ui.MainViewModel

@Composable
fun CityScreen(navController: NavController, viewModel: MainViewModel) {
    val image = painterResource(R.drawable.str_mkart) // The background image
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().background(Color(255, 249, 243))) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.padding(8.dp)) // Spacer element for top padding
            val logo = painterResource(R.drawable.logoin2000_4) // The logo image
            Image(painter = logo, contentDescription = "", modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.padding(4.dp)) // Spacer element for bottom padding
            Text(
                text = "Velg din by", // The title text
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                color = Color(2, 48, 71),
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 20.dp)
                    .zIndex(0f) // Set the z-index to 0
            )
        }
        Image(
            painter = image, // The background image
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .zIndex(0f), // Set the z-index to 0
            contentScale = ContentScale.Fit
        )

        PositionedButtons(
            image = image, // The background image
            buttons = listOf(
                "NO4" to "Tromsø",
                "NO3" to "Trondheim",
                "NO5" to "Bergen",
                "NO1" to "Oslo",
                "NO2" to "Kristiansand"
            ), // The list of city-region pairs to display as buttons
            onClick = { region ->
                viewModel.changeLocation(context, region, true) // Update the selected location
                navController.navigate("home") // Navigate to the home screen
            }
        ) { region, city, onClick ->
            Button(region = region, by = city, onClick = onClick) // Generate the content of each button
        }
    }
}


@Composable
fun Button(region: String, by: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        modifier = modifier
            .zIndex(1f), // Set the z-index to 1
        onClick = onClick, // Callback function to handle button clicks
        border = BorderStroke(1.dp, Color(209, 217, 230)), // Border style for the button
        shape = RoundedCornerShape(30), // Set the shape to a rounded rectangle with 30dp corner radius
        // or shape = CircleShape for circular buttons
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White) // Set the button color scheme
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp) // Set horizontal padding for the button content
        ) {
            Text(
                text = region, // The region code for the button
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 2.dp), // Set bottom padding for the region code
                color = Color(255, 153, 0) // The text color for the region code
            )
            Text(
                text = by, // The city name for the button
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color(2, 48, 71) // The text color for the city name
            )
        }
    }
}


@Composable
fun PositionedButtons(
    image: Painter, // The background image
    buttons: List<Pair<String, String>>, // The list of city-region pairs to display as buttons
    onClick: (String) -> Unit, // Callback function to handle button clicks
    content: @Composable (String, String, () -> Unit) -> Unit // Function to generate the content of each button
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp // Get the screen height

    // We use a Layout to achieve a very specific layout for the screen,
    // because thebuttons are requires to be placed in a specific spot above the image
    Layout(
        content = {
            // Loop through the button list and create the button content
            buttons.forEach { (region, city) ->
                content(region, city) { onClick(region) }
            }
        },
        measurePolicy = { measurables, constraints ->
            // Measure the constraints and calculate the button positions based on the screen size and aspect ratio of the image
            val placeables = measurables.map { it.measure(constraints) }
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val aspectRatio = image.intrinsicSize.width / image.intrinsicSize.height
            val scaledHeight = width / aspectRatio

            layout(width, height) {
                val yOffset = ((height - scaledHeight) / 2).toInt()

                // We check if the phone is a small one, before deciding on the placement of the buttons
                val positions = when (screenHeight) {
                    in 0..700 -> getButtonPositionsSmallPhone() // Use different positions for small screens
                    else -> getButtonPositions()
                }

                // Loop through the list of buttons and position them on the screen
                placeables.forEachIndexed { index, placeable ->
                    val (x, y) = positions[index]
                    placeable.placeRelative(
                        x = (width * x).toInt() - placeable.width / 2,
                        y = (scaledHeight * y).toInt() + yOffset - placeable.height / 2
                    )
                }
            }
        }
    )
}


fun getButtonPositions(): List<Pair<Float, Float>> {
    return listOf(
        Pair(0.74f, 0.38f), // Tromsø
        Pair(0.44f, 0.69f), // Trondheim
        Pair(0.24f, 0.8f), // Bergen
        Pair(0.6f, 0.8f), // Oslo
        Pair(0.4f, 0.92f)  // Kristiansand
    )
}
fun getButtonPositionsSmallPhone(): List<Pair<Float, Float>> {
    return listOf(
        Pair(0.74f, 0.38f), // Tromsø
        Pair(0.44f, 0.63f), // Trondheim
        Pair(0.24f, 0.73f), // Bergen
        Pair(0.6f, 0.73f), // Oslo
        Pair(0.4f, 0.85f)  // Kristiansand
    )
}

