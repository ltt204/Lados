package org.nullgroup.lados.screens.common

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.SignIn.ButtonSubmit
import org.nullgroup.lados.compose.SignIn.EmailTextField
import org.nullgroup.lados.compose.SignIn.Headline
import org.nullgroup.lados.compose.SignIn.OutlineButton
import org.nullgroup.lados.compose.SignIn.PasswordTextField
import org.nullgroup.lados.compose.SignIn.TextClickable
import org.nullgroup.lados.compose.SignIn.TextNormal
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.navigations.AdminGraph
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.StaffGraph
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.LoginScreenViewModel
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent
import org.nullgroup.lados.viewmodels.states.LoginScreenState
import org.nullgroup.lados.viewmodels.states.LoginScreenStepState

@Composable
fun EmailScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val loginScreenViewModel = hiltViewModel<LoginScreenViewModel>()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loginScreenViewModel.onGoogleSignInResult(result)
            }
        }
    )

    var email by remember {
        mutableStateOf("")
    }

    var isError by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Headline("Sign in")

        EmailTextField(
            email = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            isError = isError
        )

        ButtonSubmit(
            text = "Continue",
            onClick = {
                if (loginScreenViewModel.isValidateEmail(email)) {
                    loginScreenViewModel.handleLoginEvent(LoginScreenEvent.HandleEnterEmail(email))
                    isError = false
                } else {
                    isError = true
                    Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
            },
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
                            launcher
                        )
                    )
                }
            )

            OutlineButton(
                text = "Continue With Facebook",
                icon = R.drawable.ic_facebook,
                onClick = {
                    loginScreenViewModel.handleLoginEvent(
                        LoginScreenEvent.HandleLogInWithFacebook(
                            context as ComponentActivity
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun PasswordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
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

        PasswordTextField(
            password = password,
            onValueChange = { password = it },
            isError = isError,
            passwordVisible = passwordVisible,
            onPasswordClick = {
                passwordVisible = !passwordVisible
            }
        )

        ButtonSubmit(
            text = "Sign in",
            onClick = {
                loginScreenViewModel.handleLoginEvent(
                    LoginScreenEvent.HandleEnterPassword(
                        password
                    )
                )
            },
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

    val context = LocalContext.current
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginScreenState.Error -> {
                isError = true
                Toast.makeText(
                    context,
                    state.message ?: "Login Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            LoginScreenState.Idle -> {}

            LoginScreenState.Loading -> {}

            is LoginScreenState.Success -> {
                Toast.makeText(
                    context,
                    "Login Success",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    BackHandler(enabled = loginStep is LoginScreenStepState.Password) {
        loginScreenViewModel.onBackPressed()
    }

    when (loginState) {
        is LoginScreenState.Error -> {
            when (loginStep) {
                is LoginScreenStepState.Email -> EmailScreen(navController, modifier)
                is LoginScreenStepState.Password -> PasswordScreen(navController, modifier)
            }
        }

        LoginScreenState.Idle -> {
            when (loginStep) {
                is LoginScreenStepState.Email -> EmailScreen(navController, modifier)
                is LoginScreenStepState.Password -> PasswordScreen(navController, modifier)
            }
        }

        LoginScreenState.Loading -> {
            when (loginStep) {
                is LoginScreenStepState.Email -> EmailScreen(navController, modifier)
                is LoginScreenStepState.Password -> PasswordScreen(navController, modifier)
            }
        }

        is LoginScreenState.Success -> {
            val userRole = (loginState as LoginScreenState.Success).userRole
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