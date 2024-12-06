package org.nullgroup.lados.screens.common

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.SignIn.ButtonSubmit
import org.nullgroup.lados.compose.SignIn.CustomTextField
import org.nullgroup.lados.compose.SignIn.Headline
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.ForgotPasswordScreenViewModel
import org.nullgroup.lados.viewmodels.events.ForgotPasswordScreenEvent
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
            NotifySendEmailScreen(
                modifier = modifier,
                text = "We Sent you an Email to reset your password.",
                onClick = {
                    forgotPasswordViewModel.handleEvent(
                        ForgotPasswordScreenEvent.HandleReturnToLogin(
                            navController
                        )
                    )
                }
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

        if (isError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Email invalid",
                color = LadosTheme.colorScheme.error,
            )
        }

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