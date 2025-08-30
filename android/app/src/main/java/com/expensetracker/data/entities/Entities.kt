package com.expensetracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledger_entries")
data class LedgerEntryEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val memberId: String,
    val type: String, // INCOME, EXPENSE
    val amountMinor: Long,
    val currency: String,
    val categoryId: String,
    val occurredAt: Long,
    val notes: String?,
    val isOnline: Boolean = false,
    val needsSync: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: String, // INCOME, EXPENSE
    val archived: Boolean = false,
    val isOnline: Boolean = false,
    val needsSync: Boolean = false
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val name: String,
    val overallLimitMinor: Long,
    val periodType: String, // MONTHLY, WEEKLY, YEARLY
    val periodStart: Long,
    val periodEnd: Long,
    val alertThresholdPct: Double,
    val includeRecurring: Boolean,
    val isOnline: Boolean = false,
    val needsSync: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val familyId: String,
    val type: String,
    val title: String,
    val message: String,
    val data: String?, // JSON string
    val isRead: Boolean = false,
    val emailSent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)