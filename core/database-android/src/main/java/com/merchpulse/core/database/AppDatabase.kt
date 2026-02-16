package com.merchpulse.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.merchpulse.core.database.dao.AuditDao
import com.merchpulse.core.database.dao.EmployeeDao
import com.merchpulse.core.database.dao.ProductDao
import com.merchpulse.core.database.dao.PunchDao
import com.merchpulse.core.database.entity.AuditEntity
import com.merchpulse.core.database.entity.EmployeeEntity
import com.merchpulse.core.database.entity.EmployeePermissionEntity
import com.merchpulse.core.database.entity.ProductEntity
import com.merchpulse.core.database.entity.PunchEntity

@Database(
    entities = [
        ProductEntity::class,
        EmployeeEntity::class,
        EmployeePermissionEntity::class,
        PunchEntity::class,
        AuditEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun punchDao(): PunchDao
    abstract fun auditDao(): AuditDao

    companion object {
        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "merch_pulse.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
