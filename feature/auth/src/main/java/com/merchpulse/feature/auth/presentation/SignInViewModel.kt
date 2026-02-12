package com.merchpulse.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.SessionManager
import com.merchpulse.shared.feature.auth.SignInEffect
import com.merchpulse.shared.feature.auth.SignInIntent
import com.merchpulse.shared.feature.auth.SignInState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val employeeRepository: EmployeeRepository,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SignInEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: SignInIntent) {
        when (intent) {
            is SignInIntent.EmailChanged -> _state.value = _state.value.copy(email = intent.email)
            is SignInIntent.PinChanged -> _state.value = _state.value.copy(pin = intent.pin)
            is SignInIntent.Submit -> signIn()
        }
    }

    private fun signIn() {
        viewModelScope.launch(dispatcherProvider.io) {
            val email = _state.value.email.trim()
            val pin = _state.value.pin

            if (email.isBlank() || pin.isBlank()) {
                _state.value = _state.value.copy(error = "Email and PIN are required")
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)

            val employee = employeeRepository.getEmployeeByEmail(email).first()
            if (employee != null) {
                val verified = employeeRepository.verifyPin(employee.id, pin)
                if (verified) {
                    sessionManager.startSession(employee)
                    _effect.send(SignInEffect.NavigateToHome)
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "Invalid PIN")
                }
            } else {
                _state.value = _state.value.copy(isLoading = false, error = "User not found")
            }
        }
    }
}
