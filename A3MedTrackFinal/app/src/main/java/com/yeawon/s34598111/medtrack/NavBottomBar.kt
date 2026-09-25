package com.yeawon.s34598111.medtrack

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun NavBottomBar(current: String) {

    val context = LocalContext.current

    NavigationBar {
        // Navigate to HomeScreen
        NavigationBarItem(
            selected = current == "home",
            onClick = {
                context.startActivity(Intent(context, HomeScreen::class.java))
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        // Navigate to SymptomScreen
        NavigationBarItem(
            selected = current == "symptoms",
            onClick = {
                context.startActivity(Intent(context, SymptomScreen::class.java))
            },
            icon = { Icon(Icons.Default.Info, contentDescription = "Symptoms") },
            label = { Text("Symptom") }
        )

        // Navigate to MedCoachScreen
        NavigationBarItem(
            selected = current == "medcoach",
            onClick = {
                context.startActivity(Intent(context, MedCoachScreen::class.java))
            },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "MedCoach") },
            label = { Text("MedCoach") }
        )

        // Navigate to SettingScreen
        NavigationBarItem(
            selected = current == "settings",
            onClick = {
                context.startActivity(Intent(context, SettingScreen::class.java))
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") },
            label = {Text("Settings")}
        )
    }
}