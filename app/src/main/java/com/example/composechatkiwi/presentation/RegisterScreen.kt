package com.example.composekiwi

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.composechatkiwi.Destination
import com.example.composechatkiwi.presentation.viewmodels.ChatViewModel
import com.example.composechatkiwi.presentation.viewmodels.LoginRegisterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistrationScreen(navHostController: NavHostController, vm: LoginRegisterViewModel = viewModel()) {

    DisposableEffect(Unit) {
        val auth = Firebase.auth
        val user = auth.currentUser
        if (user != null) {
            navHostController.navigate(Destination.Main.route)
        }
        onDispose {
            Log.d("gaumarjos", "gaitisha registeri")
        }
    }

    var emailValue by remember { mutableStateOf(TextFieldValue()) }
    var usernameValue by remember { mutableStateOf(TextFieldValue()) }
    var passwordValue by remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Users List") },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 76.dp, start = 15.dp, end = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = emailValue,
                    onValueChange = { emailValue = it },
                    label = { Text(text = "Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = usernameValue,
                    onValueChange = { usernameValue = it },
                    label = { Text(text = "Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it },
                    label = { Text(text = "Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        vm.registerUser(usernameValue.text, emailValue.text, passwordValue.text)
                        navHostController.navigate(Destination.Main.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Register")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navHostController.navigate(Destination.Login.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Login")
                }
            }
        }
    )
}


