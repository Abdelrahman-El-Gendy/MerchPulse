package com.merchpulse.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.merchpulse.android.navigation.MerchPulseNavHost
import com.merchpulse.android.ui.MerchPulseMainScaffold
import com.merchpulse.core.designsystem.theme.MerchPulseTheme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MerchPulseTheme(windowSizeClass = windowSizeClass) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MerchPulseMainScaffold(navController = navController) { padding ->
                        MerchPulseNavHost(
                            navController = navController,
                            modifier = Modifier.fillMaxSize(),
                            padding = padding
                        )
                    }
                }
            }
        }
    }
}
