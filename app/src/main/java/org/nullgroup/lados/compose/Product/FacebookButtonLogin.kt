package org.nullgroup.lados.compose.Product

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.login.widget.LoginButton
import org.nullgroup.lados.data.repositories.implementations.LoginState
import org.nullgroup.lados.viewmodels.LoginScreenViewModel

@Composable
fun FacebookButtonLogin(
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
    loginViewModel: LoginScreenViewModel = hiltViewModel<LoginScreenViewModel>(),
) {
    AndroidView(
        factory = { context ->
            LoginButton(context).apply {
                setOnClickListener {
                    loginViewModel.loginWithFacebook(activity)
                }
            }
        }
    )

    val loginState by loginViewModel.loginState.collectAsState()

    when(loginState) {
        is LoginState.Initial -> {

        }
        is LoginState.Success -> {

        }
        is LoginState.Error -> {

        }
    }
}