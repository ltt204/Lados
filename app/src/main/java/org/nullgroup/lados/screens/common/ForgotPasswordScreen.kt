package org.nullgroup.lados.screens.common

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.SignIn.ButtonSubmit
import org.nullgroup.lados.compose.SignIn.CustomTextField
import org.nullgroup.lados.compose.SignIn.Headline
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.ForgotPasswordScreenViewModel
import org.nullgroup.lados.viewmodels.events.ForgotPasswordScreenEvent
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent
import org.nullgroup.lados.viewmodels.states.ForgotPasswordScreenState
import org.nullgroup.lados.viewmodels.states.ResourceState

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val forgotPasswordViewModel = hiltViewModel<ForgotPasswordScreenViewModel>()
    val forgotPasswordState by forgotPasswordViewModel.forgotPasswordState.collectAsState()
    val context = LocalContext.current

    when (val state = forgotPasswordState) {
        is ResourceState.Error -> {
            val message = state.message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            ForgotPasswordInputScreen(modifier)
        }

        ResourceState.Idle -> {
            ForgotPasswordInputScreen(modifier)
        }

        ResourceState.Loading -> {
            ForgotPasswordInputScreen(modifier)
            LoadingScreen(modifier = Modifier.fillMaxSize())
        }

        is ResourceState.Success -> {
            PasswordResetConfirmationScreen(
                navController = navController,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun ForgotPasswordInputScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    val forgotPasswordViewModel = hiltViewModel<ForgotPasswordScreenViewModel>()
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Headline(
            text = "Forgot password",
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Email Address",
            text = email,
            onValueChange = {
                email = it
                if (isError && forgotPasswordViewModel.isValidateEmail(email)) {
                    isError = false
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "forgot password email",
                    tint = LadosTheme.colorScheme.onBackground,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
        )

        Spacer(modifier = Modifier.height(32.dp))

        ButtonSubmit(
            text = "Continue",
            onClick = {
                if (!forgotPasswordViewModel.isValidateEmail(email)) {
                    isError = true
                } else {
                    isError = false
                    forgotPasswordViewModel.handleEvent(
                        ForgotPasswordScreenEvent.HandleResetPassword(
                            email
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}

@Composable
fun PasswordResetConfirmationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val forgotPasswordViewModel = hiltViewModel<ForgotPasswordScreenViewModel>()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFFFFB74D)
                )

                Canvas(modifier = Modifier) {
                    val path = Path().apply {
                        moveTo(-90f, -80f)
                        cubicTo(-200f, -140f, -100f, -250f, 0f, -250f)
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFFFFB74D),
                        style = Stroke(
                            width = 10f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    )
                    val arrowHead = Path().apply {
                        moveTo(50f, -250f)
                        lineTo(0f, -280f)
                        lineTo(0f, -220f)
                        close()
                    }

                    drawPath(path = arrowHead, color = Color(0xFFFFB74D))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "We Sent you an Email to reset your password.",
            textAlign = TextAlign.Center,
            style = LadosTheme.typography.headlineSmall,
            color = LadosTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ButtonSubmit(
            text = "Return to Login",
            onClick = {
                forgotPasswordViewModel.handleEvent(
                    ForgotPasswordScreenEvent.HandleReturnToLogin(
                        navController
                    )
                )
            },
        )
    }
}