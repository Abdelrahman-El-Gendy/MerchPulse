package com.merchpulse.shared.feature.auth

import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.mvi.UiEffect
import com.merchpulse.shared.mvi.UiIntent
import com.merchpulse.shared.mvi.UiState

// ─── Sign In ────────────────────────────────────────────────

data class SignInState(
    val email: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class SignInIntent : UiIntent {
    data class EmailChanged(val email: String) : SignInIntent()
    data class PinChanged(val pin: String) : SignInIntent()
    data object Submit : SignInIntent()
}

sealed class SignInEffect : UiEffect {
    data object NavigateToHome : SignInEffect()
    data class ShowError(val message: String) : SignInEffect()
}

// ─── Sign Up ────────────────────────────────────────────────

data class SignUpState(
    val fullName: String = "",
    val email: String = "",
    val pin: String = "",
    val confirmPin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class SignUpIntent : UiIntent {
    data class FullNameChanged(val name: String) : SignUpIntent()
    data class EmailChanged(val email: String) : SignUpIntent()
    data class PinChanged(val pin: String) : SignUpIntent()
    data class ConfirmPinChanged(val pin: String) : SignUpIntent()
    data object Submit : SignUpIntent()
}

sealed class SignUpEffect : UiEffect {
    data object NavigateToHome : SignUpEffect()
    data class ShowError(val message: String) : SignUpEffect()
}
