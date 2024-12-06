package org.nullgroup.lados.screens.common

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.SignIn.ButtonSubmit
import org.nullgroup.lados.compose.SignIn.CustomTextField
import org.nullgroup.lados.compose.SignIn.Headline
import org.nullgroup.lados.compose.SignIn.OutlineButton
import org.nullgroup.lados.compose.SignIn.TextClickable
import org.nullgroup.lados.compose.SignIn.TextNormal
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.navigations.AdminGraph
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.StaffGraph
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.LoginScreenViewModel
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent
import org.nullgroup.lados.viewmodels.states.LoginScreenStepState
import org.nullgroup.lados.viewmodels.states.ResourceState

@Composable
fun EmailScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val loginScreenViewModel = hiltViewModel<LoginScreenViewModel>()

    var email by remember {
        mutableStateOf("")
    }

    var isError by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loginScreenViewModel.onGoogleSignInResult(result)
            }
        }
    )

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Headline("Sign in")

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomTextField(
                label = "Email Address",
                text = email,
                onValueChange = {
                    email = it
                    if (loginScreenViewModel.isValidateEmail(it)) {
                        isError = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "email",
                        tint = LadosTheme.colorScheme.onBackground
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = isError
            )

            if (isError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email invalid",
                    color = LadosTheme.colorScheme.error,
                )
            }
        }

        ButtonSubmit(
            text = "Continue",
            onClick = {
                if (loginScreenViewModel.isValidateEmail(email)) {
                    loginScreenViewModel.handleLoginEvent(LoginScreenEvent.HandleEnterEmail(email))
                    isError = false
                } else {
                    Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show()
                    isError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            TextNormal("Don't have an Account? ")
            TextClickable(
                text = "Create One",
                onClick = {
                    loginScreenViewModel.handleLoginEvent(
                        LoginScreenEvent.HandleSignUp(
                            navController
                        )
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            OutlineButton(
                text = "Continue With Google",
                icon = R.drawable.ic_google,
                onClick = {
                    loginScreenViewModel.handleLoginEvent(
                        LoginScreenEvent.HandleLogInWithGoogle(
                            launcher,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PasswordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val loginScreenViewModel = hiltViewModel<LoginScreenViewModel>()
    val loginState by loginScreenViewModel.loginState.collectAsState()
    var password by remember {
        mutableStateOf("")
    }
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    var isError by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Headline("Sign in")

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            CustomTextField(
                label = "Password",
                text = password,
                onValueChange = {
                    password = it
                    if (password.length >= 8) {
                        isError = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock, contentDescription = "Password",
                        tint = LadosTheme.colorScheme.onBackground
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = description,
                            tint = LadosTheme.colorScheme.onBackground
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = isError,
            )

            if (isError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Password must be at least 8 characters",
                    color = LadosTheme.colorScheme.error,
                )
            }
        }

        ButtonSubmit(
            text = "Sign in",
            onClick = {
                if (password.length >= 8) {
                    loginScreenViewModel.handleLoginEvent(
                        LoginScreenEvent.HandleEnterPassword(
                            password
                        )
                    )
                    isError = false
                } else {
                    isError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            TextNormal(text = "Forgot Password? ")
            TextClickable(
                text = "Reset",
                onClick = {
                    loginScreenViewModel.handleLoginEvent(
                        LoginScreenEvent.HandleForgotPassword(
                            navController
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val loginScreenViewModel = hiltViewModel<LoginScreenViewModel>()
    val loginStep by loginScreenViewModel.loginStep.collectAsState()
    val loginState by loginScreenViewModel.loginState.collectAsState()

    val context = LocalContext.current

    BackHandler(enabled = loginStep is LoginScreenStepState.Password) {
        loginScreenViewModel.onBackPressed()
    }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is ResourceState.Error -> {
                val message = state.message ?: "Login Failed"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            ResourceState.Idle -> {}

            ResourceState.Loading -> {
            }

            is ResourceState.Success -> {
                Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (loginStep) {
        is LoginScreenStepState.Email -> {
            EmailScreen(navController = navController, modifier = modifier)
        }

        is LoginScreenStepState.Password -> {
            PasswordScreen(navController = navController, modifier = modifier)
        }

        is LoginScreenStepState.Home -> {
            val userRole = (loginStep as LoginScreenStepState.Home).user.role
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

    if (loginState is ResourceState.Loading) {
        LoadingScreen(modifier)
    }
}
