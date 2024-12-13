package org.nullgroup.lados.compose.common

import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import org.nullgroup.lados.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    onBackClick: () -> Unit,
    content: String
) {
    CenterAlignedTopAppBar(
        title = { Text(text = content, style = Typography.headlineSmall) },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back button"
                )

            }
        },
        scrollBehavior = scrollBehavior
    )
}