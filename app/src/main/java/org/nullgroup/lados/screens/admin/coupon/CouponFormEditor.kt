package org.nullgroup.lados.screens.admin.coupon

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.cart.ConfirmDialog
import org.nullgroup.lados.compose.cart.DialogInfo
import org.nullgroup.lados.compose.coupon.CouponForm
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.utilities.datetime.currentHostTimeZoneInString
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.coupon.CouponFormEvent
import org.nullgroup.lados.viewmodels.admin.coupon.CouponFormViewModel

// A very good guide: https://joebirch.co/android/building-an-authentication-form-using-jetpack-compose/

@Composable
fun CouponFormEditor(
    onSubmitted: (ServerCoupon) -> Unit = {},
    modifier: Modifier = Modifier,
    initialCoupon: ServerCoupon? = null,
) {
    val couponFormViewModel: CouponFormViewModel = hiltViewModel()
    val zoneId = currentHostTimeZoneInString()

    val isCouponNewlyCreated = initialCoupon == null
    val couponFormUiState = couponFormViewModel.couponFormUiState.collectAsState().value

    val formEventHandler = { event: CouponFormEvent ->
        couponFormViewModel.handleEvent(event)
    }

    val dialogInfoState = remember { mutableStateOf<DialogInfo?>(null) }
    val submitFailureTitle = stringResource(R.string.coupon_form_fail_submit_title)
    val submitFailureMessage = stringResource(R.string.coupon_form_fail_submit_message)
    val onSubmitFailed = {
        dialogInfoState.value = DialogInfo(
            titleText = submitFailureTitle,
            messageText = submitFailureMessage,
            onConfirm = {
                dialogInfoState.value = null
            }
        )
    }

    Log.e("CouponFormEditor", "initialCoupon: ${initialCoupon.toString()}")

    LaunchedEffect(initialCoupon) {
        couponFormViewModel.initialize(
            coupon = initialCoupon,
            zoneId = zoneId
        )
    }

    val cardColors = CardColors(
        containerColor = LadosTheme.colorScheme.surfaceContainer,
        contentColor = LadosTheme.colorScheme.onSurface,
        disabledContainerColor = LadosTheme.colorScheme.surfaceContainer,
        disabledContentColor = LadosTheme.colorScheme.onSurface,
    )

    val buttonColors = ButtonColors(
        contentColor = LadosTheme.colorScheme.onPrimaryContainer,
        containerColor = LadosTheme.colorScheme.primaryContainer,
        disabledContentColor = LadosTheme.colorScheme.onPrimaryContainer,
        disabledContainerColor = LadosTheme.colorScheme.primaryContainer,
    )

    val tertiaryButtonColors = ButtonColors(
        contentColor = LadosTheme.colorScheme.onTertiaryContainer,
        containerColor = LadosTheme.colorScheme.tertiaryContainer,
        disabledContentColor = LadosTheme.colorScheme.onTertiaryContainer,
        disabledContainerColor = LadosTheme.colorScheme.tertiaryContainer,
    )

    ConfirmDialog(dialogInfoState.value)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CouponForm(
            modifier = Modifier.weight(1f),
            couponFormUiState = couponFormUiState,
            handleEvent = formEventHandler,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TextButton(
                colors = buttonColors,
                onClick = {
                    formEventHandler(
                        CouponFormEvent.Submit(onSubmitted, onSubmitFailed)
                    )
                }
            ) {
                Text(if (isCouponNewlyCreated) "Create" else "Update")
            }

        }
    }
}

@Preview
@Composable
fun CouponFormEditorPreview() {
    LadosTheme {
        CouponFormEditor(
            onSubmitted = {}
        )
    }
}