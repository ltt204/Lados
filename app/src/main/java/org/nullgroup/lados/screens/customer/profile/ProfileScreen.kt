package org.nullgroup.lados.screens.customer.profile

//noinspection UsingMaterialAndMaterial3Libraries
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.ProfileTopAppBar
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.Typography
import org.nullgroup.lados.viewmodels.customer.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    viewModel: ProfileViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val currentUser = viewModel.currentUser.collectAsState()
    Scaffold(
        modifier = modifier.padding(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        ),
        topBar = {
            ProfileTopAppBar(onBackClick = { /*TODO*/ }, content = "Profile")
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp
                ),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Log.d("ProfileScreen", "ProfileScreen: ${currentUser.value}")
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        loading = {
                            LoadOnProgress(
                                modifier = Modifier
                                    .clip(CircleShape)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.padding(top = 16.dp))
                            }
                        },
                        model = ImageRequest
                            .Builder(context = LocalContext.current)
                            .data(currentUser.value.avatarUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Picture"
                    )
                }
                TwoColsItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                modifier = Modifier, text = currentUser.value.name,
                                fontSize = 18.sp,
                                color = LadosTheme.colorScheme.outline,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                modifier = Modifier, text = currentUser.value.email,
                                color = LadosTheme.colorScheme.outline.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                            Text(
                                modifier = Modifier, text = currentUser.value.phoneNumber,
                                color = LadosTheme.colorScheme.outline.copy(alpha = 0.6f),

                                fontSize = 16.sp
                            )
                        }

                    },
                    trailingAction = {
                        TextButton(onClick = {
                            navController?.navigate(Screen.Customer.EditProfile.route)
                        }) {
                            Text(
                                text = "Edit",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LadosTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }, onClick = {
                    })

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Address",
                                color = LadosTheme.colorScheme.outline,
                                style = Typography.bodyLarge
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                painter = painterResource(id = R.drawable.arrowright2),
                                tint = LadosTheme.colorScheme.outline,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
                            Log.d(
                                "ProfileScreen",
                                "ProfileScreen: ${Screen.Customer.Address.AddressList.route}"
                            )
                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Wishlist",
                                color = LadosTheme.colorScheme.outline,
                                style = Typography.bodyLarge
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                painter = painterResource(id = R.drawable.arrowright2),
                                tint = LadosTheme.colorScheme.outline,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
//                            navController?.navigate(Screen.Customer.CartScreen.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Privacy",
                                color = LadosTheme.colorScheme.outline,
                                style = Typography.bodyLarge
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                painter = painterResource(id = R.drawable.arrowright2),
                                tint = LadosTheme.colorScheme.outline,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
//                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Help",
                                color = LadosTheme.colorScheme.outline,
                                style = Typography.bodyLarge
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                painter = painterResource(id = R.drawable.arrowright2),
                                tint = LadosTheme.colorScheme.outline,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
//                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "About",
                                color = LadosTheme.colorScheme.outline,
                                style = Typography.bodyLarge
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                painter = painterResource(id = R.drawable.arrowright2),
                                tint = LadosTheme.colorScheme.outline,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
//                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                }
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                viewModel.signOut(navController)
            }) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Sign out",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    Surface {
        ProfileScreen()
    }
}