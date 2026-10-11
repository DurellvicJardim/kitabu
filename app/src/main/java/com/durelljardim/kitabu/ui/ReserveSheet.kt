package com.durelljardim.kitabu.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.durelljardim.kitabu.R
import com.durelljardim.kitabu.data.BookEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveSheet(
    book: BookEntity,
    onReserve: (userName: String, returnDate: LocalDate?) -> String?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var returnDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showDatePicker) {
        KitabuDatePickerDialog(
            earliestDate = LocalDate.now(),
            onDateChosen = { date ->
                returnDate = date
                errorMessage = null
            },
            onDismiss = { showDatePicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row {
                BookCover(
                    title = book.title,
                    modifier = Modifier
                        .width(72.dp)
                        .height(108.dp)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = book.author + ", " + book.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                    errorMessage = null
                },
                label = { Text(text = "Your name") },
                singleLine = true,
                shape = RectangleShape,
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text(text = "Enter your name")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Return in",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(Pair(1L, "1 day"), Pair(3L, "3 days"), Pair(7L, "1 week")).forEach { option ->
                        FilterChip(
                            selected = returnDate == LocalDate.now().plusDays(option.first),
                            onClick = {
                                returnDate = LocalDate.now().plusDays(option.first)
                                errorMessage = null
                            },
                            label = {
                                Text(
                                    text = option.second,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            shape = RectangleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                selectedLabelColor = MaterialTheme.colorScheme.inverseOnSurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = { showDatePicker = true },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = null
                )
                val date = returnDate
                Text(
                    text = if (date == null) {
                        "Choose return date"
                    } else {
                        val formatted = date.format(DateTimeFormatter.ofPattern("EEE d MMM"))
                        val days = ChronoUnit.DAYS.between(LocalDate.now(), date)
                        "Due $formatted, $days " + if (days == 1L) "day" else "days"
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Button(
                onClick = {
                    val error = onReserve(name, returnDate)
                    if (error == null) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    } else if (name.isBlank()) {
                        nameError = true
                    } else {
                        errorMessage = error
                    }
                },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reserve")
            }
        }
    }
}
