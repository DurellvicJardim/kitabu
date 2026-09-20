package com.durelljardim.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.durelljardim.kitabu.ui.KitabuApp
import com.durelljardim.kitabu.ui.SPLASH_MILLIS
import com.durelljardim.kitabu.ui.SplashScreen
import com.durelljardim.kitabu.ui.theme.KitabuTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitabuTheme {
                // The app is ready straight away, so the splash is held for a set time.
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(SPLASH_MILLIS)
                    showSplash = false
                }
                if (showSplash) {
                    SplashScreen()
                } else {
                    KitabuApp()
                }
            }
        }
    }
}
