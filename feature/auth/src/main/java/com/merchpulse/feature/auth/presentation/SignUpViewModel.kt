package com.merchpulse.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.Role
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.SessionManager
import com.merchpulse.shared.feature.auth.SignUpEffect
import com.merchpulse.shared.feature.auth.SignUpIntent
import com.merchpulse.shared.feature.auth.SignUpState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID

class SignUpViewModel(
    private val employeeRepository: EmployeeRepository,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SignUpEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.FullNameChanged -> _state.value = _state.value.copy(fullName = intent.name)
            is SignUpIntent.EmailChanged -> _state.value = _state.value.copy(email = intent.email)
            is SignUpIntent.PinChanged -> _state.value = _state.value.copy(pin = intent.pin)
            is SignUpIntent.ConfirmPinChanged -> _state.value = _state.value.copy(confirmPin = intent.pin)
            is SignUpIntent.Submit -> signUp()
        }
    }

    private fun signUp() {
        viewModelScope.launch(dispatcherProvider.io) {
            val s = _state.value

            if (s.fullName.isBlank() || s.email.isBlank() || s.pin.isBlank()) {
                _state.value = s.copy(error = "All fields are required")
                return@launch
            }
            if (s.pin != s.confirmPin) {
                _state.value = s.copy(error = "PINs do not match")
                return@launch
            }

            _state.value = s.copy(isLoading = true, error = null)

            val newEmployee = Employee(
                id = UUID.randomUUID().toString(),
                email = s.email.trim(),
                fullName = s.fullName.trim(),
                role = Role.STAFF,
                permissions = setOf(Permission.PUNCH_SELF, Permission.PRODUCT_VIEW),
                isActive = true,
                joinedAt = Clock.System.now()
            )

            val result = employeeRepository.createEmployee(newEmployee, s.pin)
            if (result.isSuccess) {
                sessionManager.startSession(newEmployee)
                _effect.send(SignUpEffect.NavigateToHome)
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }
}
