package org.nullgroup.lados.screens.common

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.navigations.AdminGraph
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.StaffGraph
import org.nullgroup.lados.viewmodels.common.AuthScreenViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
) {
    val authScreenViewModel: AuthScreenViewModel = hiltViewModel()

    val userName = remember {
        mutableStateOf("customer@test.com")
    }
    val password = remember {
        mutableStateOf("customer123")
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
                authScreenViewModel.login(userName.value, password.value)
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

            }
        )
    }

    val loginResult = authScreenViewModel.result.collectAsState().value
    Log.d("LoginScreen", "Login result: $loginResult")
    val userRole = authScreenViewModel.user.collectAsState().value
    if (loginResult.isSuccess && userRole != null) {
        Log.d("LoginScreen", "User role: $userRole")
        when (userRole) {
            UserRole.CUSTOMER.name -> {
                CustomerGraph()
            }

            UserRole.ADMIN.name -> {
                AdminGraph()
            }

            UserRole.STAFF.name -> {
                StaffGraph()
            }
        }
    }
}
