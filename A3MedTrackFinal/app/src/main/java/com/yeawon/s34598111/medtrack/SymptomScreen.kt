package com.yeawon.s34598111.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yeawon.s34598111.medtrack.viewmodel.MedicationViewModel
import com.yeawon.s34598111.medtrack.viewmodel.PatientViewModel
import com.yeawon.s34598111.medtrack.entity.Symptom
import com.yeawon.s34598111.medtrack.viewmodel.SymptomViewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.get
import kotlin.sequences.forEach
import kotlin.text.split

class SymptomScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: SymptomViewModel = ViewModelProvider(this)
            .get(SymptomViewModel::class.java)
        setContent {
            Final_medtrackTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    LogSymptomScreen(modifier = Modifier.padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LogSymptomScreen(modifier: Modifier = Modifier, viewModel: SymptomViewModel) {
    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)
    val patientId = sharedPref.getString("patient_id", "") ?:""

    val symptoms by viewModel.getSymptomByPatient(patientId).collectAsState(emptyList())

    var sliderValue by rememberSaveable { mutableStateOf(1f) }

    var notes by rememberSaveable { mutableStateOf("") }
    var notesError by rememberSaveable { mutableStateOf(false) }

    var chosenSymptom by rememberSaveable { mutableStateOf("") }

    var categoryError by rememberSaveable { mutableStateOf(false) }
    var dateError by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Log symptom
            Text(
                text = "Log Symptom",
                style = TextStyle(fontSize = 24.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )
            // dropdown symptom
            DropdownSymptoms(
                chosenSymptom,
                onSymptomSelected = {
                    chosenSymptom = it
                    categoryError = false
                }
            )

            if(categoryError){
                Text(
                    text = "Please select a symptom",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Assign color based on symptom severity level
            val severityColor =
                if(sliderValue <= 3f) {
                    Color.Green
                } else if(sliderValue <= 6f) {
                    Color(0xFFFFC107)
                } else{
                    Color(0xFFF44336)
                }

            // Convert numeric severity into text
            val severityText =
                if(sliderValue <= 3f) {
                    "Mild"
                } else if(sliderValue <= 6f) {
                    "Moderate"
                } else {
                    "Severe"
                }

            Text(
                text = "Severity: $severityText",
                color = severityColor
            )

            // Display slider for severity
            Text(
                text = "Value: ${sliderValue.toInt()}",
                color = severityColor
            )

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 1f..10f,
                colors = SliderDefaults.colors(thumbColor = severityColor, activeTrackColor = severityColor)
            )

            // Display note text field
            OutlinedTextField(
                value = notes,
                onValueChange = {
                    notes = it
                    notesError = it.length > 200
                },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            if(notesError) {
                Text(
                    text = "Maximum only 200 characters allowed",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Date")

            // Date picker for selecting date
            DatePicker(state = datePickerState)

            if(dateError){
                Text(
                    text = "Please select a date",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Time")

            // Time picker for selecting time
            TimePicker(state = timePickerState)

            Spacer(modifier = Modifier.height(16.dp))
            // Save symptoms
            Button(
                onClick = {
                    categoryError = chosenSymptom.isBlank()
                    dateError = datePickerState.selectedDateMillis == null
                    notesError = notes.length > 200

                    // If the inputs are valid the show snackbar
                    if(!categoryError && !dateError && !notesError) {

                        val time = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )

                        val dateMillis = datePickerState.selectedDateMillis ?: 0L

                        val date = SimpleDateFormat(
                            "dd/MM/yyyy", Locale.getDefault()).format(Date(dateMillis)
                            )

                        val symptom = Symptom(
                            patientId = patientId,
                            category = chosenSymptom,
                            severity = sliderValue.toInt(),
                            notes = notes,
                            dateTime = "$date $time"
                        )
                        viewModel.insertSymptom(symptom)

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Symptom saved successfully"
                            )
                        }
                        chosenSymptom = ""
                        sliderValue = 1f
                        notes = ""
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Please fill in all required fields"
                            )
                        }
                    }
                }
            ) {Text("Save") }

            // Added button to navigate to home
            Button(
                onClick = {
                    context.startActivity(Intent(context, HomeScreen::class.java))
                }
            ) {Text("Home") }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Symptom History",
                style = TextStyle(fontSize = 24.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            if (symptoms.isEmpty()) {
                Text(
                    text = "No symptoms logged",
                    style = TextStyle(fontSize = 18.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                symptoms.forEach { symptom ->
                    val severityLabel =
                        if(symptom.severity <= 3) "Mild"
                        else if(symptom.severity <= 6) "Moderate"
                        else "Severe"

                    val severityColor =
                        if(symptom.severity<= 3) Color.Green
                        else if(symptom.severity <= 6) Color(0xFFFFA500)
                        else Color.Red

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Category: ${symptom.category}")
                            Text(text = "Severity: $severityLabel", color = severityColor)
                            Text(text = "Date/Time: ${symptom.dateTime}")
                            Text(text = "Notes: ${symptom.notes}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownSymptoms(chosenSymptom: String, onSymptomSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = chosenSymptom,
            onValueChange = {},
            readOnly = true,
            label = { Text("Choose symptom")},

            trailingIcon = {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select symptom"
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
                text = { Text("Pain")},
                onClick = {
                    onSymptomSelected("Pain")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Nausea")},
                onClick = {
                    onSymptomSelected("Nausea")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Dizziness")},
                onClick = {
                    onSymptomSelected("Dizziness")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Fatigue")},
                onClick = {
                    onSymptomSelected("Fatigue")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Headache")},
                onClick = {
                    onSymptomSelected("Headache")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Skin Reaction")},
                onClick = {
                    onSymptomSelected("Skin Reaction")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Other")},
                onClick = {
                    onSymptomSelected("Other")
                    expanded = false
                }
            )
        }
    }
}