package org.nullgroup.lados.screens.admin.category

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.toByteArray
import org.nullgroup.lados.utilities.toDrawable
import org.nullgroup.lados.viewmodels.admin.category.AddCategoryUiState
import org.nullgroup.lados.viewmodels.admin.category.AddCategoryViewModel

@Composable
fun EditCategoryScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: AddCategoryViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val addCategoryUiState by viewModel.addCategoryUiState.collectAsState()

    var name by rememberSaveable {
        mutableStateOf(
            mapOf(
                "vi" to "",
                "en" to ""
            )
        )
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageByteArray by remember { mutableStateOf<ByteArray?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->

        uri?.toDrawable(context)?.toByteArray()?.let {
            imageByteArray = it
        }

        uri?.let {
            selectedImageUri = it
        }
    }

    if (addCategoryUiState is AddCategoryUiState.Success) {
        navController.navigate(Screen.Admin.CategoryManagement.route)
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
                        viewModel.addCategory(
                            name = name,
                            image = imageByteArray ?: ByteArray(0)
                        )
                    }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = LadosTheme.colorScheme.primary,
                        contentColor = LadosTheme.colorScheme.onPrimary
                    ),
                    enabled = name["vi"]?.isNotEmpty() == true && name["en"]?.isNotEmpty() == true
                ) {
                    Text(
                        text = "Create",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

    ) { it ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LadosTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (addCategoryUiState is AddCategoryUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LadosTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    CategoryImageSection(
                        imageUri = selectedImageUri?.toString()
                            ?: "https://firebasestorage.googleapis.com/v0/b/lados-8509b.firebasestorage.app/o/images%2Fproducts%2Fimg_placeholder.jpg?alt=media&token=1f1fed12-8ead-4433-b2a4-c5e1d765290e",
                        onEditImage = {
                            imagePickerLauncher.launch(
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
    }
}