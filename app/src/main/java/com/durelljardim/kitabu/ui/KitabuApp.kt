package com.durelljardim.kitabu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
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
import java.time.LocalDate

object Routes {
    const val CATALOG = "catalog"
    const val BOOKINGS = "bookings"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitabuApp() {
    val navController = rememberNavController()
    // One ViewModel shared by both screens, so they read the same database observers.
    val viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    // State is collected once here and passed down, so the screens hold no state.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Which book the reserve sheet is open for. Screen state, so it stays here.
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }
    // Which booking the renew date picker is open for. Null keeps the picker hidden.
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                val onBookings = currentRoute == Routes.BOOKINGS
                TopAppBar(
                    navigationIcon = {
                        // The logo is black line art, so it is tinted to stay visible in dark mode.
                        Image(
                            painter = painterResource(R.drawable.logo_kitabu),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier
                                .padding(start = 16.dp, end = 4.dp)
                                .size(32.dp)
                        )
                    },
                    title = {
                        Text(text = if (onBookings) "Your bookings" else "Kitabu")
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
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Routes.CATALOG,
                    onClick = {
                        navController.navigate(Routes.CATALOG) {
                            // Backing out of a tab goes to the first screen instead of stacking copies.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_book),
                            contentDescription = null
                        )
                    },
                    label = { Text(text = "Catalog") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.BOOKINGS,
                    onClick = {
                        navController.navigate(Routes.BOOKINGS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = null
                        )
                    },
                    label = { Text(text = "Bookings") }
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
        // Renew is validated in the ViewModel, so the picker only blocks past dates here.
        KitabuDatePickerDialog(
            earliestDate = LocalDate.now(),
            onDateChosen = { date -> viewModel.renewBooking(booking, date) },
            onDismiss = { bookingToRenew = null }
        )
    }
}
