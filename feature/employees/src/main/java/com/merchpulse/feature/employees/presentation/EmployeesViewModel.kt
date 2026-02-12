package com.merchpulse.feature.employees.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.Role
import com.merchpulse.shared.domain.policy.AuthorizationPolicy
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.feature.employees.EmployeesEffect
import com.merchpulse.shared.feature.employees.EmployeesIntent
import com.merchpulse.shared.feature.employees.EmployeesState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EmployeesViewModel(
    private val repository: EmployeeRepository,
    private val authPolicy: AuthorizationPolicy,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(EmployeesState())
    val state = _state.asStateFlow()

    private val _effect = Channel<EmployeesEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(EmployeesIntent.LoadEmployees)
    }

    fun handleIntent(intent: EmployeesIntent) {
        when (intent) {
            is EmployeesIntent.LoadEmployees -> loadEmployees()
            is EmployeesIntent.ChangeRole -> changeRole(intent.employeeId, intent.newRole)
            is EmployeesIntent.UpdatePermissions -> updatePermissions(intent.employeeId, intent.permissions)
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true) }
            try {
                authPolicy.requirePermission(Permission.EMPLOYEE_VIEW)
                repository.getAllEmployees().collect { list ->
                    _state.update { it.copy(employees = list, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun changeRole(id: String, role: Role) {
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                authPolicy.requirePermission(Permission.EMPLOYEE_MANAGE)
                val employee = repository.getEmployeeById(id).first() ?: return@launch
                repository.updateEmployee(employee.copy(role = role))
                _effect.send(EmployeesEffect.ShowMessage("Role updated for ${employee.fullName}"))
            } catch (e: Exception) {
                _effect.send(EmployeesEffect.ShowMessage(e.message ?: "Update failed"))
            }
        }
    }

    private fun updatePermissions(id: String, perms: Set<Permission>) {
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                authPolicy.requirePermission(Permission.EMPLOYEE_MANAGE)
                val employee = repository.getEmployeeById(id).first() ?: return@launch
                repository.updateEmployee(employee.copy(permissions = perms))
                _effect.send(EmployeesEffect.ShowMessage("Permissions updated"))
            } catch (e: Exception) {
                _effect.send(EmployeesEffect.ShowMessage(e.message ?: "Update failed"))
            }
        }
    }
}
