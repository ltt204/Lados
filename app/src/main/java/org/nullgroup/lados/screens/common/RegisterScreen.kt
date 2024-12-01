package org.nullgroup.lados.screens.common

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.SignIn.CustomTextField
import org.nullgroup.lados.compose.SignIn.Headline
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.navigations.AdminGraph
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.StaffGraph
import org.nullgroup.lados.viewmodels.LoginScreenViewModel
import org.nullgroup.lados.viewmodels.RegisterScreenViewModel
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent
import org.nullgroup.lados.viewmodels.events.RegisterScreenEvent
import org.nullgroup.lados.viewmodels.states.LoginScreenState
import org.nullgroup.lados.viewmodels.states.RegisterScreenState


@Composable
fun RegisterScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val registerViewModel = hiltViewModel<RegisterScreenViewModel>()
    val loginViewModel = hiltViewModel<LoginScreenViewModel>()

    val loginState by loginViewModel.loginState.collectAsState()
    val registerState by registerViewModel.registerState.collectAsState()
    val context = LocalContext.current


    when (val state = registerState) {
        is RegisterScreenState.Error -> {
            RegisterInputScreen(navController, modifier)
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
        }

        RegisterScreenState.Idle -> {
            RegisterInputScreen(navController, modifier)
        }

        RegisterScreenState.Loading -> {
            RegisterInputScreen(navController, modifier)
        }

        RegisterScreenState.Success -> {
            Toast.makeText(context, "You create account successful", Toast.LENGTH_SHORT).show()

            if (loginState is LoginScreenState.Success) {
                val userRole = (loginState as LoginScreenState.Success).userRole
                Log.d("role", userRole!!)
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
    }


}

@Composable
fun RegisterInputScreen(navController: NavController, modifier: Modifier = Modifier) {

    val registerViewModel = hiltViewModel<RegisterScreenViewModel>()
    val loginViewModel = hiltViewModel<LoginScreenViewModel>()

    val registerState by registerViewModel.registerState.collectAsState()
    val loginState by loginViewModel.loginState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Headline(text = "Create Account")

        CustomTextField(
            label = "First Name",
            text = firstName,
            onValueChange = { firstName = it },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Lastname",
            text = lastName,
            onValueChange = { lastName = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Email Address",
            text = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Password",
            text = password,
            onValueChange = { password = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                registerViewModel.handleEvent(
                    RegisterScreenEvent.HandleSignUp(
                        firstName,
                        lastName,
                        email,
                        password,
                        navController
                    )
                )
                loginViewModel.handleLoginEvent(LoginScreenEvent.HandleEnterEmail(email))
                loginViewModel.handleLoginEvent(LoginScreenEvent.HandleEnterPassword(password))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Continue",
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Forgot Password? ",
                color = Color.Gray
            )
            Text(
                text = "Reset",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("forgot_password") }
            )
        }
    }
}