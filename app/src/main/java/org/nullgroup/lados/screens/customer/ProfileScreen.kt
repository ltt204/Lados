package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.profile.TwoColsItem
import org.nullgroup.lados.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Profile") })
        },
        modifier = modifier
            .padding(bottom = paddingValues.calculateTopPadding())
            .background(color = Color.Transparent)
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "Profile Picture"
                    )
                }
                Detail(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = innerPadding.calculateTopPadding()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Address",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Wishlist",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Privacy",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "Help",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                    TwoColsItem(
                        modifier = Modifier.height(56.dp),
                        content = {
                            Text(
                                text = "About",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        trailingAction = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .height(20.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Arrow",
                            )
                        }, onClick = {
                            navController?.navigate(Screen.Customer.Address.AddressList.route)
                        })
                }
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = { /*TODO*/ }) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Sign out",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Detail(
    modifier: Modifier = Modifier,
    userName: String = "User Name",
    email: String = "example@gamil.com",
    phoneNumber: String = "+91 1234567890"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    modifier = Modifier
                        .weight(1f), text = userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .weight(1f), text = email,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Text(
                    modifier = Modifier
                        .weight(1f), text = phoneNumber,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
            TextButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .height(20.dp),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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