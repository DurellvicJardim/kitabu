package com.durelljardim.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.durelljardim.kitabu.data.BookEntity
import com.durelljardim.kitabu.di.AppViewModelProvider
import com.durelljardim.kitabu.ui.CatalogScreen
import com.durelljardim.kitabu.ui.CatalogSearchBar
import com.durelljardim.kitabu.ui.LibraryViewModel
import com.durelljardim.kitabu.ui.ReserveSheet
import com.durelljardim.kitabu.ui.theme.KitabuTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitabuTheme {
                val viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)
                // State is collected once here and passed down, so the screens hold no state.
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                // Which book the reserve sheet is open for. Screen state, so it stays here.
                var selectedBook by remember { mutableStateOf<BookEntity?>(null) }
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(uiState.message) {
                    val message = uiState.message
                    if (message != null) {
                        snackbarHostState.showSnackbar(message)
                        viewModel.messageShown()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        Column {
                            TopAppBar(title = { Text(text = "Kitabu") })
                            CatalogSearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = viewModel::onSearchQueryChange,
                                availableOnly = uiState.availableOnly,
                                onAvailableOnlyChange = viewModel::onAvailableOnlyChange
                            )
                        }
                    }
                ) { innerPadding ->
                    CatalogScreen(
                        books = uiState.books,
                        isFiltering = uiState.searchQuery.isNotBlank() || uiState.availableOnly,
                        onBookClick = { selectedBook = it },
                        modifier = Modifier.padding(innerPadding)
                    )
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
            }
        }
    }
}
