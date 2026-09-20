package com.durelljardim.kitabu.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontFamily
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
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = book.author + ", " + book.category,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                    errorMessage = null
                },
                label = { Text(text = "Your name") },
                singleLine = true,
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text(text = "Enter your name")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(
                onClick = { showDatePicker = true },
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
                        // Slide the sheet away before the book is cleared, so it does not just vanish.
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    } else if (name.isBlank()) {
                        // The name field shows its own error, so do not repeat it above the button.
                        nameError = true
                    } else {
                        errorMessage = error
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reserve")
            }
        }
    }
}

