package com.example.in2000_team1.ui.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.in2000_team1.R
import com.example.in2000_team1.ui.theme.*

// The menu button for all pages
@Composable
fun HamburgerMenuButton(onClick: () -> Unit) {
    val hamburgerMenuIcon = Icons.Default.Menu

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, BorderGray, CircleShape),
        content = {
            Icon(
                hamburgerMenuIcon,
                contentDescription = "Hamburger Menu",
                tint = AccentColor
            )
        }
    )
}


// Buttons for the menu. Takes an icon as an argument
@Composable
fun MenuButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, BorderGray, CircleShape),
        content = {
            Icon(
                icon,
                contentDescription = "Menu Button",
                tint = SecondaryColor1
            )
        }
    )
}


@Composable
fun BackButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, BorderGray, CircleShape),
        content = {
            Icon(
                icon,
                contentDescription = "tilbake",
                tint = SecondaryColor2
            )
        }
    )
}




// Menu button for navigating to the appliance screen. Since the icon is not in the defaults, it required a different approach
@Composable
fun ApplianceButton(onClick: () -> Unit) {
    val applianceIcon: Painter = painterResource(
        id = R.drawable.baseline_cable_24
    )
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(Color.White, CircleShape)
            .clickable(onClick = onClick)
            .border(1.dp, BorderGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = applianceIcon,
            contentDescription = "Cable",
            tint = SecondaryColor2
        )
    }
}

@Composable
fun AddButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        content = {
            Icon(
                painter = painterResource(id = R.drawable.add_appliance),
                contentDescription = "Add appliance",
                tint = AccentColor
            )
        }
    )
}

// Button for adding an appliance
@Composable
fun ConfirmApplianceAddButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(percent = 10)),
        modifier = Modifier
            .width(260.dp)
            .background(
                SecondaryColor1,
                shape = MaterialTheme.shapes.medium.copy(CornerSize(percent = 10))
            ),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add")
        Text("Legg til")
    }
}

