package com.yeawon.s34598111.medtrack

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yeawon.s34598111.medtrack.viewmodel.ClaimAccountViewModel
import com.yeawon.s34598111.medtrack.viewmodel.MedicationViewModel
import com.yeawon.s34598111.medtrack.viewmodel.PatientViewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import kotlinx.coroutines.launch

class ClaimAccountScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: ClaimAccountViewModel = ViewModelProvider(this).get(ClaimAccountViewModel::class.java)
        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ClaimAccountScreen(modifier = Modifier.padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ClaimAccountScreen(modifier: Modifier = Modifier, viewModel: ClaimAccountViewModel) {

    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var phoneNumberError by rememberSaveable { mutableStateOf(false) }

    var patientId by rememberSaveable { mutableStateOf("") }
    var patientIdError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Login header
            Text(
                text = "Claim Account",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display patient ID text field
            OutlinedTextField(
                value = patientId,
                onValueChange = {
                    patientId = it
                    patientIdError = false
                },
                label = {Text("Patient ID")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = patientIdError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if(patientIdError){
                Text(
                    text = "Patient ID cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display phone number text field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    phoneNumberError = false
                },
                label = {Text("Phone Number")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneNumberError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if(phoneNumberError){
                Text(
                    text = "Phone number cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display password text field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = {Text("Password")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if(passwordError){
                Text(
                    text = "Phone number cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                patientIdError = patientId.isBlank()
                phoneNumberError = phoneNumber.isBlank()
                passwordError = password.isBlank()

                // Proceed only if all inputs are valid
                if (!patientIdError && !phoneNumberError && !passwordError) {
                    // Call viewmodel to verify and claim aacount
                    viewModel.claimAccount(patientId, phoneNumber, password) { success, message ->

                        if(success && message == "SUCCESS") {
                            scope.launch {
                                snackbarHostState.showSnackbar("Account claimed successfully")
                                context.startActivity(Intent(context, LoginScreen::class.java))
                            }
                        } else if (message == "INVALID_ID"){
                            scope.launch {
                                snackbarHostState.showSnackbar("Invalid Patient ID")
                            }
                        } else if (message == "INVALID_PHONE") {
                            scope.launch {
                                snackbarHostState.showSnackbar("Invalid Phone Number")
                            }
                        } else if(message == "MISMATCH"){
                            scope.launch {
                                snackbarHostState.showSnackbar("Patient ID and Phone do not match")
                            }
                        }
                    }
                }
            }) {Text("Save") }
        }
    }
}