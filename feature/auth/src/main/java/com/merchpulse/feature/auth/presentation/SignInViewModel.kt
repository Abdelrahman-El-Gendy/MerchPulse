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
            is SignInIntent.PhoneNumberChanged -> _state.value = _state.value.copy(phoneNumber = intent.phone)
            is SignInIntent.CountryCodeChanged -> _state.value = _state.value.copy(countryCode = intent.code)
            is SignInIntent.PinChanged -> _state.value = _state.value.copy(pin = intent.pin)
            is SignInIntent.Submit -> signIn()
        }
    }

    private fun signIn() {
        viewModelScope.launch(dispatcherProvider.io) {
            val phone = _state.value.phoneNumber.trim()
            val countryCode = _state.value.countryCode
            val pin = _state.value.pin

            if (phone.isBlank() || pin.isBlank()) {
                _state.value = _state.value.copy(error = "Phone number and Password are required")
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)

            // In a real app, combine country code with phone
            val fullPhone = "$countryCode$phone"
            
            // For now, search by the number provided in the database
            // (Assuming the seeder or creation logic stores it consistently)
            val employee = employeeRepository.getEmployeeByPhone(phone).first() 
                ?: employeeRepository.getEmployeeByPhone(fullPhone).first()

            if (employee != null) {
                val verified = employeeRepository.verifyPin(employee.id, pin)
                if (verified) {
                    sessionManager.startSession(employee)
                    _effect.send(SignInEffect.NavigateToHome)
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "Invalid Password")
                }
            } else {
                _state.value = _state.value.copy(isLoading = false, error = "User not found")
            }
        }
    }
}
