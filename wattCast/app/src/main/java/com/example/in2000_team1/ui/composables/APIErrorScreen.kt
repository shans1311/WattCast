package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.in2000_team1.ui.theme.WindColor

@Composable
fun APIErrorScreen(onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Det har skjedd en feil ved lasting av data fra ett av de essensielle APIene. Vennligst prøv igjen senere",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = onRetryClick,
            shape = MaterialTheme.shapes.medium.copy(CornerSize(percent = 10)),
            modifier = Modifier
                .width(160.dp)
                .background(
                    WindColor,
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(percent = 10))
                ),
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        ) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier.size(18.dp)
            )
            Text("Last inn på nytt", fontSize = 12.sp)
        }
    }
}
