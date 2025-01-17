package org.nullgroup.lados.screens.admin.userManagement

import android.content.Context
import android.util.Log
import android.widget.RemoteViews.RemoteView
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.home.RadioButtonGroup
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.Primary
import org.nullgroup.lados.viewmodels.SharedViewModel
import org.nullgroup.lados.viewmodels.admin.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    options: List<String>,
    defaultIndex: Int = 0,
    onOptionSelected: (String) -> Unit,
    enable: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.getOrElse(defaultIndex) { "" }) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor().padding(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    enabled = enable,
                    text = { Text(text = option) },
                    onClick = {
                        selectedOption = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun UserDetailScreen(
    modifier: Modifier,
    paddingValues: PaddingValues,
    navController: NavController,
    userManagementViewModel: UserManagementViewModel= hiltViewModel(),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    context: Context
){
    val selectedUser = sharedViewModel.selectedUser
    val searchType = sharedViewModel.searchQuery
    val options = listOf("Active", "Inactive")
    var selectedOption by remember { mutableStateOf(
        when (selectedUser!!.isActive) {
            true -> options[0]
            else -> options[1]
        }
    ) }
    val radioOptions = listOf("Admin", "Staff", "Customer")
    var radioSelectedOption by remember { mutableStateOf(
        when (selectedUser!!.role) {
            "ADMIN" -> radioOptions[0]
            "STAFF" -> radioOptions[1]
            else -> radioOptions[2]
        })}

    Log.d("UserDetailScreen", "UserDetailScreen")

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingValues.calculateTopPadding())
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box (

            ){
                IconButton(
                    onClick = {
                        navController.navigate(Screen.Admin.UserManagement.route)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = "User details",
                fontSize = 24.sp,
                color = LadosTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.weight(0.5f))
        }
        Text(
            text = "View and update user profile and photo here."
        )
        Divider()
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = selectedUser!!.avatarUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(0.5f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Name",
                    fontWeight = FontWeight.Bold
                )
                BasicTextField(
                    value = selectedUser!!.name,
                    readOnly = true,
                    onValueChange = {
                        //newUser=selectedUser.copy(name=it)
                    },
                    textStyle = TextStyle(fontSize = 18.sp),
                )
            }
            Column(
                modifier = Modifier.weight(0.5f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Email",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text=selectedUser!!.email,
                    fontSize = 18.sp
                )
            }
        }
        Row {
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    "Phone number",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                BasicTextField(
                    value = selectedUser!!.phoneNumber,
                    readOnly = true,
                    onValueChange = {
                        //newUser=selectedUser.copy(phoneNumber=it)
                    },
                    textStyle = TextStyle(fontSize = 18.sp),
                )
            }

            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    "Status",
                    fontWeight = FontWeight.Bold
                )
                ComboBox(
                    options = options,
                    defaultIndex = if (selectedOption=="Active") 0 else 1,
                    onOptionSelected = { option ->
                        selectedOption = option
                    },
                    enable = searchType!="View profile"
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Role",
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    radioOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            RadioButton(
                                selected = ( radioSelectedOption == option ),
                                enabled = searchType=="Edit details",
                                onClick = { radioSelectedOption = option }
                            )
                            Text(
                                text = option,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (searchType=="View profile") {
                        sharedViewModel.searchQuery = "Edit details"
                        navController.navigate(Screen.Admin.UserDetailScreen.route)
                    } else {
                        sharedViewModel.searchQuery = "View profile"
                        navController.navigate(Screen.Admin.UserDetailScreen.route)
                    }
                },
                content = {
                    Text(
                        text= if (searchType=="View profile") "Edit details" else "Cancel",
                        fontWeight = FontWeight.Bold
                    )
                },
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    Log.d("UserManagementViewModel", "updateUserByEmail: $selectedOption")


                    if (userManagementViewModel.updateUserByEmail(selectedUser!!.id, radioSelectedOption.uppercase(), selectedOption=="Active")) {
                        selectedUser.role = radioSelectedOption.uppercase()
                        selectedUser.isActive = selectedOption=="Active"
                        sharedViewModel.updateSelectedUser(selectedUser)
                        Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Admin.UserManagement.route)
                    }
                    else{
                        Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
                    }
                },
                content = {
                    Text(
                        "Save changes",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}
