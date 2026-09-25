package com.yeawon.s34598111.medtrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import com.yeawon.s34598111.medtrack.viewmodel.ClinicianViewModel

class ClinicianDashBoardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Final_medtrackTheme {
                val viewModel: ClinicianViewModel = viewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ClinicianDashBoardScreen(modifier = Modifier.padding(innerPadding), viewModel)
                }
            }
        }
    }
}

@Composable
fun ClinicianDashBoardScreen(modifier: Modifier = Modifier, viewModel: ClinicianViewModel){

    val totalPatients  by viewModel.totalPatients.collectAsState()
    val avgMeds by viewModel.avgMedications.collectAsState()
    val commonSymptom by viewModel.commonSymptom.collectAsState()
    val avgSeverity by viewModel.avgSeverity.collectAsState()
    val insights by viewModel.insights.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }

    Column(modifier = modifier.padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
        // Screen title
        Text(
            text = "Clinician Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Aggregate statistics card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Aggregate Statistics", fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                // Total patients in system
                Text("Total Patients: $totalPatients")

                // Average number of medication
                Text("Average Medications: ${"%.2f".format(avgMeds)}")

                // Most common symptom across patients
                Text("Most Common Symptom: $commonSymptom")

                // Average symptom severity
                Text("Average Severity: ${"%.2f".format(avgSeverity)}")

            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                viewModel.findPatterns()
            }) {
                Text("Find Patterns")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if(isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Analyzing data...")
        }

        Text("GenAI Insights", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(8.dp))

        // Display Ai generated insights
        if(insights.isNotEmpty()) {
            insights.split("\n").filter{it.isNotBlank()}.forEach { line ->
                Card(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = line,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                context.startActivity(Intent(context, HomeScreen::class.java))
            }) {Text("Back to Home") }
        }
    }
}