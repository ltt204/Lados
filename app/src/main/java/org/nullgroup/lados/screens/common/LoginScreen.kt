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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.navigations.AdminGraph
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.StaffGraph
import org.nullgroup.lados.viewmodels.LoginScreenViewModel
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent
import org.nullgroup.lados.viewmodels.states.LoginScreenState
import org.nullgroup.lados.viewmodels.states.LoginScreenStepState
import kotlin.math.log

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = "Sign in",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
            ),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 1f,
                    red = 244f / 255,
                    green = 244f / 255,
                    blue = 244f / 255,
                ),
                focusedBorderColor = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 1f,
                    red = 142f / 255,
                    green = 108f / 255,
                    blue = 239f / 255,
                ),
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 1f,
                    red = 244f / 255,
                    green = 244f / 255,
                    blue = 244f / 255,
                ),
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 1f,
                    red = 244f / 255,
                    green = 244f / 255,
                    blue = 244f / 255,
                ),
                errorBorderColor = MaterialTheme.colorScheme.error,
            )
        )

        Button(
            onClick = {
                if (loginScreenViewModel.isValidateEmail(email)) {
                    isError = false
                } else {
                    isError = true
                }

                loginScreenViewModel.handleLoginEvent(LoginScreenEvent.HandleEnterEmail(email))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(
                    alpha = 1f,
                    red = 142f / 255,
                    green = 108f / 255,
                    blue = 239f / 255,
                )
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Don't have an Account? ")
            Text(text = "Create One",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    loginScreenViewModel.handleLoginEvent(
                        LoginScreenEvent.HandleSignUp(
                            navController
                        )
                    )
                })
        }

        Spacer(modifier = Modifier.height(40.dp))

        val context = LocalContext.current
        SocialLoginButtons(
            onClickGoggle = {
                loginScreenViewModel.handleLoginEvent(
                    LoginScreenEvent.HandleLogInWithGoogle(
                        launcher
                    )
                )
            },
            onClickFacebook = {
                loginScreenViewModel.handleLoginEvent(
                    LoginScreenEvent.HandleLogInWithFacebook(
                        context as ComponentActivity
                    )
                )
            }
        )
    }
}

@Composable
fun SocialLoginButtons(
    modifier: Modifier = Modifier,
    onClickGoggle: () -> Unit,
    onClickFacebook: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SocialLoginButton(
            text = "Continue With Google",
            icon = R.drawable.ic_google,
            onClick = onClickGoggle
        )

        SocialLoginButton(
            text = "Continue With Facebook",
            icon = R.drawable.ic_facebook,
            onClick = onClickFacebook
        )
    }
}

@Composable
fun SocialLoginButton(
    text: String, @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(
                alpha = 1f,
                red = 244f / 255,
                green = 244f / 255,
                blue = 244f / 255,
            ),
            contentColor = MaterialTheme.colorScheme.primary.copy(
                alpha = 1f,
                red = 25f / 255,
                green = 25f / 255,
                blue = 25f / 255,
            )
        ),
        border = BorderStroke(0.dp, Color.Transparent),
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
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

    when (val state = loginState) {
        is LoginScreenState.Error -> {
            PasswordInput(navController, modifier)
            Toast.makeText(
                LocalContext.current,
                state.message ?: "Login Failed",
                Toast.LENGTH_SHORT
            ).show()
        }

        LoginScreenState.Idle -> {
            PasswordInput(navController, modifier)
        }

        LoginScreenState.Loading -> {
            PasswordInput(navController, modifier)
        }

        is LoginScreenState.Success -> {
            PasswordInput(navController, modifier)
            Toast.makeText(LocalContext.current, "Login Success", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun PasswordInput(navController: NavHostController, modifier: Modifier = Modifier) {
    val loginScreenViewModel = hiltViewModel<LoginScreenViewModel>()
    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = "Sign in",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            )
        )

        Button(
            onClick = {
                loginScreenViewModel.handleLoginEvent(LoginScreenEvent.HandleEnterPassword(password))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text("Log In")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Forgot Password? ")
            Text(
                text = "Reset",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
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