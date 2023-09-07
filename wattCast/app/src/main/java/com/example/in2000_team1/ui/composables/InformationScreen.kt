package com.example.in2000_team1.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_team1.R
import com.example.in2000_team1.ui.theme.*

@Composable
fun InformationScreen(onInformation: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(PrimaryColor)
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.6f)
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            //title
            Text(
                text = "Informasjon",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 40.sp,
                fontWeight = FontWeight(400),
                color = AccentColor
            )
            //Subtitle
            Text(
                text = "Ofte stilte spørsmål",
                modifier = Modifier.padding(bottom = 10.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight(500),
                color = SecondaryColor1
            )
        }

        // Data class to hold the Q&A
        data class QnA(val question: String, val answer: String)

        // List of Q&A
        val qnaList = listOf(
            QnA(
                stringResource(R.string.q1),
                "Vi bruker historisk vær- og prisdata i en maskinlæringsmodell(multippel lineær regresjon) for å estimere fremtidige priser. Temperatur, regn og vind er med i beregningen. Estimatene vi viser er gjennomsnittlige priser for hver dag."
            ),
            QnA(
                "Hvordan oppdaterer jeg til de nyeste dataene?",
                "På hovedskjermen, dra fingeren ned fra toppen. Siden vil lastes inn på nytt med de nyeste dataene."
            ),
            QnA(
                "Hvordan kan jeg bruke denne kalkulatoren til å spare penger?",
                "Du kan velge å spare bruken av apparatene dine til de dagene med lavest estimert strømpris. Du kan bruke denne kalkulatoren for å identifisere enheter eller apparater som koster mye penger å bruke og gjøre endringer for å redusere bruken." +
                        " For eksempel kan du bytte til energieffektive lyspærer eller trekke ut elektronikk når de ikke er i bruk. "
            ),
            QnA("Hvordan kan jeg fjerne et apparat?", "Trykk på krysset i høyre hjørne."),
        )

        // Use the list to populate the LazyColumn
        Column(modifier = Modifier
            .weight(6.4f)
            .fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16
                    .dp, horizontal = 8.dp)
            ) {
                items(qnaList.size) { index ->
                    val qna = qnaList[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .background(Color.White, shape = RoundedCornerShape(10.dp))
                            .border(1.dp, color = BorderGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        CollapsibleText(
                            title = qna.question,
                            content = qna.answer
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackButton(icon = Icons.Default.Close) {
                onInformation()
            }
        }
    }
}

@Composable
fun CollapsibleText(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight(500),
            color = AccentColor,
            modifier = Modifier
                .clickable(onClick = { expanded = !expanded })
                .padding(16.dp)
        )
        if (expanded) {
            Text(
                text = content,
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}