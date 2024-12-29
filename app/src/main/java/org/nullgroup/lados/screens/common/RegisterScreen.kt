package org.nullgroup.lados.screens.common

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.signin.ButtonSubmit
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.compose.signin.Headline
import org.nullgroup.lados.compose.signin.TextClickable
import org.nullgroup.lados.compose.signin.TextNormal
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.common.RegisterScreenViewModel
import org.nullgroup.lados.viewmodels.common.events.RegisterScreenEvent
import org.nullgroup.lados.viewmodels.common.states.RegisterScreenStepState
import org.nullgroup.lados.viewmodels.common.states.ResourceState


@Composable
fun RegisterScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val registerViewModel = hiltViewModel<RegisterScreenViewModel>()
    val registerState by registerViewModel.registerState.collectAsState()
    val registerStepState by registerViewModel.registerStepState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is ResourceState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }

            ResourceState.Idle -> {

            }

            ResourceState.Loading -> {

            }

            is ResourceState.Success -> {
                Toast.makeText(context,
                    context.getString(R.string.auth_create_account_successful), Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (registerStepState) {
        RegisterScreenStepState.BackLogin -> {}
        RegisterScreenStepState.EnterInfo -> {
            RegisterInputScreen(navController, modifier)
        }

        is RegisterScreenStepState.Notification -> {
            val email = (registerStepState as RegisterScreenStepState.Notification).email
            NotifySendEmailScreen(
                text = stringResource(R.string.auth_verify_account, email),
                onClick = {
                    registerViewModel.handleEvent(
                        RegisterScreenEvent.HandleBackLogin(
                            navController
                        )
                    )
                },
                modifier = modifier,
            )
        }
    }

    if (registerState is ResourceState.Loading) {
        LoadingScreen(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun RegisterInputScreen(navController: NavController, modifier: Modifier = Modifier) {

    val registerViewModel = hiltViewModel<RegisterScreenViewModel>()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    var firstNameTouched by remember { mutableStateOf(false) }
    var lastNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var phoneTouched by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        Headline(text = stringResource(R.string.auth_create_account_header))

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = stringResource(R.string.auth_first_name),
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

        if (firstNameError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.auth_first_name_require),
                color = LadosTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = stringResource(R.string.auth_last_name),
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

        if (lastNameError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.auth_last_name_required),
                color = LadosTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = stringResource(id = R.string.auth_email_address),
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

        if (emailError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.auth_invalid_email),
                color = LadosTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = stringResource(R.string.auth_password),
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


        if (passwordError) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.auth_password_length_requirement),
                    color = LadosTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.auth_password_form_requirement),
                    color = LadosTheme.colorScheme.error,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = stringResource(R.string.auth_phone_number),
            text = phone,
            onValueChange = {
                phone = it
                phoneTouched = true
                if (phoneError && registerViewModel.validatePhoneNumber(it)) {
                    phoneError = false
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (!registerViewModel.validatePhoneNumber(phone)) {
                        phoneError = true
                    } else {
                        phoneError = false
                    }
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!phoneTouched) return@onFocusChanged
                    if (!it.isFocused) {
                        phoneError = !registerViewModel.validatePhoneNumber(phone)
                    }
                },
            isError = phoneError,
        )

        if (phoneError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.auth_phone_requirement),
                color = LadosTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        ButtonSubmit(
            text = stringResource(R.string.auth_register),
            onClick = {
                if (!registerViewModel.validateEmail(email)) emailError = true
                if (!registerViewModel.validatePassword(password)) passwordError = true
                if (!registerViewModel.validateNotEmpty(firstName)) firstNameError = true
                if (!registerViewModel.validateNotEmpty(lastName)) lastNameError = true

                if (!firstNameError && !lastNameError && !emailError && !passwordError) {
                    registerViewModel.handleEvent(
                        RegisterScreenEvent.HandleSignUp(
                            firstName,
                            lastName,
                            email,
                            password,
                            phone,
                            context,
                        )
                    )
                }
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
                text = stringResource(R.string.auth_forgot_password)
            )
            TextClickable(
                text = stringResource(R.string.auth_reset),
                onClick = {
                    navController.navigate("forgot_password")
                },
            )
        }
    }
}