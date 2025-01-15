package org.nullgroup.lados.compose.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.models.User

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    user: User
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        loading = {
            LoadOnProgress(
                modifier = Modifier
                    .clip(CircleShape)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.padding(top = 16.dp))
            }
        },
        model = ImageRequest
            .Builder(context = LocalContext.current)
            .data(user.avatarUri)
            .crossfade(true)
            .build(),
        contentDescription = "Profile Picture"
    )

}