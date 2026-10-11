package com.durelljardim.kitabu.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.durelljardim.kitabu.R
import com.durelljardim.kitabu.data.BookEntity
import com.durelljardim.kitabu.domain.toLocalDate
import com.durelljardim.kitabu.ui.theme.BurntOrange
import com.durelljardim.kitabu.ui.theme.ForestGreen
import java.time.format.DateTimeFormatter

private val dueBackFormatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy")

@Composable
fun CatalogScreen(
    books: List<BookEntity>,
    dueDates: Map<Int, Long>,
    isFiltering: Boolean,
    onBookClick: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (books.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isFiltering) "No books match your search" else "No books to show",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(books, key = { it.bookId }) { book ->
                BookRow(
                    book = book,
                    dueBack = dueDates[book.bookId],
                    onBookClick = onBookClick
                )
            }
        }
    }
}

@Composable
fun CatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    availableOnly: Boolean,
    onAvailableOnlyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RectangleShape,
            placeholder = { Text(text = "Search by title or author") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() }
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.toggleable(
                value = availableOnly,
                role = Role.Checkbox,
                onValueChange = onAvailableOnlyChange
            )
        ) {
            Checkbox(checked = availableOnly, onCheckedChange = null, modifier = Modifier.padding(12.dp))
            Text(text = "Available only", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun BookRow(book: BookEntity, dueBack: Long?, onBookClick: (BookEntity) -> Unit) {
    OutlinedCard(
        shape = RectangleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = book.isAvailable) { onBookClick(book) }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            BookCover(
                title = book.title,
                modifier = Modifier
                    .width(56.dp)
                    .height(84.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = book.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    StatusBadge(isAvailable = book.isAvailable)
                    if (!book.isAvailable && dueBack != null) {
                        Text(
                            text = "Due back " + dueBack.toLocalDate().format(dueBackFormatter),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(isAvailable: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = if (isAvailable) "Available" else "Borrowed",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = Color.White,
        modifier = modifier
            .background(if (isAvailable) ForestGreen else BurntOrange)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
