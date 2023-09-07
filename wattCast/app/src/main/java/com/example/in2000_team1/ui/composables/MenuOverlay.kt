package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.in2000_team1.ui.theme.*


// The overlay with the menu buttons, which appears when you push the hamburgermenubutton
@Composable
fun MenuOverlay(navController: NavController, onClose: () -> Unit){
    Surface(color = TextColor1.copy(alpha = 0.7f)) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier

                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(80.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Info", color = Color.White, fontSize = 16.sp, fontFamily = OpenSansBold)
                Spacer(modifier = Modifier.padding(4.dp))
                MenuButton(icon = Icons.Default.Info) {
                    navController.navigate("info")
                    onClose()
                }
            }

            Spacer(modifier = Modifier.padding(48.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Apparater", color = Color.White, fontSize = 16.sp, fontFamily = OpenSansBold)
                Spacer(modifier = Modifier.padding(4.dp))
                ApplianceButton {
                    navController.navigate("applianceList")
                    onClose()

                }
            }
            Spacer(modifier = Modifier.padding(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Hjem", color = Color.White, fontSize = 16.sp, fontFamily = OpenSansBold)
                    Spacer(modifier = Modifier.padding(4.dp))
                    MenuButton(icon = Icons.Default.Home) {
                        navController.navigate("home")
                        onClose()
                    }
                }
                Spacer(modifier = Modifier.padding(80.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sted", color = Color.White, fontSize = 16.sp, fontFamily = OpenSansBold)
                    Spacer(modifier = Modifier.padding(4.dp))
                    MenuButton(icon = Icons.Default.LocationOn) {
                        navController.navigate("map")
                        onClose()
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tilbake", color = Color.White, fontSize = 16.sp, fontFamily = OpenSansBold)
                Spacer(modifier = Modifier.padding(4.dp))
                BackButton(icon = Icons.Default.Close) {
                    onClose()
                }
            }
        }
    }
}