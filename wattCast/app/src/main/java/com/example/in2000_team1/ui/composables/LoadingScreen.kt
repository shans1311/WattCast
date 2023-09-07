package com.example.in2000_team1.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.example.in2000_team1.R
import com.example.in2000_team1.ui.theme.OrangeGradient


// Display a loading screen with the logo and a background. Used while the data is loading in the app
@SuppressLint("SuspiciousIndentation")
@Composable
fun LoadingScreen() {

    Box(
        Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = OrangeGradient
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val logo = painterResource(R.drawable.logoin2000_4)
             Image(painter = logo, contentDescription = "", modifier = Modifier.size(150.dp) )

    }

}
