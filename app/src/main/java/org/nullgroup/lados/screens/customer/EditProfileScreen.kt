package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.profile.ConfirmDialog
import org.nullgroup.lados.compose.profile.CustomTextField
import org.nullgroup.lados.compose.profile.LoadOnProgress
import org.nullgroup.lados.compose.profile.ProfileTopAppBar
import org.nullgroup.lados.ui.theme.Typography
import org.nullgroup.lados.viewmodels.customer.EditProfileViewModel
import org.nullgroup.lados.viewmodels.customer.UserUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    var saveConfirmation by remember { mutableStateOf(false) }
    val userInfo = viewModel.userUisState.value

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding()),
        topBar = {
            ProfileTopAppBar(
                onBackClick = { navController?.navigateUp() },
                content = "Edit profile"
            )
        }) { innerPadding ->
        when (userInfo) {
            is UserUiState.Loading -> LoadingContent()
            is UserUiState.Success -> SuccessContent(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                userInfo = userInfo,
                saveConfirmation = saveConfirmation,
                onSaveClick = { saveConfirmation = true },
                onDismissRequest = { saveConfirmation = false },
                navController = navController
            )

            is UserUiState.Error -> ErrorContent(userInfo.message)
        }

        if (saveConfirmation) {
            ConfirmDialog(
                title = { Text(text = "Confirm information") },
                message = { Text(text = "userInfo.value.toString()") },
                onDismissRequest = { saveConfirmation = false },
                confirmButton = {
                    saveConfirmation = false
                    navController?.navigateUp()
                }
            )
        }
    }
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    LoadOnProgress(
        modifier = modifier
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(text = "Loading...")
    }
}

@Composable
fun SuccessContent(
    modifier: Modifier = Modifier,
    userInfo: UserUiState.Success,
    saveConfirmation: Boolean,
    onSaveClick: () -> Unit,
    onDismissRequest: () -> Unit,
    navController: NavController?
) {
    val userName = remember { mutableStateOf(userInfo.user.name) }
    val userPhone = remember { mutableStateOf(userInfo.user.phoneNumber) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {
            ProfileImage()
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = userInfo.user.email,
                style = Typography.headlineSmall
            )
        }
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = userName.value,
            onValueChange = { userName.value = it },
            header = "Name",
            placeHolder = "Name",
            trailingIcon = {
                IconButton(onClick = { userName.value = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = userPhone.value,
            onValueChange = { userPhone.value = it },
            header = "Phone number",
            placeHolder = "Phone number",
            trailingIcon = {
                IconButton(onClick = { userPhone.value = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = onSaveClick
        ) {
            Text(text = "Save")
        }
    }

    if (saveConfirmation) {
        ConfirmDialog(
            title = { Text(text = "Confirm information") },
            message = { Text(text = userInfo.user.toString()) },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                onDismissRequest()
                navController?.navigateUp()
            }
        )
    }
}

@Composable
fun ProfileImage(
    modifier : Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.clip(CircleShape),
            contentScale = ContentScale.Crop,
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Profile Picture"
        )
        OutlinedIconButton(
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.BottomEnd)
                .size(30.dp),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = Color.White,
                containerColor = Color(0xFF8E6CEF)
            ),
            border = BorderStroke(2.dp, Color.White),
            onClick = { /*TODO: call ACTION_PICK with Mime type is image to upload image from gallery*/ }
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Filled.Edit,
                contentDescription = null
            )
        }
    }
}

@Composable
fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Error: $message")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}