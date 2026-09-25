package com.yeawon.s34598111.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.yeawon.s34598111.medtrack.viewmodel.MedicationViewModel
import com.yeawon.s34598111.medtrack.viewmodel.PatientViewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import com.yeawon.s34598111.medtrack.viewmodel.DietGenAiViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Viewmodels for medication AI diet
        val viewModel: MedicationViewModel = ViewModelProvider(this)
            .get(MedicationViewModel::class.java)

        val dietGenAiViewModel: DietGenAiViewModel = ViewModelProvider(this)
            .get(DietGenAiViewModel::class.java)

        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding), viewModel =viewModel, dietGenAiViewModel = dietGenAiViewModel)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: MedicationViewModel, dietGenAiViewModel: DietGenAiViewModel) {
    val context = LocalContext.current

    // Retrieve logged in patient details
    val sharedPref = context.getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)
    val patientName = sharedPref.getString("patient_name", "Guest")
    val patientId = sharedPref.getString("patient_id", "") ?: ""

    // Get today's data
    val currentDate = Date()
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH)
    val formattedDate = dateFormat.format(currentDate)

    val medicationInfo by viewModel.getMedicationByPatient(patientId).collectAsState(emptyList())
    val dietTip by dietGenAiViewModel.dietPlan.collectAsState()
    val dietLoading by dietGenAiViewModel.isLoading.collectAsState()

    val takenMap by viewModel.takenMap.collectAsState()

    val takenCount = takenMap.values.count{it}

    LaunchedEffect(patientId) {
        viewModel.loadTakenStatus(patientId)
    }

    Scaffold(
        bottomBar = {
            // Bottom navigation bar
            NavBottomBar(current = "home")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Greet patient
                    Text(
                        text = "Hello, $patientName",
                        style = TextStyle(fontSize = 24.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Patient ID: $patientId",
                        style = TextStyle(fontSize = 18.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )

                    // Display today's date
                    Text(
                        text = formattedDate,
                        style = TextStyle(fontSize = 18.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Track medication taken by using summary bar
                    Text(
                        text = "$takenCount of ${medicationInfo.size} medications taken today"
                    )
                }
            }
            // Check if there are medication for that patient
            // If no then display no medication scheduled
            if (medicationInfo.isEmpty()) {
                item {
                    Text(
                        text = "No medications scheduled",
                        style = TextStyle(fontSize = 18.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                // Display medication list
                items(medicationInfo) { med ->

                    val isTaken = takenMap[med.medicationId] ?: false

                    val cardColor =
                        if (isTaken) {
                            Color.Gray
                        } else {
                            Color.LightGray
                        }

                    val textStyle =
                        if (isTaken) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Name: ${med.medicationName}",
                                    textDecoration = textStyle
                                )
                                Text(
                                    text = "Dosage: ${med.dosage}",
                                    textDecoration = textStyle
                                )
                                Text(
                                    text = "Frequency: ${med.frequency}",
                                    textDecoration = textStyle
                                )
                                Text(
                                    text = "Scheduled: ${med.time}",
                                    textDecoration = textStyle
                                )
                            }
                            // Toggle medication taken status
                            Switch(
                                checked = isTaken,
                                onCheckedChange = { checked ->
                                    viewModel.toggleTaken(med.medicationId, patientId, checked)
                                }
                            )
                        }
                    }
                }
            }
            // Navigate to add medication
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = {
                        context.startActivity(Intent(context, MedicationScreen::class.java))
                    }) { Text("Add Medication") }
                }
            }

            // Diet Gen Ai recommendation
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Diet Recommendation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(onClick = {
                        dietGenAiViewModel.generateDiet(patientId)
                    }) {
                        Text("Generate Diet Advice")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Loading state while AI generates response
                    if (dietLoading) {
                        CircularProgressIndicator()
                        Text("Generating diet plan...")
                    }

                    // Display Ai generated diet result
                    if (dietTip.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = dietTip
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}