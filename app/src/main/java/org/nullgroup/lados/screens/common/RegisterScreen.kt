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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.SignIn.ButtonSubmit
import org.nullgroup.lados.compose.SignIn.CustomTextField
import org.nullgroup.lados.compose.SignIn.Headline
import org.nullgroup.lados.compose.SignIn.TextClickable
import org.nullgroup.lados.compose.SignIn.TextNormal
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
import org.nullgroup.lados.viewmodels.states.ResourceState


@Composable
fun RegisterScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val registerViewModel = hiltViewModel<RegisterScreenViewModel>()
    val registerState by registerViewModel.registerState.collectAsState()
    val context = LocalContext.current

    when (val state = registerState) {
        is ResourceState.Error -> {
            RegisterInputScreen(navController, modifier)
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
        }

        ResourceState.Idle -> {
            RegisterInputScreen(navController, modifier)
        }

        ResourceState.Loading -> {
            RegisterInputScreen(navController, modifier)
            LoadingScreen(modifier = Modifier.fillMaxSize())
        }

        is ResourceState.Success -> {
            Toast.makeText(context, "You create account successful", Toast.LENGTH_SHORT).show()

            val userRole = (registerState as ResourceState.Success).data?.role

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

@Composable
fun RegisterInputScreen(navController: NavController, modifier: Modifier = Modifier) {

    val registerViewModel = hiltViewModel<RegisterScreenViewModel>()
    val registerState by registerViewModel.registerState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    var firstNameTouched by remember { mutableStateOf(false) }
    var lastNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current


    LaunchedEffect(registerState) {
        when (registerState) {
            is ResourceState.Error -> {}
            ResourceState.Idle -> {}
            ResourceState.Loading -> {}
            is ResourceState.Success -> {
                registerViewModel.handleEvent(
                    RegisterScreenEvent.HandleLogin(
                        email,
                        password,
                    )
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        Headline(text = "Create Account")

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "First Name",
            text = firstName,
            onValueChange = {
                firstName = it
                firstNameTouched = true
                if (firstNameError && registerViewModel.validateNotEmpty(firstName)) {
                    firstNameError = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!firstNameTouched) return@onFocusChanged
                    if (!it.isFocused) {
                        firstNameError = !registerViewModel.validateNotEmpty(firstName)
                    }
                },
            isError = firstNameError,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (!registerViewModel.validateNotEmpty(firstName)) {
                        firstNameError = true
                    } else {
                        firstNameError = false
                    }
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Last Name",
            text = lastName,
            onValueChange = {
                lastName = it
                lastNameTouched = true
                if (lastNameError && registerViewModel.validateNotEmpty(it)) {
                    lastNameError = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!lastNameTouched) return@onFocusChanged
                    if (!it.isFocused) {
                        lastNameError = !registerViewModel.validateNotEmpty(lastName)
                    }
                },
            isError = lastNameError,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (!registerViewModel.validateEmail(lastName)) {
                        lastNameError = true
                    } else {
                        lastNameError = false
                    }
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Email Address",
            text = email,
            onValueChange = {
                email = it
                emailTouched = true
                if (emailError && registerViewModel.validateEmail(it)) {
                    emailError = false
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (!registerViewModel.validateEmail(email)) {
                        emailError = true
                    } else {
                        emailError = false
                    }
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!emailTouched) return@onFocusChanged
                    if (!it.isFocused) {
                        emailError = !registerViewModel.validateEmail(email)
                    }
                },
            isError = emailError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Password",
            text = password,
            onValueChange = {
                password = it
                passwordTouched = true
                if (passwordError && registerViewModel.validatePassword(it)) {
                    passwordError = false
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (!registerViewModel.validatePassword(password)) {
                        passwordError = true
                    } else {
                        passwordError = false
                    }
                }
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!passwordTouched) return@onFocusChanged
                    if (!it.isFocused) {
                        passwordError = !registerViewModel.validatePassword(password)
                    }
                },
            isError = passwordError,
        )

        Spacer(modifier = Modifier.height(32.dp))

        ButtonSubmit(
            text = "Register",
            onClick = {
                registerViewModel.handleEvent(
                    RegisterScreenEvent.HandleSignUp(
                        firstName,
                        lastName,
                        email,
                        password,
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextNormal(
                text = "Forgot password? "
            )
            TextClickable(
                text = "Reset",
                onClick = {
                    navController.navigate("forgot_password")
                },
            )
        }
    }
}