package org.nullgroup.lados.screens.customer.profile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.DefaultCenterTopAppBar
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.compose.profile.SwipeToDeleteContainer
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.customer.AddressListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressList(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val viewModel = hiltViewModel<AddressListViewModel>()
    val addressList = viewModel.userAddresses.collectAsState()
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            DefaultCenterTopAppBar(
                onBackClick = { navController?.navigateUp() },
                content = stringResource(id = R.string.address_title),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                start = 16.dp,
                end = 16.dp
            )
        ) {
            if (addressList.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_address_found_message),
                        textAlign = TextAlign.Center,
                        style = LadosTheme.typography.bodyLarge.copy(
                            color = LadosTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 40.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = addressList.value, key = { it.id }) { address ->
                        SwipeToDeleteContainer(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .height(80.dp),
                            item = address,
                            onDelete = {
                                viewModel.deleteAddress(it.id)
                            },
                        ) {
                            TwoColsItem(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .height(80.dp),
                                content = {
                                    Log.d("AddressList", "Address: $address")
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = address.toString(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 16.sp,
                                        color = LadosTheme.colorScheme.onBackground,
                                    )
                                },
                                trailingAction = {
                                    TextButton(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .height(20.dp),
                                        onClick = {
                                            Log.d(
                                                "AddressList",
                                                "Route: ${Screen.Customer.Address.EditAddress.route}/${address.id}"
                                            )
                                            navController?.navigate("${Screen.Customer.Address.EditAddress.route}/${address.id}")
                                        }) {
                                        Text(
                                            text = stringResource(R.string.edit_button),
                                            color = LadosTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = { navController?.navigate(Screen.Customer.Address.AddAddress.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LadosTheme.colorScheme.primary,
                    contentColor = LadosTheme.colorScheme.onPrimary,
                    disabledContainerColor = LadosTheme.colorScheme.outline,
                ),
            ) {
                Text(text = stringResource(R.string.address_add_button), style = LadosTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ))
            }
        }
    }
}