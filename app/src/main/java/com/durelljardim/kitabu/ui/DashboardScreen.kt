package com.durelljardim.kitabu.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.durelljardim.kitabu.data.BookingStatus
import com.durelljardim.kitabu.domain.BookingWithBook
import com.durelljardim.kitabu.domain.daysUntil
import com.durelljardim.kitabu.domain.toLocalDate
import java.time.format.DateTimeFormatter

private val reservedDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
private val dueDateFormatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy")

@Composable
fun DashboardScreen(
    bookings: List<BookingWithBook>,
    onCollected: (BookingWithBook) -> Unit,
    onCancel: (BookingWithBook) -> Unit,
    onReturn: (BookingWithBook) -> Unit,
    onRenew: (BookingWithBook) -> Unit,
    modifier: Modifier = Modifier
) {
    if (bookings.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No bookings yet",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Reserve a book from the catalog to see it here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookings, key = { it.bookingId }) { booking ->
                BookingRow(
                    booking = booking,
                    onCollected = { onCollected(booking) },
                    onCancel = { onCancel(booking) },
                    onReturn = { onReturn(booking) },
                    onRenew = { onRenew(booking) }
                )
            }
        }
    }
}

@Composable
private fun BookingRow(
    booking: BookingWithBook,
    onCollected: () -> Unit,
    onCancel: () -> Unit,
    onReturn: () -> Unit,
    onRenew: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(text = "Cancel reservation?") },
            text = {
                Text(text = "The reservation for \"${booking.title}\" will be removed and the book goes back on the shelf.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancel()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Yes, cancel it")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(text = "Keep it")
                }
            }
        )
    }

    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            BookCover(
                title = booking.title,
                modifier = Modifier
                    .width(56.dp)
                    .height(84.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = booking.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = booking.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Reserved " + booking.bookingDate.toLocalDate().format(reservedDateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Due " + booking.returnDeadline.toLocalDate().format(dueDateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    StatusLabel(status = booking.status)
                    DaysLeftLabel(
                        deadlineMillis = booking.returnDeadline,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    // Only PENDING and ACTIVE reach this screen, so no other status needs buttons.
                    if (booking.status == BookingStatus.PENDING) {
                        Button(onClick = onCollected) {
                            Text(text = "Collected")
                        }
                        TextButton(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(text = "Cancel")
                        }
                    } else {
                        Button(onClick = onReturn) {
                            Text(text = "Return")
                        }
                        OutlinedButton(onClick = onRenew) {
                            Text(text = "Renew")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusLabel(status: BookingStatus) {
    Text(
        text = if (status == BookingStatus.PENDING) "Reserved" else "On loan",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun DaysLeftLabel(deadlineMillis: Long, modifier: Modifier = Modifier) {
    val days = daysUntil(deadlineMillis)
    val text = when {
        days > 1 -> "$days days left"
        days == 1L -> "1 day left"
        days == 0L -> "Due today"
        days == -1L -> "Overdue by 1 day"
        else -> "Overdue by ${-days} days"
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        // Overdue and due today stand out in red, the rest stay plain.
        color = if (days <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}
