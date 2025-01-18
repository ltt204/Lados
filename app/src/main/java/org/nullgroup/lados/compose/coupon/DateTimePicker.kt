package org.nullgroup.lados.compose.coupon

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import org.nullgroup.lados.compose.cart.DialogInfo
import org.nullgroup.lados.data.models.currentHostTimeZoneInString
import org.nullgroup.lados.data.models.timestampFromNow
import org.nullgroup.lados.data.models.toLocalDateTime
import org.nullgroup.lados.data.models.toTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    val zoneId = currentHostTimeZoneInString()

    val dialogInfoState = remember { mutableStateOf<DialogInfo?>(null) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDateTime.of(currentDate, LocalTime.MIDNIGHT).toTimestamp(zoneId).seconds * 1000
    )

    var showDatePickerDialog = {currentDate: LocalDate ->
        dialogInfoState.value = DialogInfo(
            message = @Composable {
                DatePicker(
                    state = datePickerState
                )
            },
            onConfirm = {
                val selectedDate = datePickerState.selectedDateMillis?.let {
                    timestampFromNow(it / 1000).toLocalDateTime(zoneId).toLocalDate()
                }
                selectedDate?.let { onDateSelected(it) }
                dialogInfoState.value = null
            },
            onCancel = {
                dialogInfoState.value = null
            }
        )
    }

    OutlinedTextField(
        value = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        onValueChange = { },
        modifier = modifier
            .pointerInput(currentDate) {
                awaitEachGesture {
                    // https://developer.android.com/develop/ui/compose/components/datepickers

                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showDatePickerDialog(currentDate)
                    }
                }
            },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = null
            )
        },
        label = label,
        isError = isError,
        shape = shape,
        colors = colors,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerTextField(
    currentTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    val dialogInfoState = remember { mutableStateOf<DialogInfo?>(null) }
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true,
    )

    var showTimePickerDialog = {currentTime: LocalTime ->
        dialogInfoState.value = DialogInfo(
            message = @Composable {
                TimePicker(
                    state = timePickerState
                )
            },
            onConfirm = {
                val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                onTimeSelected(selectedTime)
                dialogInfoState.value = null
            },
            onCancel = {
                dialogInfoState.value = null
            }
        )
    }

    OutlinedTextField(
        value = currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = { },
        modifier = modifier
            .pointerInput(currentTime) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null && enabled) {
                        showTimePickerDialog(currentTime)
                    }
                }
            },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = null
            )
        },
        label = label,
        isError = isError,
        shape = shape,
        colors = colors,
    )
}