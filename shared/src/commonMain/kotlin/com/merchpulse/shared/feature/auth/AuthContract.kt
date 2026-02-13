package com.merchpulse.shared.feature.auth

import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.mvi.UiEffect
import com.merchpulse.shared.mvi.UiIntent
import com.merchpulse.shared.mvi.UiState

// ─── Sign In ────────────────────────────────────────────────

data class SignInState(
    val phoneNumber: String = "",
    val countryCode: String = "+20",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class SignInIntent : UiIntent {
    data class PhoneNumberChanged(val phone: String) : SignInIntent()
    data class CountryCodeChanged(val code: String) : SignInIntent()
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
    val phoneNumber: String = "",
    val countryCode: String = "+20",
    val email: String = "",
    val pin: String = "",
    val confirmPin: String = "",
    val selectedRole: com.merchpulse.shared.domain.model.Role = com.merchpulse.shared.domain.model.Role.STAFF,
    val isTermsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class SignUpIntent : UiIntent {
    data class FullNameChanged(val name: String) : SignUpIntent()
    data class PhoneNumberChanged(val phone: String) : SignUpIntent()
    data class CountryCodeChanged(val code: String) : SignUpIntent()
    data class EmailChanged(val email: String) : SignUpIntent()
    data class PinChanged(val pin: String) : SignUpIntent()
    data class ConfirmPinChanged(val pin: String) : SignUpIntent()
    data class RoleChanged(val role: com.merchpulse.shared.domain.model.Role) : SignUpIntent()
    data class TermsToggled(val accepted: Boolean) : SignUpIntent()
    data object Submit : SignUpIntent()
}

sealed class SignUpEffect : UiEffect {
    data object NavigateToHome : SignUpEffect()
    data class ShowError(val message: String) : SignUpEffect()
}
