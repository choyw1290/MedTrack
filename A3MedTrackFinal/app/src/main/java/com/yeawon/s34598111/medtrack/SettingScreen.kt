package com.yeawon.s34598111.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme

class SettingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)

    val patientName = sharedPref.getString("patient_name", "Guest")
    val patientId = sharedPref.getString("patient_id", "") ?: ""
    val phoneNumber = sharedPref.getString("phone_number", "No phone")

    Scaffold(
        // Top app bar with logout button
        topBar = {
            TopAppBar(
                title = {Text(text = "Settings")},
                actions = {
                    // Clears session and navigate to LoginScreen
                    IconButton(
                        onClick = {
                            sharedPref.edit().clear().apply()

                            val intent = Intent(context, LoginScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    ) { Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout"
                    ) }
                }
            )
        },
        // Bottom navigation bar
        bottomBar = {
            NavBottomBar(current = "settings")
        }
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Display patient information
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Patient name
                    Text(text = "Name: $patientName")
                    Spacer(modifier = Modifier.height(8.dp))

                    // Patient ID
                    Text(text = "Patient ID: $patientId")
                    Spacer(modifier = Modifier.height(8.dp))

                    // Patient phone number
                    Text(text = "Phone: $phoneNumber")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigate to ClinicianLoginScreen
            Button(
                onClick = {context.startActivity(Intent(context, ClinicianLoginScreen::class.java))}
            ) {
                Text("Clinician Login")
            }
        }
    }
}