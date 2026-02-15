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

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import com.merchpulse.core.common.PreferencesManager
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val preferencesManager: PreferencesManager by inject()
    
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val themeMode by preferencesManager.themeStream.collectAsState("follow_system")
            val language by preferencesManager.languageStream.collectAsState("en")
            
            // Apply Locale
            LaunchedEffect(language) {
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            val isDarkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            MerchPulseTheme(
                windowSizeClass = windowSizeClass,
                darkTheme = isDarkTheme
            ) {
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
