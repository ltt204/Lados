package org.nullgroup.lados.compose.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.ui.theme.LadosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteSearchBar(
    modifier: Modifier = Modifier,
    options: List<String> = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5"),
    onOptionSelected: (String?, Int?) -> Unit = { value, index -> },
    label: String = "Label",
) {
    var exp by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(expanded = exp, onExpandedChange = { exp = !exp }) {
        org.nullgroup.lados.compose.signin.CustomTextField(
            modifier = modifier.height(48.dp),
            shape = RoundedCornerShape(24.dp),
            text = selectedOption,
            leadingIcon = {
                if (selectedOption.isNotEmpty()) {
                    IconButton(onClick = {
                        selectedOption = ""
                        onOptionSelected(null, null)
                    }) {
                        Icon(Icons.Filled.SearchOff, contentDescription = "Dropdown")
                    }
                } else {
                    Icon(Icons.Filled.Search, contentDescription = "Dropdown")
                }
            },
            onValueChange = {
                selectedOption = it
                exp = true
            },
            label = label,
            singleLine = true,
        )
        val filterOpts = options.filter { it.contains(selectedOption, ignoreCase = true) }
        if (filterOpts.isNotEmpty()) {
            ExposedDropdownMenu(expanded = exp, onDismissRequest = { exp = false }) {
                filterOpts.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption = option
                            onOptionSelected(option, options.indexOf(option))
                            exp = false
                        },
                        text = { Text(text = option) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AutoCompleteSearchBarPreview() {
    val sampleData = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    LadosTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AutoCompleteSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                options = sampleData
            )
        }
    }
}