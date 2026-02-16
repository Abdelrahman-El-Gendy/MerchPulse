package com.merchpulse.core.common

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidPreferencesManager(context: Context) : PreferencesManager {
    private val prefs: SharedPreferences = context.getSharedPreferences("merchpulse_prefs", Context.MODE_PRIVATE)
    
    private val _themeStream = MutableStateFlow(getString("theme", "follow_system"))
    override val themeStream: Flow<String> = _themeStream.asStateFlow()
    
    private val _languageStream = MutableStateFlow(getString("language", "en"))
    override val languageStream: Flow<String> = _languageStream.asStateFlow()

    private val _biometricStream = MutableStateFlow(getBoolean("biometric_enabled", false))
    override val biometricEnabledStream: Flow<Boolean> = _biometricStream.asStateFlow()

    override fun getString(key: String, defaultValue: String): String = prefs.getString(key, defaultValue) ?: defaultValue
    
    override fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
        if (key == "theme") _themeStream.value = value
        if (key == "language") _languageStream.value = value
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    
    override fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
        if (key == "biometric_enabled") _biometricStream.value = value
    }
}
