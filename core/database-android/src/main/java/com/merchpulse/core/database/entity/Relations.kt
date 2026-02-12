package com.merchpulse.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EmployeeWithPermissions(
    @Embedded val employee: EmployeeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "employeeId"
    )
    val permissionEntities: List<EmployeePermissionEntity>
)
