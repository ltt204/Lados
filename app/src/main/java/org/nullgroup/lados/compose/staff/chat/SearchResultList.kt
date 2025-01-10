package org.nullgroup.lados.compose.staff.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.compose.profile.UserAvatar
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.ui.theme.LadosTheme


@Composable
fun SearchResultList(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    listResult: List<User>,
    onItemSelected: (User) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues,
        verticalArrangement = spacedBy(8.dp)
    ) {
        items(items = listResult, key = { it.id }) { user ->
            SearchResultItem(
                modifier = Modifier.fillMaxWidth(),
                user = user,
                onItemSelected = onItemSelected
            )
        }
    }
}

@Composable
fun SearchResultItem(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    user: User,
    onItemSelected: (User) -> Unit
) {
    Row(modifier = modifier.clickable { onItemSelected(user) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(user = user)
            Spacer(modifier = Modifier.widthIn(16.dp))
            Text(
                text = user.name, style = LadosTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultListPreview() {
    SearchResultList(
        modifier = Modifier.fillMaxSize(),
        listResult = listOf(
            User(
                "1",
                "John Doe",
                "https://randomuser.me/api/port"
            ),
            User(
                "2",
                "Jane Doe",
                "https://randomuser.me/api/port"
            ),
            User(
                "3",
                "Jane Doe",
                "https://randomuser.me/api/port"
            ), User(
                "4",
                "Jane Doe",
                "https://randomuser.me/api/port"
            )
        ),
        onItemSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SearchResultItemPreview() {
    SearchResultItem(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        user = User(
            "1",
            "John Doe",
            "https://randomuser.me/api/port"
        ),
        onItemSelected = {}
    )
}