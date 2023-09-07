package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.in2000_team1.datalayer.*
import com.example.in2000_team1.ui.MainViewModel
import com.example.in2000_team1.ui.theme.*
import java.util.*

@Composable
@ExperimentalFoundationApi
fun ApplianceScreen(
    appliances: MutableList<Appliance>, // A list of appliances to display
    currentPrice: Double, // The current price of energy
    lowestPrice: Pair<String, Double>, // The lowest price of energy and its weekday
    navController: NavController, // A NavController for navigating to other screens
    onApplianceRemoved: (Appliance) -> Unit, // A callback for when an appliance is removed
    viewModel: MainViewModel // the viewmodel
) {
    // Initialize a mutable state variable for the menu overlay
    var menuOpen by remember { mutableStateOf(false) }

    // Compose the UI elements for the upper part of the UI
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(PrimaryColor)
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the header text
        Column(modifier = Modifier.weight(2f)) {
            Spacer(modifier = Modifier.padding(16.dp))
            Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                        Text(
                            "Plan",
                            color = AccentColor,
                            fontSize = 40.sp,
                        )

                    Box(modifier = Modifier.align(Alignment.CenterVertically)){
                        AddButton {
                            navController.navigate("addAppliance")
                        }
                    }

            }


            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                "Mine Apparater",
                color = SecondaryColor1,
                textAlign = TextAlign.Left,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()

            )
        }

        //Create LazyColumn for list of appliances
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(appliances) { appliance ->
                ApplianceBox( //call ApplianceBox function
                    appliance = appliance,
                    currentPrice = currentPrice,
                    lowestPrice = lowestPrice,
                    onApplianceRemoved = onApplianceRemoved,
                    viewModel = viewModel
                )
            }
        }

        //get length of appliances. If length is over 6, indicate that the lazy column is scrollable with a fade-effect
        val appliancesCount = Collections.unmodifiableList(appliances) //turn mutablelist of appliances into normal list
        if(appliancesCount.size > 6)( //check length
                Box( //fadeout effect
                    Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp)
                        .height(20.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PrimaryColor

                                )
                            )
                        )
                )
        )



        // Display the hamburger menu button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BackButton(icon = Icons.Default.Close) {
                navController.navigate("home")
            }
        }
    }

    // If the menu is open, display the menu overlay
    if (menuOpen) {
        MenuOverlay(navController = navController, onClose = { menuOpen = false })
    }
}

@Composable
fun ApplianceBox(
    appliance: Appliance, // The appliance to display
    currentPrice: Double, // The current price of energy
    lowestPrice: Pair<String, Double>, // The lowest price of energy and its weekday
    onApplianceRemoved: (Appliance) -> Unit, // A callback for when the appliance is removed
    viewModel: MainViewModel
) {

    // Calculate the appliance cost at the current price and the lowest price
    val appliancePriceNow = String.format("%.2f", viewModel.calculateApplianceCost(appliance, currentPrice))
    val appliancePriceLowest = String.format("%.2f", viewModel.calculateApplianceCost(appliance, lowestPrice.second))
    val applianceDuration = appliance.durationMinutes

    // Compose the UI elements for the appliance box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                BorderGray,
                shape = RoundedCornerShape(20.dp),
            )
            .background(Color.White),


    ) {

        //Compose the UI elements inside the appliance box
            //the appliance box consists of: a row of 3 boxes, each box containing a column, with each column containing
            //2 box elements, with each inner box containing text.
        Row(modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround) {
            var padding by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current.density

            Box(modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp, top = 16.dp, start = 12.dp))
            {
                Column {
                    Box(modifier = Modifier
                         ){
                        Text(
                            modifier = Modifier,

                            text = appliance.name.replaceFirstChar { //always first letter capitalied
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            },
                            color = SecondaryColor1,
                            fontSize = 16.sp,
                            fontWeight = SemiBold,
                            fontStyle = FontStyle.Italic,

                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,

                            onTextLayout = {
                                val lineCount = it.lineCount
                                val height = (it.size.height/density).dp

                                println("navn: lineCount: $lineCount, Height: $height")
                                padding = if (lineCount > 1) 0.dp else height
                            }
                        )
                    }
                    Box(modifier = Modifier
                         ){
                        Text(
                            modifier = Modifier,
                            text = "${applianceDuration}min",
                            color = TextColor1,
                            fontSize = 10.sp,
                            onTextLayout = {
                                val lineCount = it.lineCount
                                val height = (it.size.height/density).dp

                                println("lavest: lineCount: $lineCount, Height: $height")
                                padding = if (lineCount > 1) 0.dp else height
                            }
                        )
                    }
                }
                }


            Box(modifier = Modifier
                .padding(bottom = 16.dp, top = 16.dp)){
                Column {
                    Box(modifier = Modifier

                    ) {
                        Text(
                            modifier = Modifier,
                            text = "$appliancePriceNow kr",
                            color = AccentColor,
                            fontSize = 16.sp,
                            fontWeight = SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = {
                                val lineCount = it.lineCount
                                val height = (it.size.height / density).dp

                                println("prisnå: ineCount: $lineCount, Height: $height")
                                padding = if (lineCount > 1) 0.dp else height
                            }
                        )
                    }

                    Box(modifier = Modifier
                        ){
                        Text(
                            modifier = Modifier,
                            text = " Pris nå",
                            color = TextColor1,
                            fontSize = 12.sp,
                            onTextLayout = {
                                val lineCount = it.lineCount
                                val height = (it.size.height/density).dp

                                println("lavest: lineCount: $lineCount, Height: $height")
                                padding = if (lineCount > 1) 0.dp else height
                            }
                        )
                    }
                }
            }



            Box(modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp, top = 16.dp, end = 12.dp)){
                Column(Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier
                        .align(Alignment.End)
                    ) {
                        Text(
                            modifier = Modifier,
                            text = " $appliancePriceLowest kr",
                            color = GreenColor,
                            fontSize = 16.sp,
                            fontWeight = SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = {
                                val lineCount = it.lineCount
                                val height = (it.size.height / density).dp

                                println("lavest: lineCount: $lineCount, Height: $height")
                                padding = if (lineCount > 1) 0.dp else height
                            }
                        )
                    }
                    Box(modifier = Modifier
                        .align(Alignment.End)
                    ){
                        Text(
                            modifier = Modifier,
                            text = " billigst(${lowestPrice.first})",
                            color = TextColor1,
                            fontSize = 10.sp,
                            onTextLayout = {
                                val lineCount = it.lineCount
                                val height = (it.size.height/density).dp

                                println("lavest: lineCount: $lineCount, Height: $height")
                                padding = if (lineCount > 1) 0.dp else height
                            }
                        )
                    }
                }
            }
            Box(modifier = Modifier
                .weight(0.25f)){
                // Display the delete button
                IconButton(
                    onClick = { onApplianceRemoved(appliance) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextColor2)
                }
            }
        }



    }
}



