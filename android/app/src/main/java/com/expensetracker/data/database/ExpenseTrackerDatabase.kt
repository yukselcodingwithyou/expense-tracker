package com.expensetracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.expensetracker.data.entities.*
import com.expensetracker.data.dao.*

@Database(
    entities = [
        LedgerEntryEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    
    abstract fun ledgerEntryDao(): LedgerEntryDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun notificationDao(): NotificationDao
    
    companion object {
        @Volatile
        private var INSTANCE: ExpenseTrackerDatabase? = null
        
        fun getDatabase(context: Context): ExpenseTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseTrackerDatabase::class.java,
                    "expense_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    // Add any type converters if needed for complex data types
}