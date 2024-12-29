package org.nullgroup.lados.screens.customer.profile

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.DefaultCenterTopAppBar
import org.nullgroup.lados.compose.profile.ConfirmDialog
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.Typography
import org.nullgroup.lados.utilities.toByteArray
import org.nullgroup.lados.utilities.toDrawable
import org.nullgroup.lados.viewmodels.customer.profile.EditProfileViewModel
import org.nullgroup.lados.viewmodels.customer.profile.ProfilePictureUiState
import org.nullgroup.lados.viewmodels.customer.profile.UserUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    var saveConfirmation by remember { mutableStateOf(false) }
    var cancelConfirmation by remember { mutableStateOf(false) }
    val userInfo = viewModel.userUiState.value
    val profilePictureUiState = viewModel.profilePictureUiState.value
    var isSaveClick by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    val singlePicturePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.toDrawable(context)?.toByteArray()?.let {
                viewModel.onProfilePictureChanged(
                    uri = uri.toString(),
                    image = it
                )
            }
        }
    BackHandler {
        cancelConfirmation = true
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DefaultCenterTopAppBar(
                onBackClick = { cancelConfirmation = true },
                content = stringResource(R.string.edit_profile_title)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        when (userInfo) {
            is UserUiState.Loading -> LoadingContent()
            is UserUiState.Success -> SuccessContent(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                userInfo = userInfo,
                onSaveClick = { saveConfirmation = true },
                onEditImage = { /*TODO: call ACTION_PICK with Mime type is image to upload image from gallery*/
                    singlePicturePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onNameChanged = { viewModel.onNameChanged(it) },
                onPhoneChanged = { viewModel.onPhoneChanged(it) },
                isInfoChanged = viewModel.isInfoChanged.value
            )

            is UserUiState.Error -> ErrorContent(userInfo.message)
        }

        if (saveConfirmation) {
            ConfirmDialog(
                title = { Text(text = "Confirm information") },
                message = { Text(text = "Are you sure you want to save new information?") },
                onDismissRequest = { saveConfirmation = false },
                confirmButton = {
                    saveConfirmation = false
                    isSaveClick = true
                    viewModel.onSaveClicked()
                }
            )
        }

        if (isSaveClick) {
            when (profilePictureUiState) {
                is ProfilePictureUiState.Loading,
                is ProfilePictureUiState.Initial,
                -> {
                    Log.d("EditProfileScreen", "ProfilePictureUiState.Loading")
                }

                is ProfilePictureUiState.Error -> {
                    ErrorContent(profilePictureUiState.message)
                }

                is ProfilePictureUiState.Success -> {
                    Log.d("EditProfileScreen", "ProfilePictureUiState.Success")
                    isSaveClick = false
                    navController?.navigateUp()
                }
            }
        }

        if (cancelConfirmation && viewModel.isInfoChanged.value) {
            ConfirmDialog(
                title = { Text(text = "Cancel editing") },
                message = { Text(text = "Are you sure you want to cancel editing?") },
                primaryButtonText = "Exit",
                onDismissRequest = {
                    cancelConfirmation = false
                },
                secondaryButtonText = "Continue",
                confirmButton = {
                    cancelConfirmation = false
                    navController?.navigateUp()
                }
            )
        } else if (cancelConfirmation) {
            cancelConfirmation = false
            navController?.navigateUp()
        }
    }
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
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
    onSaveClick: () -> Unit,
    onEditImage: () -> Unit,
    onNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    isInfoChanged: Boolean,
) {
    val userName = remember { mutableStateOf(userInfo.user.name) }
    val userPhone = remember { mutableStateOf(userInfo.user.phoneNumber) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage(
                modifier = Modifier,
                imageUri = userInfo.user.avatarUri,
                onEditImage = onEditImage
            )
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = userInfo.user.email,
                style = Typography.headlineSmall,
                color = LadosTheme.colorScheme.onBackground
            )
        }
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            text = userName.value,
            onValueChange = {
                userName.value = it
                onNameChanged(it)
            },
            label = "Name"
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            text = userPhone.value,
            onValueChange = {
                userPhone.value = it
                onPhoneChanged(it)
            },
            label = "Phone number"
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = onSaveClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = LadosTheme.colorScheme.primary,
                contentColor = LadosTheme.colorScheme.onPrimary,
                disabledContentColor = LadosTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            ),
            enabled = isInfoChanged
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    imageUri: String,
    onEditImage: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            loading = {
                LoadOnProgress(
                    modifier = Modifier
                        .clip(CircleShape)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                }
            },
            model = ImageRequest
                .Builder(context = LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
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
            onClick = {
                /*TODO: call ACTION_PICK with Mime type is image to upload image from gallery*/
                onEditImage()
            }
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
fun ProfileImagePreview() {
    ProfileImage(imageUri = "https://firebasestorage.googleapis.com/v0/b/lados-8509b.firebasestorage.app/o/images%2Fusers%2Fdefault_avatar.jpg?alt=media&token=5549abef-cbbd-4ff2-8332-f83fb79026ac")
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SuccessContentPreview() {
    Scaffold(
        topBar = {
            DefaultCenterTopAppBar(
                onBackClick = {},
                content = "Edit profile"
            )
        }
    ) { innerPadding ->
        SuccessContent(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            userInfo = UserUiState.Success(
                user = User(
                    name = "John Doe",
                    email = "",
                    phoneNumber = "",
                    avatarUri = "https://firebasestorage.googleapis.com/v0/b/lados-8509b.firebasestorage.app/o/images%2Fusers%2Fdefault_avatar.jpg?alt=media&token=5549abef-cbbd-4ff2-8332-f83fb79026ac"
                )
            ),
            onSaveClick = {},
            onEditImage = {},
            onNameChanged = {},
            onPhoneChanged = {},
            isInfoChanged = false
        )
    }
}
