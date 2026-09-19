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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.durelljardim.kitabu.di.AppViewModelProvider
import com.durelljardim.kitabu.ui.CatalogScreen
import com.durelljardim.kitabu.ui.CatalogSearchBar
import com.durelljardim.kitabu.ui.LibraryViewModel
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
