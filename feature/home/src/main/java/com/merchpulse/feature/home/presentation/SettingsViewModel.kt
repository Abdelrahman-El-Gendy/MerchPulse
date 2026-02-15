package com.merchpulse.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.PreferencesManager
import com.merchpulse.shared.domain.repository.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    val theme: StateFlow<String> = preferencesManager.themeStream
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "follow_system")
        
    val language: StateFlow<String> = preferencesManager.languageStream
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun setTheme(theme: String) {
        preferencesManager.setString("theme", theme)
    }

    fun setLanguage(language: String) {
        preferencesManager.setString("language", language)
    }

    fun getBiometricEnabled(): Boolean {
        return preferencesManager.getBoolean("biometric_enabled", false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        preferencesManager.setBoolean("biometric_enabled", enabled)
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.endSession()
            onSuccess()
        }
    }
}
