package com.yeawon.s34598111.medtrack

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.InputChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.yeawon.s34598111.medtrack.data.DatabaseSeeder
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.data.MedTrackRepository
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve shared preferences to check if a patient is logged in
        val sharedPref = getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)
        val patientId = sharedPref.getString("patient_id", null)

        // If user have logged in, move directly to HomeScreen
        if(patientId != null) {
            val intent = Intent(this, HomeScreen()::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        val database = MedTrackDatabase.getDatabase(this)

        val repository = MedTrackRepository(context = this, database.patientDao(), database.medicationDao(), database.symptomDao())

        // Seed initial database data
        val seeder = DatabaseSeeder(repository)

        lifecycleScope.launch {
            seeder.seedDatabase(this@MainActivity)
        }

        enableEdgeToEdge()
        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display medtrack logo
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.medication_logo),
            contentDescription = "Medication logo",
            modifier = Modifier.size(200.dp)
        )

        // Display medtrack header
        Text(
            text = "MedTrack",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        // Link to Monash Health Clinic
        Text(
            text = "Monash Health Clinic",
            color = Color.Blue,
            modifier = Modifier.clickable{
                val url = "https://monashhealth.org/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        )

        // Display health disclaimer text
        Text(
            text = "This app is for tracking purposes only and does not replace professional medical advice",
            style = TextStyle(fontSize = 12.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        // Add Login button to navigate to home screen
        Button(onClick = {
            context.startActivity(Intent(context, LoginScreen::class.java))
        }
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add Sign up button to navigate to sign up
        Button(onClick = {
            context.startActivity(Intent(context, SignUpScreen::class.java))
        }) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display student name and student id
        Text(
            text = "Cho Yea Won (34598111)",
            style = TextStyle(fontSize = 16.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

    }
}