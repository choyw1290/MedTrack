package com.yeawon.s34598111.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.yeawon.s34598111.medtrack.entity.Patient
import com.yeawon.s34598111.medtrack.viewmodel.PatientViewModel
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.sequences.forEach

class SignUpScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: PatientViewModel = ViewModelProvider(this).get(PatientViewModel::class.java)
        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignUpScreen(modifier = Modifier.padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, viewModel: PatientViewModel){

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var fullName by rememberSaveable { mutableStateOf("") }
    var fullNameError by rememberSaveable { mutableStateOf(false) }

    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var phoneEmptyError by rememberSaveable { mutableStateOf(false) }
    var phoneFormatError by rememberSaveable { mutableStateOf(false) }
    var phoneExistError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordEmptyError by rememberSaveable { mutableStateOf(false) }
    var passwordFormatError by rememberSaveable { mutableStateOf(false) }

    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var confirmEmptyError by rememberSaveable { mutableStateOf(false) }
    var confirmMatchError by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val viewModel: PatientViewModel = viewModel()

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState)}
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display sign up header
            Text(
                text = "Sign Up",
                style = TextStyle(fontSize = 24.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            // Display text field full name
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    fullNameError = false
                },
                label = { Text("Full Name") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = fullNameError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if(fullNameError){
                Text(
                    text = "Full name cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display text field phone number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    phoneEmptyError = false
                    phoneFormatError = false
                    phoneExistError = false
                },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneEmptyError || phoneFormatError || phoneExistError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if(phoneEmptyError){
                Text(
                    text = "Phone number cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            if(phoneFormatError){
                Text(
                    text = "Must start with 04 and have exactly 10 digits",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            if(phoneExistError){
                Text(
                    text = "Phone number already exists",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display text field password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordEmptyError = false
                    passwordFormatError = false
                },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordEmptyError || passwordFormatError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if(passwordEmptyError){
                Text(
                    text = "Password cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            if(passwordFormatError){
                Text(
                    text = "Must have at least 8 characters and contains at least 1 letter and number",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display text field confirm password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmEmptyError = false
                    confirmMatchError = false
                },
                label = { Text("Confirm Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmEmptyError || confirmMatchError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if(confirmEmptyError){
                Text(
                    text = "Confirm password cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            if(confirmMatchError){
                Text(
                    text = "Password does not match",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up button
            Button(
                onClick = {
                    fullNameError = fullName.isBlank()

                    phoneEmptyError = phoneNumber.isBlank()
                    phoneFormatError = false
                    phoneExistError = false

                    passwordEmptyError = password.isBlank()
                    passwordFormatError = false

                    confirmEmptyError = confirmPassword.isBlank()
                    confirmMatchError = false

                    // Track if any error exists
                    var error = false

                    if(fullNameError) {
                        error = true
                    }

                    if(phoneEmptyError) {
                        error = true
                    } else {
                        // Check phone number format
                        if(phoneNumber.length != 10 || !phoneNumber.startsWith("04")) {
                            phoneFormatError = true
                            error = true
                        }
                    }

                    if(passwordEmptyError) {
                        error = true
                    } else {
                        // Check password format
                        if (password.length < 8 || !password.any { it.isLetter() } || !password.any { it.isDigit() }) {
                            passwordFormatError = true
                            error = true
                        }
                    }

                    if(confirmEmptyError) {
                        error = true
                    } else{
                        // Check if match with password
                        if(password != confirmPassword) {
                            confirmMatchError = true
                            error = true
                        }
                    }

                    // If no error then save
                    if(!error) {

                        viewModel.signUp(fullName, phoneNumber, password) { result, newId ->
                            if(result == "PHONE_EXISTS") {
                                phoneExistError = true
                            }

                            // If sign up is successful, save user data
                            if(result == "SUCCESS") {
                                val sharedPref = context.getSharedPreferences("logged_in_patient", Context.MODE_PRIVATE)
                                sharedPref.edit()
                                    .putString("patient_id", newId)
                                    .putString("patient_name", fullName)
                                    .putString("phone_number", phoneNumber)
                                    .apply()

                                scope.launch {
                                    snackbarHostState.showSnackbar("Sign up successful", duration = SnackbarDuration.Short)
                                    context.startActivity(Intent(context, HomeScreen::class.java))
                                }
                            }
                        }
                    }
                }
            ) {Text("Sign Up") }

            Spacer(modifier = Modifier.height(24.dp))

            // Display text
            Text(
                text = "Already have account?",
                style = TextStyle(fontSize = 16.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            // Display to Login to navigate to login screen
            Text(
                text = "Login",
                color = Color.Blue,
                style = TextStyle(fontSize = 16.sp,),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp).clickable{context.startActivity(Intent(context, LoginScreen::class.java))}
            )
        }
    }
}