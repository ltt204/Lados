package org.nullgroup.lados.screens.common

import android.app.Activity
import android.graphics.Paint.Align
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.Product.FacebookButtonLogin
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.implementations.GoogleAuthRepositoryImplement
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.navigations.AdminGraph
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.StaffGraph
import org.nullgroup.lados.ui.theme.Purple40
import org.nullgroup.lados.viewmodels.AuthScreenViewModel
import org.nullgroup.lados.viewmodels.GoogleAuthViewModel
import org.nullgroup.lados.viewmodels.LoginScreenViewModel
import org.nullgroup.lados.viewmodels.LoginStep
import javax.inject.Inject

//@Composable
//fun LoginScreen(
//    modifier: Modifier = Modifier,
//    //onNavigateToSignUp: () -> Unit,
//    //onNavigateToForgotPassword: () -> Unit,
//) {
//    // val authScreenViewModel: AuthScreenViewModel = hiltViewModel<AuthScreenViewModel>()
//    val userName = remember {
//        mutableStateOf("")
//    }
//    val password = remember {
//        mutableStateOf("")
//    }
//
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Spacer(modifier = Modifier.height(40.dp))
//
//        // Implementation
//        Text(
//            text = "Sign in",
//            style = MaterialTheme.typography.headlineMedium,
//            fontWeight = FontWeight.Bold,
//        )
//
//        if (userName.value.isEmpty()) {
//            OutlinedTextField(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                value = userName.value,
//                onValueChange = { userName.value = it },
//                label = { Text("Email Address") },
//                singleLine = true,
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
//            )
//        }
//
//
//        OutlinedTextField(
//            value = password.value,
//            onValueChange = { password.value = it },
//            label = { Text("Password") },
//            modifier = Modifier
//                .fillMaxWidth(),
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//        Button(
//            onClick = {
//                // authScreenViewModel.login(userName.value, password.value)
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
//        ) {
//            Text("Log In")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("Does not have an account? Sign up.",
//            modifier = Modifier.clickable {
//                // onNavigateToSignup()
//
//            }
//        )
//    }
//
//    // val loginResult = authScreenViewModel.result.collectAsState().value
//    // Log.d("LoginScreen", "Login result: $loginResult")
//    // val userRole = authScreenViewModel.user.collectAsState().value
////    if (loginResult.isSuccess && userRole != null) {
////        Log.d("LoginScreen", "User role: $userRole")
////        when (userRole) {
////            UserRole.CUSTOMER.name -> {
////                CustomerGraph()
////            }
////
////            UserRole.ADMIN.name -> {
////                AdminGraph()
////            }
////
////            UserRole.STAFF.name -> {
////                StaffGraph()
////            }
////        }
////    }
//}


@Composable
fun EmailScreen(
    modifier: Modifier = Modifier,
    lifecycleScope: LifecycleCoroutineScope,
    navController: NavHostController,
) {
    val googleAuthViewModel = hiltViewModel<GoogleAuthViewModel>()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    val signInResult = googleAuthViewModel.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    googleAuthViewModel.onSignInResult(signInResult)
                }
            }
        }
    )
    val loginScreenViewModel = hiltViewModel<LoginScreenViewModel>()
    var email by remember {
        mutableStateOf("")
    }

    val state = googleAuthViewModel.state
    LaunchedEffect(state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            // Xử lý khi đăng nhập thành công
        }
    }

    if (state.signInError != null) {
        // Hiển thị lỗi nếu có
        Toast.makeText(
            LocalContext.current,
            state.signInError,
            Toast.LENGTH_LONG
        ).show()
    }

    fun handleSignInWithGoogle() {
        lifecycleScope.launch {
            val signInIntentSender = googleAuthViewModel.signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )

            Log.d("LoginScreen", "handleSignInWithGoogle: $signInIntentSender")
        }
        Log.d("LoginScreen", "handleSignInWithGoogle: $launcher")
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
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )

        Button(
            onClick = {
                loginScreenViewModel.setEmail(email)
                loginScreenViewModel.validateEmail()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text("Continue")
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
                    navController.navigate("register")
                })
        }

        Spacer(modifier = Modifier.height(40.dp))

        SocialLoginButtons(
            onClickGoggle = { handleSignInWithGoogle() }
        )
    }
}

@Composable
fun SocialLoginButtons(
    modifier: Modifier = Modifier,
    onClickGoggle: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SocialLoginButton(
            text = "Continue With Google",
            icon = R.drawable.ic_google,
            onClick = { onClickGoggle() }
        )

//        SocialLoginButton(
//            text = "Continue With Facebook",
//            icon = R.drawable.ic_facebook,
//            onClick = {}
//        )
        FacebookButtonLogin(activity = LocalContext.current as ComponentActivity)

        SocialLoginButton(
            text = "Continue With Twitter",
            icon = R.drawable.ic_twitter,
            onClick = {}
        )
    }
}

@Composable
fun SocialLoginButton(
    text: String, @DrawableRes icon: Int, onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = Color.Black
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
                loginScreenViewModel.login(password)
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
                    navController.navigate("forgot_password")
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    lifecycleScope: LifecycleCoroutineScope,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val loginScreenViewModel = hiltViewModel<LoginScreenViewModel>()
    val loginStep by loginScreenViewModel.loginStep.collectAsState()
    val authScreenViewModel = hiltViewModel<AuthScreenViewModel>()

    BackHandler(enabled = loginStep is LoginStep.Password) {
        loginScreenViewModel.onBackPressed()
    }

    when (loginStep) {
        LoginStep.Email -> EmailScreen(
            modifier = modifier,
            lifecycleScope = lifecycleScope,
            navController = navController
        )

        LoginStep.Password -> PasswordScreen(
            navController = navController,
            modifier = modifier,
        )
    }

    val loginResult = loginScreenViewModel.result.collectAsState().value
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {

}