package org.nullgroup.lados.screens.Common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit,
    onSignup: () -> Unit
) {
    val userName = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    var isPasswordShowed by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        // Implementation
        Text(text = "Sign in")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = userName.value,
            onValueChange = { userName.value = it },
            label = { Text("Username") }
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        )
        Button(
            onClick = {
                onLogin()
                // authViewModel.login(email, password)
                // if (authViewModel.result.value == Result.Success(true)) {
                // onLoginAccept()
                // email = ""
                // password = ""
                // } else {
                //      Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                // }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Log in")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Does not have an account? Sign up.",
            modifier = Modifier.clickable {
                // onNavigateToSignup()
                onSignup()
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    LoginScreen(
        onLogin = {},
        onSignup = {}
    )
}