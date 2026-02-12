package com.merchpulse.shared.feature.employees

import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.Role
import com.merchpulse.shared.mvi.UiEffect
import com.merchpulse.shared.mvi.UiIntent
import com.merchpulse.shared.mvi.UiState

data class EmployeesState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class EmployeesIntent : UiIntent {
    data object LoadEmployees : EmployeesIntent()
    data class ChangeRole(val employeeId: String, val newRole: Role) : EmployeesIntent()
    data class UpdatePermissions(val employeeId: String, val permissions: Set<Permission>) : EmployeesIntent()
}

sealed class EmployeesEffect : UiEffect {
    data class ShowMessage(val message: String) : EmployeesEffect()
}
