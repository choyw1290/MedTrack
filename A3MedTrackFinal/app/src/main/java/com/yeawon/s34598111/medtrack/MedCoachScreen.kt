package com.yeawon.s34598111.medtrack

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.type.Content
import com.yeawon.s34598111.medtrack.viewmodel.MedCoachViewModel
import com.yeawon.s34598111.medtrack.viewmodel.MedicationViewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import com.yeawon.s34598111.medtrack.viewmodel.GenAiViewModel
import kotlin.math.exp

class MedCoachScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val medCoachViewModel: MedCoachViewModel = ViewModelProvider(this).get(MedCoachViewModel::class.java)
        val genAiViewModel: GenAiViewModel = ViewModelProvider(this).get(GenAiViewModel::class.java)

        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MedCoachScreen(modifier = Modifier.padding(innerPadding), medCoachViewModel = medCoachViewModel, genAiViewModel = genAiViewModel)
                }
            }
        }
    }
}

@Composable
fun MedCoachScreen(modifier: Modifier = Modifier, medCoachViewModel: MedCoachViewModel, genAiViewModel: GenAiViewModel) {

    // Collect Ai tip and history
    val tip by genAiViewModel.tip.collectAsState()
    val history by genAiViewModel.history.collectAsState()
    val genAiLoading by genAiViewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)
    val patientId = sharedPref.getString("patient_id", "") ?: ""

    var drugName by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable() { mutableStateOf(false) }

    // Drug API state
    val drugInfo by medCoachViewModel.drugInfo.collectAsState()
    val isLoading by medCoachViewModel.isLoading.collectAsState()
    val errorMessage by medCoachViewModel.errorMessage.collectAsState()
    val medicationList by medCoachViewModel.medicationList.collectAsState()

    var showHistoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        medCoachViewModel.loadPatientMedications(patientId)
    }

    Scaffold(
        bottomBar = {
            NavBottomBar(current = "medcoach")
        }
    ) {innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            // Display Title
            Text(
                text = "MedCoach",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Your Medications")

            Box{

                // Dropdown for selecting medication
                Button(onClick = { expanded = true }) {
                    Text(text = if(drugName.isEmpty()) "Select medication" else drugName)
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false}) {
                    if(medicationList.isEmpty()) {
                        DropdownMenuItem(
                            text = {Text("No medications found")},
                            onClick = {expanded = false}
                        )
                    } else {
                        medicationList.forEach { med ->
                            DropdownMenuItem(
                                text = {Text(med)},
                                onClick = {
                                    drugName = med
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input for medication
            OutlinedTextField(
                value = drugName,
                onValueChange = {drugName = it},
                label ={Text("Enter medication name")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search drug info from API
            Button(
                onClick = {
                    if(drugName.isNotBlank()){
                        medCoachViewModel.searchDrug(drugName)
                    }
                }
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Loading indicator for API call
            if(isLoading){
                CircularProgressIndicator()
            }

            // Error message display
            if(errorMessage != null) {
                Text(errorMessage ?: "")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display drug details
            val drug = drugInfo?.results?.firstOrNull()

            if(drug != null) {
                Text("Purpose:")
                Text(drug.purpose?.firstOrNull() ?: "No data")

                Text("Warnings:")
                Text(drug.warnings?.firstOrNull() ?: "No data")

                Text("Dosage and administration:")
                Text(drug.dosage_and_administration?.firstOrNull() ?: "No data")
            } else if (!isLoading && errorMessage == null) {
                Text("No drug data found")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Medication Tips",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Generate Ai tip
            Button(
                onClick = {
                    genAiViewModel.generateTip(patientId)
                },
                enabled = !genAiLoading
            ) {
                Text("Generate Tip")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ai loading state
            if(genAiLoading) {
                CircularProgressIndicator()
                Text("Generating tip...")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show latest Ai tip
            if(tip.isNotEmpty()){
                Text(
                    text = "Latest Tip",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = tip,
                        )
                    }
                }
            }

            // Show history dialog
            Button(
                onClick = {
                    genAiViewModel.loadHistory(patientId)
                    showHistoryDialog = true
                }
            ) {Text("Show History") }
        }
    }

    // History dialog
    if(showHistoryDialog){
        AlertDialog(
            onDismissRequest = {
                showHistoryDialog = false
            },
            confirmButton = {
                Button(onClick = {showHistoryDialog = false}) {
                    Text("Close")
                }
            },
            title = {Text("Medication Tips History")
            },
            text = {
                if(history.isEmpty()) {
                    Text("No history yet")
                } else {
                    LazyColumn {
                        items(history) { item ->
                            Card(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = item.tip,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}