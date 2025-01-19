package org.nullgroup.lados.screens.admin.userManagement

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
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
        CustomTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(0.dp),
            text = selectedOption,
            onValueChange = {
                //newUser=selectedUser.copy(phoneNumber=it)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isReadonly = true,
            textStyle = TextStyle(fontSize = 18.sp),
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
    userManagementViewModel: UserManagementViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    context: Context
) {
    val selectedUser = sharedViewModel.selectedUser
    val searchType = sharedViewModel.searchQuery
    val options = listOf("Active", "Inactive")
    var selectedOption by remember {
        mutableStateOf(
            when (selectedUser!!.isActive) {
                true -> options[0]
                else -> options[1]
            }
        )
    }
    val radioOptions = listOf("Admin", "Staff", "Customer")
    var radioSelectedOption by remember {
        mutableStateOf(
            when (selectedUser!!.role) {
                "ADMIN" -> radioOptions[0]
                "STAFF" -> radioOptions[1]
                else -> radioOptions[2]
            }
        )
    }

    Log.d("UserDetailScreen", "UserDetailScreen")

    Column(
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
            Box(

            ) {
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                AsyncImage(
                    model = selectedUser!!.avatarUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = selectedUser!!.email,style = LadosTheme.typography.bodyMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Name", style = LadosTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = selectedUser!!.name,
                    onValueChange = {
                        //newUser=selectedUser.copy(name=it)
                    },
                    isReadonly = true,
                    textStyle = TextStyle(fontSize = 18.sp),
                )
            }

        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    "Phone number", style = LadosTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = selectedUser!!.phoneNumber,
                    onValueChange = {
                        //newUser=selectedUser.copy(phoneNumber=it)
                    },
                    isReadonly = true,
                    textStyle = TextStyle(fontSize = 18.sp),
                )
            }

            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    "Status", style = LadosTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                ComboBox(
                    options = options,
                    defaultIndex = if (selectedOption == "Active") 0 else 1,
                    onOptionSelected = { option ->
                        selectedOption = option
                    },
                    enable = searchType != "View profile"
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Role:",
                    style = LadosTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                radioOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (radioSelectedOption == option),
                            enabled = searchType == "Edit details",
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
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (searchType == "View profile") {
                        sharedViewModel.searchQuery = "Edit details"
                        navController.navigate(Screen.Admin.UserDetailScreen.route)
                    } else {
                        sharedViewModel.searchQuery = "View profile"
                        navController.navigate(Screen.Admin.UserDetailScreen.route)
                    }
                },
                content = {
                    Text(
                        text = if (searchType == "View profile") "Edit details" else "Cancel",
                        fontWeight = FontWeight.Bold
                    )
                },
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    Log.d("UserManagementViewModel", "updateUserByEmail: $selectedOption")


                    if (userManagementViewModel.updateUserByEmail(
                            selectedUser!!.id,
                            radioSelectedOption.uppercase(),
                            selectedOption == "Active"
                        )
                    ) {
                        selectedUser.role = radioSelectedOption.uppercase()
                        selectedUser.isActive = selectedOption == "Active"
                        sharedViewModel.updateSelectedUser(selectedUser)
                        Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate(Screen.Admin.UserManagement.route)
                    } else {
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
