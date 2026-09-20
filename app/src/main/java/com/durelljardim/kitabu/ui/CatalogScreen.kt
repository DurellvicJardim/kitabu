package com.durelljardim.kitabu.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.durelljardim.kitabu.R
import com.durelljardim.kitabu.data.BookEntity
import com.durelljardim.kitabu.ui.theme.AvailableBackground
import com.durelljardim.kitabu.ui.theme.AvailableBackgroundDark
import com.durelljardim.kitabu.ui.theme.AvailableText
import com.durelljardim.kitabu.ui.theme.AvailableTextDark
import com.durelljardim.kitabu.ui.theme.BorrowedBackground
import com.durelljardim.kitabu.ui.theme.BorrowedBackgroundDark
import com.durelljardim.kitabu.ui.theme.BorrowedText
import com.durelljardim.kitabu.ui.theme.BorrowedTextDark

@Composable
fun CatalogScreen(
    books: List<BookEntity>,
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
                BookRow(book = book, onBookClick = onBookClick)
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
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(text = "Search by title or author") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null
                )
            },
            trailingIcon = {
                // Only offer the clear button once something has been typed.
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
        FilterChip(
            selected = availableOnly,
            onClick = { onAvailableOnlyChange(!availableOnly) },
            label = { Text(text = "Available only") },
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun BookRow(book: BookEntity, onBookClick: (BookEntity) -> Unit) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        // clickable on the modifier, not the card, so a borrowed row is not greyed out.
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = book.isAvailable) { onBookClick(book) }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // There are no cover images yet, so every book gets this plain block.
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(84.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = book.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusBadge(
                    isAvailable = book.isAvailable,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(isAvailable: Boolean, modifier: Modifier = Modifier) {
    val darkTheme = isSystemInDarkTheme()
    val textColor = when {
        isAvailable && darkTheme -> AvailableTextDark
        isAvailable -> AvailableText
        darkTheme -> BorrowedTextDark
        else -> BorrowedText
    }
    val backgroundColor = when {
        isAvailable && darkTheme -> AvailableBackgroundDark
        isAvailable -> AvailableBackground
        darkTheme -> BorrowedBackgroundDark
        else -> BorrowedBackground
    }
    Text(
        text = if (isAvailable) "Available" else "Borrowed",
        style = MaterialTheme.typography.labelMedium,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
