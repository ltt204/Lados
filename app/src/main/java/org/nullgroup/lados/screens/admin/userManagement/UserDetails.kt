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
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.Primary

@Composable
fun UserDetailScreen(
    modifier: Modifier,
    paddingValues: PaddingValues,
    navController: NavController
){
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User details",
            fontSize = 24.sp,
            color = LadosTheme.colorScheme.onBackground,
        )
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

        }

        Button(
            onClick = {},
            modifier = Modifier.wrapContentWidth(),
            content = {
                Text(
                text = "Change photo",
                fontWeight = FontWeight.Bold)
            }

        )

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
                    value = "",
                    onValueChange = {},
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
                BasicTextField(
                    value = "",
                    onValueChange = {},
                    textStyle = TextStyle(fontSize = 18.sp),
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
                BasicTextField(
                    value = "",
                    onValueChange = {},
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
                BasicTextField(
                    value = "",
                    onValueChange = {},
                    textStyle = TextStyle(fontSize = 18.sp),
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
                    modifier = Modifier.height(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = false,
                        modifier = Modifier.wrapContentSize(),
                        onCheckedChange = {}
                    )
                    Text(
                        text = "Admin",
                        fontWeight = FontWeight.Bold
                    )
                    Checkbox(
                        checked = false,
                        modifier = Modifier.wrapContentSize(),
                        onCheckedChange = {}
                    )
                    Text(
                        text = "Staff",
                        fontWeight = FontWeight.Bold
                    )
                    Checkbox(
                        checked = false,
                        modifier = Modifier.wrapContentSize(),
                        onCheckedChange = {}
                    )
                    Text(
                        text = "User",
                        fontWeight = FontWeight.Bold
                    )

                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {},
                content = {
                    Text(
                        "Edit details",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {},
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserDetailScreenPreview(){
    UserDetailScreen(navController = NavController(LocalContext.current), modifier = Modifier, paddingValues = PaddingValues(0.dp))
}