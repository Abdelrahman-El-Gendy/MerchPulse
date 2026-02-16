package com.merchpulse.core.common

import kotlinx.coroutines.flow.Flow

interface PreferencesManager {
    fun getString(key: String, defaultValue: String): String
    fun setString(key: String, value: String)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun setBoolean(key: String, value: Boolean)
    
    // For flow-based updates if needed later
    val themeStream: Flow<String>
    val languageStream: Flow<String>
    val biometricEnabledStream: Flow<Boolean>
}
