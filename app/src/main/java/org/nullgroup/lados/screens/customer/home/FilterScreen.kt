package org.nullgroup.lados.screens.customer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.nullgroup.lados.screens.customer.product.DrawProductInCategoryScreenContent
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.SharedViewModel

@Composable
fun Header(modifier: Modifier = Modifier, content: String) {
    Text(
        text = content,
        // note: modify color
        style = TextStyle(
            fontSize = 20.sp,
            color = LadosTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = modifier
    )
}

@Composable
fun FilterItem(modifier: Modifier = Modifier, content: String) {
    var isChoose by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(CircleShape)
            // note: modify background
            .background(
                if (isChoose) LadosTheme.colorScheme.onSurface else LadosTheme.colorScheme.secondary.copy(
                    alpha = 0.2f
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { isChoose = !isChoose }
    ) {
        Text(
            text = content,
            style = TextStyle(
                fontSize = 16.sp,
                // note: modify color
                color = if (isChoose) LadosTheme.colorScheme.onBackground else LadosTheme.colorScheme.background
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSliderWithLabels(
    range: MutableState<ClosedFloatingPointRange<Float>>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        RangeSlider(
            value = range.value,
            onValueChange = { newRange ->
                range.value = newRange
                onValueChange(newRange)
            },

            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                // note: modify
                thumbColor = LadosTheme.colorScheme.surfaceContainer,
                // note: modify
                activeTrackColor = LadosTheme.colorScheme.onPrimary,
                inactiveTrackColor = Color.LightGray
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )

        val startLabel = valueRange.start.toInt().toString()
        val endLabel = valueRange.endInclusive.toInt().toString()

        Text(
            text = startLabel,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 8.dp)
        )

        Text(
            text = endLabel,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 8.dp)
        )
    }
}

@Composable
fun PricingRange(modifier: Modifier = Modifier, start: Float, end: Float) {
    val selectedRange = remember { mutableStateOf(start..end) }
    RangeSliderWithLabels(
        range = selectedRange,
        onValueChange = { newRange ->
            println("Selected range: $newRange")
        }

    )

}

@Composable
fun CustomSlider() {
    var sliderValue by remember { mutableStateOf(2f) }
    val steps = listOf(2f, 7f, 22f, 50f, 100f, 150f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Custom Slider
        Slider(
            value = sliderValue,
            onValueChange = { value ->
                sliderValue = value
            },
            valueRange = steps.first()..steps.last(),
            steps = steps.size - 2,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF6B4F29),
                activeTrackColor = Color(0xFF6B4F29),
                inactiveTrackColor = Color(0xFFDDC9B0)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEach { step ->
                BasicText(
                    text = if (step == steps.last()) "${step.toInt()}+" else step.toInt()
                        .toString(),
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun ReviewRow(modifier: Modifier = Modifier, content: String) {
    Row(
        modifier = modifier,
    ) {
        for (i in 1..5) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                // note: modify color
                tint = LadosTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = content)
    }
}

@Composable
fun RadioButtonGroup(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedOption: MutableState<String>,
    onOptionSelected: (String) -> Unit,
) {
    Column {
        options.forEach { option ->
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                ReviewRow(modifier, content = option)
                RadioButton(
                    selected = selectedOption.value == option,
                    onClick = {
                        selectedOption.value = option
                        onOptionSelected(option)
                    },
                )
            }
        }
    }
}

@Composable
fun RadioButtonGroupDraw(modifier: Modifier = Modifier, content: List<String>) {
    val selectedOption = remember { mutableStateOf(content[0]) }

    RadioButtonGroup(
        options = content,
        selectedOption = selectedOption,
        onOptionSelected = { newOption ->
            // Handle the new selected option here
            println("Selected option: $newOption")
        }
    )
}

@Composable
fun FilterScreenBottom(
    onResetFilterClick: () -> Unit,
    onApplyChangesClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onResetFilterClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            // note: modify
            colors = ButtonDefaults.buttonColors(
                LadosTheme.colorScheme.error
            )
        ) {
            Text("Reset Filter", color = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onApplyChangesClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            // note: modify
            colors = ButtonDefaults.buttonColors(
                LadosTheme.colorScheme.error
            )
        ) {
            Text("Apply Changes", color = Color.White)
        }
    }
}


@Composable
fun FilterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    sharedViewModel: SharedViewModel = SharedViewModel(),
) {
    Scaffold(
        modifier = modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        containerColor = LadosTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        // note: modify
                        .background(LadosTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Search",
                        // note: modify
                        tint = LadosTheme.colorScheme.onBackground

                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    navController = navController,
                    onSearch = {})
            }
        }
    ) {
        DrawFilterScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
        )
    }
}


@Composable
fun DrawFilterScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = 8.dp,
                vertical = paddingValues.calculateTopPadding()
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DrawProductInCategoryScreenContent(
            navController = navController,
            textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            paddingValues = PaddingValues(horizontal = 8.dp),
            onButtonClick = {},
        )
        //FilterMenuSelection(title = "Test", options = listOf("Option 1", "Option 2", "Option 3"), onSelectionChanged = {})
    }
}

@Composable
fun FilterMenuSelection(
    modifier: Modifier = Modifier,
    title: String,
    options: List<String>, // Renamed 'content' to 'options' for clarity
    onSelectionChanged: (String) -> Unit, // Changed event to a callback with the selected item
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            // note: modify
            .background(LadosTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f))
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Text(text = "Clear")

                Text(text = title, style = MaterialTheme.typography.headlineMedium)
                Text(text = "    X")
            }
            Spacer(modifier = Modifier.height(8.dp))
            options.forEach { option ->
                Button(
                    onClick = {},
                    modifier = Modifier
                        // i want width the content button align left


                        .fillMaxWidth(0.95f)
                        .height(48.dp),
                    // note: modify
                    colors = ButtonDefaults.buttonColors(LadosTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            Icons.Outlined.Done,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    LadosTheme {
        FilterScreen(navController = NavController(LocalContext.current))
    }
}