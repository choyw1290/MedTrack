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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeawon.s34598111.medtrack.ui.theme.Final_medtrackTheme
import kotlinx.coroutines.launch

class ClinicianLoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Final_medtrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ClinicianLoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ClinicianLoginScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var inputKey by rememberSaveable { mutableStateOf("") }
    var inputError by rememberSaveable { mutableStateOf(false) }

    val correctKey = "dollar-entry-apples"

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(16.dp).fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            // Screen title
            Text(
                text = "Clinician Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Access key input field
            OutlinedTextField(
                value = inputKey,
                onValueChange = {
                    inputKey = it
                    inputError = false
                },
                label = { Text("Enter Access Key")},
                modifier = Modifier.fillMaxWidth(),
                isError = inputError
            )

            if(inputError){
                Text(
                    text = "Input key cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Login button to validate access key
            Button(onClick = {
                inputError = inputKey.isBlank()

                if(!inputError) {
                    if(inputKey == correctKey) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Login successful")
                        }
                        context.startActivity(Intent(context, ClinicianDashBoardScreen::class.java))
                    } else {
                        scope.launch{snackbarHostState.showSnackbar("Invalid access key")}
                    }
                }
            }) {Text("Login") }
        }
    }
}