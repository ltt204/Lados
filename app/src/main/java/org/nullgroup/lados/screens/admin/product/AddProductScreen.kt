package org.nullgroup.lados.screens.admin.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.ui.theme.LadosTheme


@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

    var scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add product",
                        style = LadosTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = LadosTheme.colorScheme.background,
                contentColor = LadosTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
        }

    ) { it ->

        var name by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LadosTheme.colorScheme.background)
                .padding(it)
                .padding(horizontal = 10.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            Text(
                text = "Name",
                style = LadosTheme.typography.titleMedium,
                color = LadosTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                label = "Vietnamese Name",
                text = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            CustomTextField(
                label = "English Name",
                text = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "Description",
                style = LadosTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LadosTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                label = "Vietnamese Description",
                text = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            CustomTextField(
                label = "English Description",
                text = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            Text(
                text = "Variants",
                style = LadosTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LadosTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            AddVariantDialog()


        }
    }
}


@Composable
fun VariantsSection(modifier: Modifier = Modifier) {
    LazyColumn {

    }
}

@Preview
@Composable
fun AddVariantDialog() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LadosTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "Color",
            style = LadosTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = LadosTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuWithTextField(
            options = listOf("Red", "Green", "Blue"),
            lable = "Choose color",
            onOptionSelected = {}
        )

        Text(
            text = "Size",
            style = LadosTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = LadosTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuWithTextField(
            options = listOf("S", "M", "L", "XL", "XXL"),
            lable = "Choose size",
            onOptionSelected = {}
        )

        Text(
            text = "Quantity",
            style = LadosTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = LadosTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            label = "Quantity",
            text = "",
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = false
        )

        Text(
            text = "Price",
            style = LadosTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = LadosTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            CustomTextField(
                label = "Enter price",
                text = "",
                onValueChange = {},
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            DropdownMenuWithButton(
                options = listOf("VND", "USD"),
                label = "",
                onOptionSelected = {}
            )

        }

        CustomTextField(
            label = "English Description",
            text = "",
            onValueChange = {

            },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {

            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            isError = false
        )


    }


}


@Composable
fun DropdownMenuWithTextField(
    options: List<String>,
    lable: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    Column(modifier = Modifier.wrapContentSize()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {}, // Không thay đổi giá trị khi nhập
            label = {
                Text(
                    lable,
                    color = LadosTheme.colorScheme.primary,
                    modifier = Modifier.background(Color.Transparent)
                )
            },
            readOnly = true, // Chỉ cho phép chọn từ menu
            modifier = Modifier
                .clickable { expanded = true }, // Mở menu khi nhấn
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Mở menu",
                    modifier = Modifier.clickable {
                        expanded = true
                    }
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
                focusedBorderColor = LadosTheme.colorScheme.primary,
                unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = LadosTheme.colorScheme.error,
                focusedTextColor = LadosTheme.colorScheme.onBackground,
                unfocusedTextColor = LadosTheme.colorScheme.onBackground,
                errorTextColor = LadosTheme.colorScheme.error,
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onOptionSelected(selectedOption)
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuWithButton(
    options: List<String>,
    label: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    Column(modifier = Modifier.wrapContentSize()) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                contentColor = LadosTheme.colorScheme.onBackground
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedOption,
                    color = LadosTheme.colorScheme.onBackground,
                    modifier = Modifier
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Mở menu",
                    tint = LadosTheme.colorScheme.primary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onOptionSelected(selectedOption)
                    }
                )
            }
        }
    }
}


