package org.nullgroup.lados.screens.admin.userManagement

import android.widget.RemoteViews.RemoteView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.Primary
import org.nullgroup.lados.viewmodels.admin.UserManagementViewModel
import org.nullgroup.lados.viewmodels.admin.UsersUiState
import org.nullgroup.lados.viewmodels.customer.home.ProductUiState

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {

    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier.fillMaxWidth(0.6f),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            singleLine = true,
            placeholder = {
                Text(
                    "Search",
                    style = LadosTheme.typography.bodyMedium.copy(
                        color = LadosTheme.colorScheme.onBackground
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(24.dp),
                    tint = LadosTheme.colorScheme.onBackground,
                )
            },
            shape = RoundedCornerShape(10),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
                focusedBorderColor = LadosTheme.colorScheme.primary,
                unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                unfocusedBorderColor = LadosTheme.colorScheme.onBackground.copy(alpha=0.3f),
                errorBorderColor = LadosTheme.colorScheme.error,
                focusedTextColor = LadosTheme.colorScheme.onBackground,
                unfocusedTextColor = LadosTheme.colorScheme.onBackground,
                errorTextColor = LadosTheme.colorScheme.error,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch(searchText)
                focusManager.clearFocus()
            })
        )
    }
}


@Composable
fun UserManagementScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val usersUiState = viewModel.usersUIState.collectAsStateWithLifecycle()
    when (usersUiState.value) {
        is UsersUiState.Loading -> {
            LoadOnProgress(
                modifier = modifier,
                content = { CircularProgressIndicator() }
            )
        }

        is UsersUiState.Error -> {
            Text(
                text = "Failed to load data",
                style = LadosTheme.typography.headlineSmall.copy(
                    color = LadosTheme.colorScheme.error,
                )
            )
        }

        is UsersUiState.Success -> {
            val users = (usersUiState.value as UsersUiState.Success).users
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Manage your team members and their account permissions here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        onSearch = { /* Handle Search */ }

                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { /* Handle Filters */ },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LadosTheme.colorScheme.primary,
                            contentColor = LadosTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            // filter icon
                            Icons.Default.FilterList,
                            contentDescription = null,
                            tint = LadosTheme.colorScheme.onPrimary
                        )
                        Text("Filters")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().background(color = LadosTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = "All users: ",
                            style = LadosTheme.typography.titleMedium,
                            color = LadosTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = users.size.toString(),
                            style = LadosTheme.typography.titleMedium,
                            color = LadosTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // User List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(users) { user ->
                        UserRow(user)
                        Divider()
                    }
                }

                /*
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Page 1 of 6",
                        style = MaterialTheme.typography.bodyLarge,
                        color = LadosTheme.colorScheme.onBackground,
                    )
                    Button(
                        onClick = {}
                    ) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LadosTheme.colorScheme.onPrimary,
                        )
                    }
                }

                 */
            }
        }
    }
}

@Composable
fun MoreVertIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More Options")
    }
}

@Composable
fun FlyoutMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    menuItems: List<String>,
    onMenuItemClick: (String) -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        menuItems.forEach { item ->
            DropdownMenuItem(
                modifier = Modifier.background(
                    color=LadosTheme.colorScheme.primaryContainer,
                    shape=RoundedCornerShape(8.dp)
                ).padding(8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = when (item) {
                            "View profile" -> Icons.Outlined.Person
                            "Edit details" -> Icons.Outlined.Edit
                            else -> Icons.Outlined.Delete
                        },
                        contentDescription = null
                    )
                },
                text = { Text(text = item) },
                onClick = {
                    onMenuItemClick(item)
                    onDismiss()
                },
            )
        }
    }
}


@Composable
fun BottomSheetContent(

) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            // note: modify
            .background(LadosTheme.colorScheme.background)
            .clip(RoundedCornerShape(16.dp))
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
            )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {},
                    content = {
                        Text(
                            "Clear"
                        )
                    }
                )

                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                IconButton(
                    onClick = {},
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null
                        )
                    }
                )
            }
            Text(
                "Role"
            )
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(
                    onClick={},
                    content={
                        Text(
                            "Admin",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                Button(
                    onClick={},
                    content={
                        Text(
                            "Staff",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                Button(
                    onClick={},
                    content={
                        Text(
                            "Customer",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

            }

            Text(
                "Status"
            )
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {},
                    content = {
                        Text(
                            "Active",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                Button(
                    onClick = {},
                    content = {
                        Text(
                            "Unactive",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            Text(
                "Username"
            )
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {},
                    content = {
                        Text(
                            "User Name to A - Z",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                Button(
                    onClick = {},
                    content = {
                        Text(
                            "User Name to Z - A",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            Text(
                "Email"
            )
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {},
                    content = {
                        Text(
                            "Email to A - Z",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                Button(
                    onClick = {},
                    content = {
                        Text(
                            "Email to Z - A",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }


        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomSheetPreview() {
    BottomSheetContent()
}


@Composable
fun MoreVertMenu(
menuItems: List<String>,
onMenuItemClick: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    MoreVertIconButton(onClick = { expanded = true })

    FlyoutMenu(
        expanded = expanded,
        onDismiss = { expanded = false },
        menuItems = menuItems,
        onMenuItemClick = onMenuItemClick
    )
}

@Composable
fun UserRow(user: org.nullgroup.lados.data.models.User) {
    val menuItems = listOf("View profile", "Edit details", "Delete user")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column (
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        Spacer(Modifier.width(2.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(78.dp)
        ) {
            Text(
                text = user.role,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .background(Color.LightGray, shape = RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        Spacer(Modifier.weight(1f))
        MoreVertMenu(
            menuItems = menuItems,
            onMenuItemClick = { item ->
                // Handle menu item click here
                println("Clicked on $item")
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    //sharedViewModel: SharedViewModel = SharedViewModel(),
    //viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .padding(horizontal = 16.dp),
        containerColor = LadosTheme.colorScheme.background,

    ) { it ->
        UserManagementScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
        )
    }
}
