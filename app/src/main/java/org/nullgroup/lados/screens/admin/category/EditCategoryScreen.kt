package org.nullgroup.lados.screens.admin.category

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.screens.customer.profile.ErrorContent
import org.nullgroup.lados.screens.customer.profile.LoadingContent
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.toByteArray
import org.nullgroup.lados.utilities.toDrawable
import org.nullgroup.lados.viewmodels.admin.category.CategoryPictureUiState
import org.nullgroup.lados.viewmodels.admin.category.EditCategoryUiState
import org.nullgroup.lados.viewmodels.admin.category.EditCategoryViewModel

@Composable
fun EditCategoryScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: EditCategoryViewModel = hiltViewModel(),
    navController: NavController,
    categoryId: String,
    modifier: Modifier = Modifier
) {

    var saveConfirmation by remember { mutableStateOf(false) }
    var cancelConfirmation by remember { mutableStateOf(false) }
    val categoryInfo = viewModel.categoryUiState.value
    val categoryPictureUiState = viewModel.categoryPictureUiState.value
    var isSaveClick by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.loadCategory(categoryId)
    }

    val context = LocalContext.current

    val singlePicturePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.toDrawable(context)?.toByteArray()?.let {
                viewModel.onCategoryPictureChanged(
                    uri = uri.toString(),
                    image = it
                )
            }
        }

    BackHandler {
        cancelConfirmation = true
    }


    var viName = ""
    var enName = ""

    if(categoryInfo is EditCategoryUiState.Success){
        viName = categoryInfo.category.categoryName["vi"] ?: ""
        enName = categoryInfo.category.categoryName["en"] ?: ""
    }

    Log.d(
        "EditCategoryScreen",
        "viName: $viName, enName: $enName"
    )
    var name by rememberSaveable {
        mutableStateOf(
            mapOf(
                "vi" to viName,
                "en" to enName
            )
        )
    }

    LaunchedEffect(viName, enName) {
        name = mapOf(
            "vi" to viName,
            "en" to enName
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(LadosTheme.colorScheme.background)
            .padding(paddingValues),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LadosTheme.colorScheme.background),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        saveConfirmation = true
                    }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = LadosTheme.colorScheme.primary,
                        contentColor = LadosTheme.colorScheme.onPrimary
                    ),
                    enabled = viewModel.isInfoChanged.value
                ) {
                    Text(
                        text = "Save",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

    ) { it -> val padding = it

        when (categoryInfo) {
            is EditCategoryUiState.Loading -> {
                LoadingContent()
            }

            is EditCategoryUiState.Success -> {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CategoryImageSection(
                            imageUri = categoryInfo.category.categoryImage
                                ?: "https://firebasestorage.googleapis.com/v0/b/lados-8509b.firebasestorage.app/o/images%2Fproducts%2Fimg_placeholder.jpg?alt=media&token=1f1fed12-8ead-4433-b2a4-c5e1d765290e",
                            onEditImage = {
                                singlePicturePicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }

                    Text(
                        text = "Name",
                        style = LadosTheme.typography.titleMedium,
                        color = LadosTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CustomTextField(
                        label = "Vietnamese Name",
                        text = name["vi"] ?: "",
                        onValueChange = {
                            viewModel.onNameChanged(mapOf("vi" to it, "en" to name["en"].toString()))
                            name = name.toMutableMap().apply { this["vi"] = it }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        isError = false
                    )

                    CustomTextField(
                        label = "English Name",
                        text = name["en"] ?: "",
                        onValueChange = {
                            viewModel.onNameChanged(mapOf("vi" to name["vi"].toString(), "en" to it))
                            name = name.toMutableMap().apply { this["en"] = it }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        isError = false
                    )

                    Spacer(modifier = Modifier.size(4.dp))
                }

            }

            is EditCategoryUiState.Error -> {
                ErrorContent(categoryInfo.message)
                Log.d("Edit error", categoryInfo.message.toString())

            }
        }
    }

    if (saveConfirmation) {
        org.nullgroup.lados.compose.profile.ConfirmDialog(
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
        when (categoryPictureUiState) {
            is CategoryPictureUiState.Loading,
            is CategoryPictureUiState.Initial,
                -> {
                Log.d("EditProfileScreen", "ProfilePictureUiState.Loading")
            }

            is CategoryPictureUiState.Error -> {
                ErrorContent(categoryPictureUiState.message)
            }

            is CategoryPictureUiState.Success -> {
                Log.d("EditProfileScreen", "ProfilePictureUiState.Success")
                isSaveClick = false
                navController?.navigateUp()
            }
        }
    }

    if (cancelConfirmation && viewModel.isInfoChanged.value) {
        org.nullgroup.lados.compose.profile.ConfirmDialog(
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