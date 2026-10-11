package com.durelljardim.kitabu

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            KitabuTheme {
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
