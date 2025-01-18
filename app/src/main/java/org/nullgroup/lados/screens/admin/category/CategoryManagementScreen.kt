package org.nullgroup.lados.screens.admin.category

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.toByteArray
import org.nullgroup.lados.utilities.toDrawable
import org.nullgroup.lados.viewmodels.admin.category.CategoryManagementUiState
import org.nullgroup.lados.viewmodels.admin.category.CategoryManagementViewModel
import org.nullgroup.lados.viewmodels.admin.category.FilterItem
import org.nullgroup.lados.viewmodels.admin.category.categoriesSortOption

@Composable
fun CategoryManagementScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: CategoryManagementViewModel = hiltViewModel(),
    navController: NavController
) {

    val categoriesUiState by viewModel.categoriesUiState.collectAsState()

    Log.d("ManageProductScreen", "products: $categoriesUiState")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(LadosTheme.colorScheme.background)
            .padding(paddingValues),

        ) { it ->

        var searchQuery by remember { mutableStateOf("") }
        var openSort by remember { mutableStateOf(false) }
        var sortOption by remember { mutableStateOf("All") }
        var categoryOption by remember { mutableStateOf("All") }
        var showAddDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }
        var showConfirmDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LadosTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchTextField(
                    query = searchQuery,
                    onQueryChange = { value ->
                        searchQuery = value
                        viewModel.searchCategories(value)
                    },
                    modifier = Modifier.weight(1f),
                    onSearch = { }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                OptionButton(
                    item = FilterItem(
                        title = "Sort: $sortOption"
                    ),
                    modifier = Modifier.wrapContentWidth(),
                    onClick = { openSort = true }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        sortOption = "All"
                        viewModel.getSortedAndFilteredCategories()
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LadosTheme.colorScheme.error),
                    modifier = Modifier
                        .weight(1f)

                ) {
                    Text(
                        text = "Reset",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            ManageSection(
                onAddNewCategory = {
                    navController.navigate(Screen.Admin.AddCategory.route)
                },
                onDeleteAllSelected = { showConfirmDialog = true }
            )

            when (categoriesUiState) {
                is CategoryManagementUiState.Success -> {
                    val categories =
                        (categoriesUiState as CategoryManagementUiState.Success).categories
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories.size) { index ->
                            CategoryItem(categories[index])
                        }
                    }
                }

                is CategoryManagementUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LadosTheme.colorScheme.background),
                        contentAlignment = Alignment.Center,

                        ) {
                        CircularProgressIndicator(color = LadosTheme.colorScheme.primary)
                    }
                }

                is CategoryManagementUiState.Error -> {
                    val message = (categoriesUiState as CategoryManagementUiState.Error).message
                    Log.d("CategoryManagementScreen", "Error: $message")
                }

            }

            SortDialog(
                isOpen = openSort,
                onDismiss = { openSort = false },
                onSortOptionSelected = {
                    sortOption = it
                },
                currentOption = sortOption,
                onApply = {
                    Log.d("CategoryManagementScreen", "onApply: $sortOption")
                    Log.d(
                        "CategoryManagementScreen",
                        "onApply: ${viewModel.extractSortOption(sortOption)}"
                    )
                    viewModel.getSortedAndFilteredCategories(
                        sortByField = viewModel.extractSortOption(sortOption).first,
                        ascending = viewModel.extractSortOption(sortOption).second
                    )
                    openSort = false
                },
                onReset = {
                    openSort = false
                }
            )
        }
    }
}

@Composable
fun CategoryDialog(
    title: String,
    show: Boolean,
    modifier: Modifier = Modifier
) {

    if (show) {
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

        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LadosTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    style = LadosTheme.typography.titleLarge.copy(
                        color = LadosTheme.colorScheme.onBackground
                    )
                )

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LadosTheme.colorScheme.background),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {

                        }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(
                            containerColor = LadosTheme.colorScheme.primary,
                            contentColor = LadosTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Confirm",
                            style = LadosTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Button(onClick = {

                    }) {
                        Text(text = "Cancel")
                    }
                }

            }
        }
    }
}


