package com.durelljardim.kitabu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.durelljardim.kitabu.R
import com.durelljardim.kitabu.data.BookEntity
import com.durelljardim.kitabu.di.AppViewModelProvider
import com.durelljardim.kitabu.domain.BookingWithBook
import com.durelljardim.kitabu.ui.theme.HeaderBlack
import com.durelljardim.kitabu.ui.theme.Mustard
import java.time.LocalDate

object Routes {
    const val CATALOG = "catalog"
    const val BOOKINGS = "bookings"
}

private fun countLabel(count: Int, noun: String): String =
    if (count == 1) "1 $noun" else "$count ${noun}s"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitabuApp() {
    val navController = rememberNavController()
    val viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }
    var bookingToRenew by remember { mutableStateOf<BookingWithBook?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        val message = uiState.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.messageShown()
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val goToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                val onBookings = currentRoute == Routes.BOOKINGS
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = HeaderBlack,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        Image(
                            painter = painterResource(R.drawable.logo_kitabu),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier
                                .padding(start = 16.dp, end = 4.dp)
                                .size(32.dp)
                        )
                    },
                    title = {
                        Column {
                            Text(
                                text = if (onBookings) "Your bookings" else "KITABU",
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (onBookings) {
                                    countLabel(uiState.bookings.size, "active booking")
                                } else {
                                    countLabel(uiState.books.size, "book")
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = Mustard
                            )
                        }
                    }
                )
                if (currentRoute == Routes.CATALOG) {
                    CatalogSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        availableOnly = uiState.availableOnly,
                        onAvailableOnlyChange = viewModel::onAvailableOnlyChange
                    )
                }
            }
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                BottomTab(
                    label = "Catalog",
                    selected = currentRoute == Routes.CATALOG,
                    onClick = { goToTab(Routes.CATALOG) },
                    modifier = Modifier.weight(1f)
                )
                BottomTab(
                    label = "Bookings",
                    selected = currentRoute == Routes.BOOKINGS,
                    onClick = { goToTab(Routes.BOOKINGS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CATALOG,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.CATALOG) {
                CatalogScreen(
                    books = uiState.books,
                    dueDates = uiState.bookings.associate { it.bookOwnerId to it.returnDeadline },
                    isFiltering = uiState.searchQuery.isNotBlank() || uiState.availableOnly,
                    onBookClick = { selectedBook = it }
                )
            }
            composable(Routes.BOOKINGS) {
                DashboardScreen(
                    bookings = uiState.bookings,
                    onCollected = viewModel::markCollected,
                    onCancel = viewModel::cancelBooking,
                    onReturn = viewModel::returnBook,
                    onRenew = { bookingToRenew = it }
                )
            }
        }
    }

    selectedBook?.let { book ->
        ReserveSheet(
            book = book,
            onReserve = { name, date ->
                viewModel.reserveBook(book.bookId, name, date)
            },
            onDismiss = { selectedBook = null }
        )
    }

    bookingToRenew?.let { booking ->
        KitabuDatePickerDialog(
            earliestDate = LocalDate.now(),
            onDateChosen = { date -> viewModel.renewBooking(booking, date) },
            onDismiss = { bookingToRenew = null }
        )
    }
}

@Composable
private fun BottomTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(if (selected) Mustard else HeaderBlack)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (selected) HeaderBlack else Color.White
        )
    }
}
