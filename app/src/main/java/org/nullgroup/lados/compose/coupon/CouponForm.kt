package org.nullgroup.lados.compose.coupon

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import org.nullgroup.lados.R
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.coupon.CouponFormEvent
import org.nullgroup.lados.viewmodels.admin.coupon.CouponFormUiState
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun CouponForm(
    modifier: Modifier = Modifier,
    couponFormUiState: CouponFormUiState,
    handleEvent: (CouponFormEvent) -> Unit,
) {
    val code = couponFormUiState.code
    val discountPercentage = couponFormUiState.discountPercentage
    val maximumDiscount = couponFormUiState.maximumDiscount
    val minimumOrderAmount = couponFormUiState.minimumOrderAmount
    val startDate = couponFormUiState.startDate
    val endDate = couponFormUiState.endDate
    val usageDuration = couponFormUiState.usageDuration
    val maximumRedemption = couponFormUiState.maximumRedemption
    val autoFetching = couponFormUiState.autoFetching

    val outlineTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
        focusedBorderColor = LadosTheme.colorScheme.primary,
        unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHigh,
        unfocusedBorderColor = Color.Transparent,
        errorBorderColor = LadosTheme.colorScheme.error,
        focusedTextColor = LadosTheme.colorScheme.onBackground,
        unfocusedTextColor = LadosTheme.colorScheme.onBackground,
        errorTextColor = LadosTheme.colorScheme.error,
    )

    val scrollOffset = remember { mutableFloatStateOf(0f) }
    Column(
        modifier = modifier
            .background(LadosTheme.colorScheme.background)
            .scrollable(
                state = rememberScrollableState { delta ->
                    scrollOffset.value += delta
                    delta
                },
                orientation = Orientation.Vertical,
            ),
        verticalArrangement = Arrangement.spacedBy(LadosTheme.size.medium),
        horizontalAlignment = Alignment.Start,
    ) {
        // Coupon code
        OutlinedTextField(
            value = code.value,
            onValueChange = {
                handleEvent(CouponFormEvent.CodeChanged(it))
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Ascii,
            ),
            label = { Text(stringResource(R.string.coupon_form_code)) },
            isError = code.isError,
            shape = LadosTheme.shape.medium,
            colors = outlineTextFieldColors,
        )

        // Discount percentage
        OutlinedTextField(
            value = discountPercentage.value.toString(),
            onValueChange = {
                handleEvent(CouponFormEvent.DiscountPercentageChanged(it.toIntOrNull() ?: 0))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            label = { Text(stringResource(R.string.coupon_form_discount_percentage)) },
            isError = discountPercentage.isError,
            shape = LadosTheme.shape.medium,
            colors = outlineTextFieldColors,
        )

        // Maximum discount
        val isMaximumDiscountMarkedAsNull = maximumDiscount.markedAsNull
        NullableInputWrapper(
            isMarkedNull = isMaximumDiscountMarkedAsNull,
            onToggled = { handleEvent(CouponFormEvent.MaximumDiscountNullMarkChanged) },
        ) {
            OutlinedTextField(
                value = maximumDiscount.value?.toString() ?: "",
                onValueChange = {
                    handleEvent(CouponFormEvent.MaximumDiscountChanged(it.toDoubleOrNull() ?: 0.0))
                },
                enabled = !isMaximumDiscountMarkedAsNull,
                textStyle = LocalTextStyle.current.copy(
                    textDecoration = if (isMaximumDiscountMarkedAsNull) TextDecoration.LineThrough else TextDecoration.None,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                ),
                label = { Text(stringResource(R.string.coupon_form_maximum_discount)) },
                isError = maximumDiscount.isError,
                shape = LadosTheme.shape.medium,
                colors = outlineTextFieldColors,
            )
        }

        // Minimum order amount
        OutlinedTextField(
            value = minimumOrderAmount.value.toString(),
            onValueChange = {
                handleEvent(CouponFormEvent.MinimumOrderAmountChanged(it.toDoubleOrNull() ?: 0.0))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
            ),
            label = { Text(stringResource(R.string.coupon_form_minimum_order_amount)) },
            isError = minimumOrderAmount.isError,
            shape = LadosTheme.shape.medium,
            colors = outlineTextFieldColors,
        )

        // Start date
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val currentDate = startDate.value.toLocalDate()
            val currentTime = startDate.value.toLocalTime()
            DatePickerTextField(
                currentDate = currentDate,
                onDateSelected = {
                    handleEvent(
                        CouponFormEvent.StartDateChanged(
                            LocalDateTime.of(it, currentTime)
                        )
                    )
                },
                label = { Text(stringResource(R.string.coupon_form_start_date)) },
                isError = startDate.isError,
                shape = LadosTheme.shape.medium,
                colors = outlineTextFieldColors,
            )
            TimePickerTextField(
                currentTime = currentTime,
                onTimeSelected = {
                    handleEvent(
                        CouponFormEvent.StartDateChanged(
                            LocalDateTime.of(currentDate, it)
                        )
                    )
                },
                label = { Text(stringResource(R.string.coupon_form_start_time)) },
                isError = startDate.isError,
                shape = LadosTheme.shape.medium,
                colors = outlineTextFieldColors,
            )
        }

        // End date
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val currentDate = endDate.value.toLocalDate()
            val currentTime = endDate.value.toLocalTime()
            DatePickerTextField(
                currentDate = currentDate,
                onDateSelected = {
                    handleEvent(
                        CouponFormEvent.EndDateChanged(
                            LocalDateTime.of(it, currentTime)
                        )
                    )
                },
                label = { Text(stringResource(R.string.coupon_form_end_date)) },
                isError = endDate.isError,
                shape = LadosTheme.shape.medium,
                colors = outlineTextFieldColors,
            )
            TimePickerTextField(
                currentTime = currentTime,
                onTimeSelected = {
                    handleEvent(
                        CouponFormEvent.EndDateChanged(
                            LocalDateTime.of(currentDate, it)
                        )
                    )
                },
                label = { Text(stringResource(R.string.coupon_form_end_time)) },
                isError = endDate.isError,
                shape = LadosTheme.shape.medium,
                colors = outlineTextFieldColors,
            )
        }

        // Usage duration
        NullableInputWrapper(
            isMarkedNull = usageDuration.markedAsNull,
            onToggled = { handleEvent(CouponFormEvent.UsageDurationNullMarkChanged) },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val oneDayInSecond: Long = 60 * 60 * 24
                OutlinedTextField(
                    value = usageDuration.value?.toString() ?: "",
                    onValueChange = {
                        handleEvent(
                            CouponFormEvent.UsageDurationChanged(
                                (it.toLongOrNull() ?: 0) * oneDayInSecond
                            )
                        )
                    },
                    enabled = !usageDuration.markedAsNull,
                    textStyle = LocalTextStyle.current.copy(
                        textDecoration = if (usageDuration.markedAsNull) TextDecoration.LineThrough else TextDecoration.None,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    label = { Text(stringResource(R.string.coupon_form_usage_duration_day)) },
                    isError = usageDuration.isError,
                    shape = LadosTheme.shape.medium,
                    colors = outlineTextFieldColors,
                )

                val oneHourInSecond = 60 * 60
                var oneMinuteInSecond = 60
                val currentTime = LocalTime.of(
                    (usageDuration.value?.toInt() ?: 0) / oneHourInSecond,
                    ((usageDuration.value?.toInt() ?: 0) % oneHourInSecond) / oneMinuteInSecond
                )
                TimePickerTextField(
                    currentTime = currentTime,
                    onTimeSelected = {
                        handleEvent(
                            CouponFormEvent.UsageDurationChanged(
                                it.hour.toLong() * oneHourInSecond + it.minute.toLong() * oneMinuteInSecond
                            )
                        )
                    },
                    enabled = !usageDuration.markedAsNull,
                    label = { Text(stringResource(R.string.coupon_form_usage_duration_time)) },
                    isError = usageDuration.isError,
                    shape = LadosTheme.shape.medium,
                    colors = outlineTextFieldColors,
                )
            }
        }

        // Maximum redemption
        val isMaximumRedemptionMarkedAsNull = maximumRedemption.markedAsNull
        NullableInputWrapper(
            isMarkedNull = isMaximumRedemptionMarkedAsNull,
            onToggled = { handleEvent(CouponFormEvent.MaximumRedemptionNullMarkChanged) },
        ) {
            OutlinedTextField(
                value = maximumRedemption.value?.toString() ?: "",
                onValueChange = {
                    handleEvent(CouponFormEvent.MaximumRedemptionChanged(it.toIntOrNull() ?: 0))
                },
                enabled = !isMaximumRedemptionMarkedAsNull,
                textStyle = LocalTextStyle.current.copy(
                    textDecoration = if (isMaximumRedemptionMarkedAsNull) TextDecoration.LineThrough else TextDecoration.None,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                label = { Text(stringResource(R.string.coupon_form_maximum_redemption)) },
                isError = maximumRedemption.isError,
                shape = LadosTheme.shape.medium,
                colors = outlineTextFieldColors,
            )
        }

        // Auto fetching
        Row(
            horizontalArrangement = Arrangement.spacedBy(LadosTheme.size.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.coupon_form_auto_fetching),
                color = LadosTheme.colorScheme.onBackground,
            )

            Checkbox(
                checked = autoFetching.value,
                onCheckedChange = {
                    handleEvent(CouponFormEvent.AutoFetchingChanged(it))
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = LadosTheme.colorScheme.primary,
                    uncheckedColor = LadosTheme.colorScheme.onBackground,
                )
            )
        }
    }
}

@Composable
private fun NullableInputWrapper(
    isMarkedNull: Boolean,
    onToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    inputComponent: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        inputComponent()

        Row {
            Checkbox(
                checked = isMarkedNull,
                onCheckedChange = onToggled,
                colors = CheckboxDefaults.colors(
                    checkedColor = LadosTheme.colorScheme.primary,
                    uncheckedColor = LadosTheme.colorScheme.onBackground,
                )
            )
            Text(
                text = stringResource(R.string.coupon_form_null_checkbox),
                color = LadosTheme.colorScheme.onBackground,
            )
        }

    }
}

@Preview
@Composable
fun CouponFormPreview() {
    LadosTheme {
        CouponForm(
            couponFormUiState = CouponFormUiState(),
            handleEvent = {},
        )
    }
}