@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {}
) {

    val focusManager = LocalFocusManager.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                style = LadosTheme.typography.bodySmall.copy(
                    color = Color.Gray
                )
            )
        },
        leadingIcon = {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Search"
                    )
                }
            }
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = LadosTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(20.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
            unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        ),
        shape = RoundedCornerShape(16.dp),
        textStyle = LadosTheme.typography.bodySmall.copy(
            color = LadosTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun ManageSection(
    onAddNewCategory: () -> Unit,
    onDeleteAllSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAddNewCategory,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = "+ New",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(5.dp)
            )
        }

        Button(
            onClick = onDeleteAllSelected,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(
                text = "Delete All",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onEdit: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = LadosTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(category.categoryImage)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentDescription = category.categoryName,
            )

            Text(
                text = category.categoryName,
                style = LadosTheme.typography.titleSmall.copy(
                    color = LadosTheme.colorScheme.onBackground
                )
            )

            Box {  // Wrap the IconButton and DropdownMenu in a Box
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Edit Button",
                        tint = LadosTheme.colorScheme.onBackground
                    )
                }

                // Use Box with modifier to control the dropdown menu's position
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Position it below the icon
                    // Optional: Adjust vertical position if needed
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            onEdit(category.categoryId)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDelete(category.categoryId)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun SortDialog(
    modifier: Modifier = Modifier,
    isOpen: Boolean = true,
    onDismiss: () -> Unit = {},
    onSortOptionSelected: (String) -> Unit = {},
    onReset: () -> Unit = {},
    currentOption: String = "All",
    onApply: () -> Unit = {}
) {
    var isReset by remember { mutableStateOf(false) }

    if (isOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LadosTheme.colorScheme.background
                ),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()

                ) {
                    DropdownWithTitle(
                        "Sort by",
                        categoriesSortOption,
                        isReset = isReset,
                        currentOption = currentOption,
                        onOptionSelected = {
                            onSortOptionSelected(it)
                            isReset = false
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LadosTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = LadosTheme.colorScheme.onError
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = {
                            isReset = true
                            onSortOptionSelected("All")
                            //onReset()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LadosTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LadosTheme.colorScheme.primary),

                        ) {
                        Text(
                            text = "Reset",
                            color = LadosTheme.colorScheme.primary
                        )

                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = { onApply() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LadosTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Apply",
                            color = LadosTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericDropdown(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = LadosTheme.colorScheme.outline
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption.toString(),
                    style = LadosTheme.typography.bodyLarge,
                    color = LadosTheme.colorScheme.onSurface
                )

                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (expanded) 180f else 0f),
                    tint = LadosTheme.colorScheme.onSurface
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(LadosTheme.colorScheme.background)
                .height(200.dp)
                .verticalScroll(scrollState),
            shadowElevation = MenuDefaults.ShadowElevation,
            shape = RoundedCornerShape(12.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.toString(),
                            style = LadosTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = LadosTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun DropdownWithTitle(
    title: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    currentOption: String,
    isReset: Boolean = false,
    modifier: Modifier = Modifier
) {

    var selectedOption by remember { mutableStateOf(options[0]) }

    if (currentOption.isNotEmpty()) {
        selectedOption = currentOption
    }

    if (isReset) selectedOption = options[0]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 16.dp),
            color = LadosTheme.colorScheme.onBackground
        )

        GenericDropdown(
            label = "Rating",
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = {
                selectedOption = it
                onOptionSelected(selectedOption)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun OptionButton(
    item: FilterItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LadosTheme.colorScheme.primary
        ),
        modifier = modifier
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = LadosTheme.typography.bodySmall.copy(
                    color = LadosTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            androidx.compose.material3.Icon(
                painter = painterResource(R.drawable.arrowright2),
                contentDescription = "Dropdown Icon",
                modifier = Modifier.rotate(90f),
                tint = LadosTheme.colorScheme.onBackground
            )

        }
    }

}


@Composable
fun CategoryImageSection(
    modifier: Modifier = Modifier,
    imageUri: String,
    onEditImage: () -> Unit = {},
) {

    Box(
        modifier = modifier
            .wrapContentSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {

        SubcomposeAsyncImage(
            modifier = Modifier
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit,
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
            contentDescription = "Variant Image"
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
            onClick = onEditImage
        ) {
            androidx.compose.material3.Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Filled.Edit,
                contentDescription = null
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text("Confirm", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onCancel() }
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}
