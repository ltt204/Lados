package org.nullgroup.lados.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nullgroup.lados.compose.signin.ButtonSubmit
import org.nullgroup.lados.compose.signin.Headline
import org.nullgroup.lados.compose.signin.TextNormal
import org.nullgroup.lados.ui.theme.LadosTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSetUpScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var isSelectMan by remember { mutableStateOf(false) }
    var isSelectWoman by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selectedAge by remember { mutableStateOf("") }
    val ageOptions = (18..60).map {
        it.toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Headline(text = "Tell us About yourself")

        Spacer(modifier = Modifier.height(16.dp))

        TextNormal(text = "Who do you shop for?")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ButtonSubmit(
                text = "Men",
                onClick = {
                    if (isSelectWoman) {
                        isSelectWoman = false
                    }
                    isSelectMan = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                colors = if (isSelectMan) ButtonDefaults.buttonColors(
                    containerColor = LadosTheme.colorScheme.primary,
                    contentColor = LadosTheme.colorScheme.onPrimary,
                ) else ButtonDefaults.buttonColors(
                    containerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                    contentColor = LadosTheme.colorScheme.onSurface,
                ),
            )

            ButtonSubmit(
                text = "Women",
                onClick = {
                    if (isSelectMan) {
                        isSelectMan = false
                    }
                    isSelectWoman = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                colors = if (isSelectWoman) ButtonDefaults.buttonColors(
                    containerColor = LadosTheme.colorScheme.primary,
                    contentColor = LadosTheme.colorScheme.onPrimary,
                ) else ButtonDefaults.buttonColors(
                    containerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                    contentColor = LadosTheme.colorScheme.onSurface,
                ),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextNormal(text = "How old are you?")

        Column(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                }
            ) {
                TextField(
                    value = selectedAge,
                    onValueChange = {
                        selectedAge = it
                    },
                    label = {
                        Text(text = "Select Age")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                        )
                    },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ageOptions.forEach { age ->
                        DropdownMenuItem(
                            text = { Text(text = age) },
                            onClick = {
                                selectedAge = age
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ButtonSubmit(
            text = "Finish",
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = { }
        )
    }
}
