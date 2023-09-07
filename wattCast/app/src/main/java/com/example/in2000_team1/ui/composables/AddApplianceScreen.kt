package com.example.in2000_team1.ui.composables

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_team1.ui.MainViewModel
import com.example.in2000_team1.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApplianceScreen(onAddAppliance: () -> Unit, context: Context, viewModel: MainViewModel) {
    // Store the data from the text fields
    var name by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    // Initialize a snackbar host state and a coroutine scope for showing snackbar messages
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(PrimaryColor)
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Column(modifier = Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(8.dp),
        )  {
            // Display the header text
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                "Legg til",
                color = AccentColor,
                textAlign = TextAlign.Start,
                fontSize = 40.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Legg til ditt apparat",
                color = SecondaryColor1,
                textAlign = TextAlign.Start,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(modifier = Modifier.weight(1f))  {

            Text("Navn", color = TextColor1)
            TextField(
                value = name,
                onValueChange = {name = it},
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                label = {
                    Text(
                        text = "Navnet på apparatet",
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier.border(
                    1.dp,
                    BorderGray,
                    shape = RoundedCornerShape(10.dp),
                ),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                     containerColor = Color.White,
                    focusedLabelColor = TextColor1,
                    focusedIndicatorColor =  Color.Transparent, //hide the indicator
                    unfocusedIndicatorColor = Color.Transparent),
                singleLine = true
            )



            // Display the consumption text field
            Text("Forbruk(Watt)", color = TextColor1)
            TextField(
                value = consumption,
                onValueChange = {consumption = it},
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                label = {
                    Text(
                        text = "Watt",
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier.border(
                    1.dp,
                    BorderGray,
                    shape = RoundedCornerShape(10.dp),
                ),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedLabelColor = TextColor1,
                    focusedIndicatorColor =  Color.Transparent, //hide the indicator
                    unfocusedIndicatorColor = Color.Transparent),
                singleLine = true,
                keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            // Display the duration text field
            Text("Varighet", color = TextColor1)
            TextField(
                value = duration,
                onValueChange = {duration = it},
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                label = {
                    Text(
                        text = "Antall minutter",
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier.border(
                    1.dp,
                    BorderGray,
                    shape = RoundedCornerShape(10.dp),
                ),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedLabelColor = TextColor1,
                    focusedIndicatorColor =  Color.Transparent, //hide the indicator
                    unfocusedIndicatorColor = Color.Transparent),
                singleLine = true,
                keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Number)
            )



        }


        // Host the snackbar in this box, together with a column containing the two buttons
        Column(modifier = Modifier.weight(1f)) {
            Box {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ConfirmApplianceAddButton {
                    val usageWatts = consumption.toIntOrNull()
                    val durationMinutes = duration.toIntOrNull()
                    if (name.isNotEmpty() && usageWatts != null && durationMinutes != null) {

                        viewModel.addAppliance(
                            context = context,
                            name = name,
                            usageWatts = usageWatts,
                            durationMinutes = durationMinutes
                        )

                        onAddAppliance()

                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Ugyldig input.")
                        }
                    }
                }
                BackButton(icon = Icons.Default.Close) {
                    onAddAppliance()
                }
            }
        }
    }

}
