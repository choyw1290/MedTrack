package com.yeawon.s34598111.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.yeawon.s34598111.medtrack.entity.Medication
import com.yeawon.s34598111.medtrack.viewmodel.MedicationViewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import kotlinx.coroutines.launch

class MedicationScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Final_medtrackTheme {
                val viewModel: MedicationViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddMedicationScreen(modifier = Modifier.padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddMedicationScreen(modifier: Modifier = Modifier, viewModel: MedicationViewModel) {

    val context = LocalContext.current

    var medicationName by rememberSaveable { mutableStateOf("") }
    var medicationNameError by rememberSaveable { mutableStateOf(false) }

    var dosage by rememberSaveable { mutableStateOf("") }
    var dosageError by rememberSaveable { mutableStateOf(false) }

    var notes by rememberSaveable { mutableStateOf("") }

    var chosenFrequency by rememberSaveable { mutableStateOf("") }
    var chosenType by rememberSaveable() { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val timePickerState = rememberTimePickerState()

    // To validate the dosage pattern
    val dosagePattern = Regex("^\\d+(\\.\\d+)?(mg|ml|g)$")

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding).verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // Display add medication header
            Text(
                text = "Add Medication",
                style = TextStyle(fontSize = 24.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            // Display text field medication name
            OutlinedTextField(
                value = medicationName,
                onValueChange = {
                    medicationName = it
                    medicationNameError = it.isBlank()
                },
                label = { Text(text = "Medication Name")},
                modifier = Modifier.fillMaxWidth()
            )

            if (medicationNameError) {
                Text(
                    text = "Medication name cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display text field dosage
            OutlinedTextField(
                value = dosage,
                onValueChange = {
                    dosage = it
                    dosageError = it.isBlank()
                },
                label = { Text(text = "Dosage")},
                modifier = Modifier.fillMaxWidth()
            )

            if (dosageError) {
                Text(
                    text = "Dosage must be a number followed by mg, ml or g",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display dropdown frequency
            DropdownFrequency(
                chosenFrequency = chosenFrequency,
                onFrequencySelected = {chosenFrequency = it})

            Spacer(modifier = Modifier.height(24.dp))

            // Display scheduled time
            Text(
                text = "Scheduled Time",
                style = TextStyle(fontSize = 16.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            // Show time picker
            TimePicker(
                state = timePickerState
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display dropdown type
            DropdownMedicationType(
                chosenType = chosenType,
                onTypeSelected = {chosenType = it}
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display notes text field
            OutlinedTextField(
                value = notes,
                onValueChange = {notes = it},
                label = { Text("Notes (optional)")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            // Added save button
            Button(
                onClick = {
                    // Check whether the input fields are empty
                    medicationNameError = medicationName.isBlank()
                    dosageError = dosage.isBlank() || !dosagePattern.matches(dosage)

                    val hour = timePickerState.hour
                    val minute = timePickerState.minute

                    val selectedTime = String.format("%02d:%02d", hour, minute)

                    // If not empty save newly created medication to SharedPreferences based on patient ID
                    if (medicationName.isNotBlank() && !dosageError) {

                        val loginPref = context.getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)
                        val patientId = loginPref.getString("patient_id", "")?:""

                        val medication = Medication(
                            patientId = patientId,
                            medicationName = medicationName,
                            dosage = dosage,
                            frequency = chosenFrequency,
                            type = chosenType,
                            notes = notes,
                            time = selectedTime
                        )

                        viewModel.insertMedication(medication)

                        // Clears the text fields and dropdown
                        medicationName = ""
                        dosage = ""
                        notes = ""
                        chosenFrequency =""
                        chosenType = ""

                        scope.launch {
                            snackbarHostState.showSnackbar("Medication saved successfully", duration = SnackbarDuration.Short)
                            context.startActivity(Intent(context, HomeScreen::class.java))
                        }

                    } else  {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Please fill all required fields"
                            )
                        }
                    }
                }
            ) {Text("Save") }

            Spacer(modifier = Modifier.height(16.dp))

            // Clears the text fields and dropdown
            Button(
                onClick = {
                    medicationName = ""
                    dosage = ""
                    notes = ""
                    chosenFrequency =""
                    chosenType = ""
                }
            ) {Text("Clear") }

            Spacer(modifier = Modifier.height(16.dp))

            // Add home button to navigate to home
            Button(
                onClick = {
                    context.startActivity(Intent(context, HomeScreen::class.java))
                }
            ) {Text("Home") }
        }
    }
}


// Create list for dropdown medication frequency
@Composable
fun DropdownFrequency(chosenFrequency: String, onFrequencySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false)}

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = chosenFrequency,
            onValueChange = {},
            readOnly = true,
            label = { Text("Choose frequency")},

            // Add dropdown arrow that opens the frequency menu when clicked
            trailingIcon = {
                IconButton(
                    onClick = { expanded = true}
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select frequency"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            DropdownMenuItem(
                text = { Text("Once daily") },
                onClick = {
                    onFrequencySelected("Once daily")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Twice daily") },
                onClick = {
                    onFrequencySelected("Twice daily")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Three times daily") },
                onClick = {
                    onFrequencySelected("Three times daily")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("As needed") },
                onClick = {
                    onFrequencySelected("As needed")
                    expanded = false
                }
            )
        }
    }
}

// Create list for dropdown medication type
@Composable
fun DropdownMedicationType(chosenType: String, onTypeSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false)}

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = chosenType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Choose medication type")},

            // Add dropdown arrow that opens the frequency menu when clicked
            trailingIcon = {
                IconButton(
                    onClick = { expanded = true}
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select medication type"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            DropdownMenuItem(
                text = { Text("Tablet") },
                onClick = {
                    onTypeSelected("Tablet")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Capsule") },
                onClick = {
                    onTypeSelected("Capsule")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Liquid") },
                onClick = {
                    onTypeSelected("Liquid")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Injection") },
                onClick = {
                    onTypeSelected("Injection")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Topical") },
                onClick = {
                    onTypeSelected("Topical")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Other") },
                onClick = {
                    onTypeSelected("Other")
                    expanded = false
                }
            )
        }
    }
}