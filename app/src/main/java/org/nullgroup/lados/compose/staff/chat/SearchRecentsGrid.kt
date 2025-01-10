package org.nullgroup.lados.compose.staff.chat

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.compose.profile.UserAvatar
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun SearchRecentGrid(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
     recentlySearches: List<User>,
    onItemSelected: (User) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        contentPadding = paddingValues,
        verticalArrangement = spacedBy(12.dp)
    ) {
        items(items = recentlySearches.take(8), key = { it.id },
            span = {
                GridItemSpan(1)
            }) { user ->
            SearchResultItemGrid(
                modifier = Modifier.fillMaxWidth(),
                user = user,
                onItemSelected = onItemSelected
            )
        }
    }
}

@Composable
fun SearchResultItemGrid(
    modifier: Modifier = Modifier,
    user: User,
    onItemSelected: (User) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        UserAvatar(user = user)
        Spacer(modifier = Modifier.heightIn(8.dp))
        Text(
            text = user.name, style = LadosTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchRecentGridPreview() {
    LadosTheme {
        SearchRecentGrid(
            recentlySearches = listOf(
                User(
                    id = "1",
                    name = "John Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "2",
                    name = "Jane Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "3",
                    name = "John Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "4",
                    name = "Jane Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "5",
                    name = "John Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "6",
                    name = "Jane Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "7",
                    name = "John Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "8",
                    name = "Jane Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
                User(
                    id = "9",
                    name = "John Doe",
                    avatarUri = "https://randomuser.me/api/port"
                ),
            ),
            onItemSelected = {}
        )
    }
}