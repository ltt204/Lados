package org.nullgroup.lados.screens.customer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.nullgroup.lados.ui.theme.BlackMaterial
import org.nullgroup.lados.ui.theme.BrownMaterial
import org.nullgroup.lados.ui.theme.GrayMaterial
import org.nullgroup.lados.ui.theme.WhiteMaterial
import org.nullgroup.lados.ui.theme.YellowMaterial
import org.nullgroup.lados.viewmodels.HomeViewModel

@Composable
fun Header(modifier: Modifier = Modifier, content: String) {
    Text(
        text = content,
        style = TextStyle(fontSize = 20.sp, color = BlackMaterial, fontWeight = FontWeight.SemiBold),
        modifier = modifier
    )
}

@Composable
fun FilterItem(modifier: Modifier = Modifier, content: String) {
    var isChoose by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isChoose) BrownMaterial else GrayMaterial.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { isChoose = !isChoose }
    ) {
        Text(
            text = content,
            style = TextStyle(
                fontSize = 16.sp,
                color = if (isChoose) WhiteMaterial else BlackMaterial
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
    steps: Int = 0
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
                thumbColor = BrownMaterial,
                activeTrackColor = BrownMaterial,
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
fun PricingRange(modifier: Modifier=Modifier, start: Float, end: Float){
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
                    text = if (step == steps.last()) "${step.toInt()}+" else step.toInt().toString(),
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun FilterCateRow(modifier: Modifier=Modifier, content: List<String>) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        //horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(4.dp),
    ) {
        items(content) { item ->
            FilterItem(content = item)
        }
    }
}

@Composable
fun ReviewRow(modifier: Modifier=Modifier, content: String) {
    Row(
        modifier = modifier,
    ) {
        for (i in 1..5) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = YellowMaterial
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
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(modifier = modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                ReviewRow(modifier, content=option)
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
fun RadioButtonGroupDraw(modifier: Modifier=Modifier, content: List<String>) {
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
    onApplyChangesClick: () -> Unit
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
                .height(48.dp)
            ,
            colors = ButtonDefaults.buttonColors(
                BrownMaterial
            )
        ) {
            Text("Reset Filter", color = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onApplyChangesClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ,
            colors = ButtonDefaults.buttonColors(
                BrownMaterial
            )
        ) {
            Text("Apply Changes", color = Color.White)
        }
    }
}


@Composable
fun FilterScreenDraw(modifier: Modifier=Modifier, navController: NavController, paddingValues: PaddingValues) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val categories = homeViewModel.categories.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp),

        ) {
        item {
            Header(content = "Brands")
        }

        item {
            FilterCateRow(content = categories.value.map { it.categoryName })
        }
        item {
            Header(content = "Gender")
        }

        item {
            FilterCateRow(content = listOf("All", "Men", "Women"))
        }
        item {
            Header(content = "Sort by")
        }

        item {
            FilterCateRow(content = listOf("Most Recent", "Popular", "Price High"))
        }

        item {
            Header(content = "Pricing Range")
        }
        item {
            CustomSlider()
        }
        item {
            Header(content = "Reviews")
        }
        item {
            RadioButtonGroupDraw(
                content = listOf(
                    "4.5 and above",
                    "4.0 to 4.5",
                    "3.5 to 4.0",
                    "3.0 to 3.5",
                    "2.5 to 3.0",
                    "2.0 to 2.5",
                    "1.5 to 2.0",
                    "1.0 to 1.5"
                )
            )
        }
        item{
            FilterScreenBottom({},{})
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    LadosTheme {
//        FilterScreenDraw()
//    }
//}