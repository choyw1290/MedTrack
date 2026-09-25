package com.yeawon.s34598111.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.ParsedPhoneNumber
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.yeawon.s34598111.medtrack.viewmodel.PatientViewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: PatientViewModel = ViewModelProvider(this)
            .get(PatientViewModel::class.java)

        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding), viewModel)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, viewModel: PatientViewModel) {

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    var patientId by rememberSaveable { mutableStateOf("") }
    var patientIdError by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {padding ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Login header
            Text(
                text = "Login",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

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

            // Login Button
            Button(
                onClick = {
                    // Check whether the input fields are empty
                    patientIdError = patientId.isBlank()
                    passwordError = password.isBlank()

                    // If not empty then check the phone number and password
                    if(!passwordError && !patientIdError) {
                        viewModel.login(patientId, password) { patient ->
                            // If login is successful, store patient details
                            if(patient != null) {
                                val sharedPref = context.getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)

                                sharedPref.edit()
                                    .putString("patient_id", patient.patientId)
                                    .putString("patient_name", patient.name)
                                    .putString("phone_number", patient.phoneNumber)
                                    .apply()

                                scope.launch {
                                    snackbarHostState.showSnackbar("Login successful")
                                    context.startActivity(Intent(context, HomeScreen::class.java))
                                }

                            }else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Invalid login")
                                }
                            }
                        }
                    }
                }
            ) {Text("Login") }

            // Added button to navigate to sign up
            Button(
                onClick = {
                    context.startActivity(Intent(context, SignUpScreen::class.java))
                }
            ) {Text("Sign Up") }

            Spacer(modifier = Modifier.height(24.dp))

            // Display text
            Text(
                text = "For existing patient, please claim your account",
                style = TextStyle(fontSize = 16.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            // Display to Claim Account
            Text(
                text = "Claim Account",
                color = Color.Blue,
                style = TextStyle(fontSize = 16.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp).clickable{context.startActivity(Intent(context,
                    ClaimAccountScreen::class.java))}
            )
        }
    }
}