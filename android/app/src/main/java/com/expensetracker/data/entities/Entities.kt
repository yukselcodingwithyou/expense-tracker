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

// Extension methods for entity conversion
fun LedgerEntryEntity.toDomainModel(): LedgerEntry {
    return LedgerEntry(
        id = this.id,
        familyId = this.familyId,
        memberId = this.memberId,
        type = LedgerEntryType.valueOf(this.type),
        amountMinor = this.amountMinor.toInt(),
        currency = this.currency,
        categoryId = this.categoryId,
        occurredAt = java.time.Instant.ofEpochMilli(this.occurredAt).toString(),
        notes = this.notes
    )
}

fun CategoryEntity.toDomainModel(): Category {
    return Category(
        id = this.id,
        familyId = this.familyId,
        name = this.name,
        icon = this.icon,
        color = this.color,
        type = LedgerEntryType.valueOf(this.type),
        archived = this.archived
    )
}

fun BudgetEntity.toDomainModel(): Budget {
    return Budget(
        id = this.id,
        familyId = this.familyId,
        name = this.name,
        overallLimitMinor = this.overallLimitMinor.toInt(),
        periodType = BudgetPeriodType.valueOf(this.periodType),
        periodStart = java.time.Instant.ofEpochMilli(this.periodStart).toString(),
        periodEnd = java.time.Instant.ofEpochMilli(this.periodEnd).toString(),
        alertThresholdPct = this.alertThresholdPct,
        includeRecurring = this.includeRecurring
    )
}

fun NotificationEntity.toDomainModel(): Notification {
    return Notification(
        id = this.id,
        userId = this.userId,
        familyId = this.familyId,
        type = this.type,
        title = this.title,
        message = this.message,
        data = this.data,
        isRead = this.isRead,
        emailSent = this.emailSent,
        createdAt = java.time.Instant.ofEpochMilli(this.createdAt).toString()
    )
}

// Extension methods for API to entity conversion
fun LedgerEntry.toEntity(): LedgerEntryEntity {
    return LedgerEntryEntity(
        id = this.id,
        familyId = this.familyId,
        memberId = this.memberId,
        type = this.type.name,
        amountMinor = this.amountMinor.toLong(),
        currency = this.currency,
        categoryId = this.categoryId,
        occurredAt = java.time.Instant.parse(this.occurredAt).toEpochMilli(),
        notes = this.notes
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        familyId = this.familyId,
        name = this.name,
        icon = this.icon,
        color = this.color,
        type = this.type.name,
        archived = this.archived
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = this.id,
        familyId = this.familyId,
        name = this.name,
        overallLimitMinor = this.overallLimitMinor.toLong(),
        periodType = this.periodType.name,
        periodStart = java.time.Instant.parse(this.periodStart).toEpochMilli(),
        periodEnd = java.time.Instant.parse(this.periodEnd).toEpochMilli(),
        alertThresholdPct = this.alertThresholdPct,
        includeRecurring = this.includeRecurring
    )
}

// Extension methods for request to entity conversion
fun CreateLedgerEntryRequest.toEntity(): LedgerEntryEntity {
    return LedgerEntryEntity(
        id = "",
        familyId = this.familyId,
        memberId = this.memberId,
        type = this.type.name,
        amountMinor = this.amountMinor.toLong(),
        currency = this.currency,
        categoryId = this.categoryId,
        occurredAt = java.time.Instant.parse(this.occurredAt).toEpochMilli(),
        notes = this.notes
    )
}

fun CreateCategoryRequest.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = "",
        familyId = this.familyId,
        name = this.name,
        icon = this.icon,
        color = this.color,
        type = this.type.name,
        archived = false
    )
}

fun CreateBudgetRequest.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = "",
        familyId = this.familyId,
        name = this.name,
        overallLimitMinor = this.overallLimitMinor.toLong(),
        periodType = this.periodType.name,
        periodStart = java.time.Instant.parse(this.periodStart).toEpochMilli(),
        periodEnd = java.time.Instant.parse(this.periodEnd).toEpochMilli(),
        alertThresholdPct = this.alertThresholdPct,
        includeRecurring = this.includeRecurring
    )
}

// Extension methods for entity to request conversion
fun LedgerEntryEntity.toCreateRequest(): CreateLedgerEntryRequest {
    return CreateLedgerEntryRequest(
        familyId = this.familyId,
        memberId = this.memberId,
        type = LedgerEntryType.valueOf(this.type),
        amountMinor = this.amountMinor.toInt(),
        currency = this.currency,
        categoryId = this.categoryId,
        occurredAt = java.time.Instant.ofEpochMilli(this.occurredAt).toString(),
        notes = this.notes
    )
}

fun CategoryEntity.toCreateRequest(): CreateCategoryRequest {
    return CreateCategoryRequest(
        familyId = this.familyId,
        name = this.name,
        icon = this.icon,
        color = this.color,
        type = LedgerEntryType.valueOf(this.type)
    )
}

fun BudgetEntity.toCreateRequest(): CreateBudgetRequest {
    return CreateBudgetRequest(
        familyId = this.familyId,
        name = this.name,
        overallLimitMinor = this.overallLimitMinor.toInt(),
        periodType = BudgetPeriodType.valueOf(this.periodType),
        periodStart = java.time.Instant.ofEpochMilli(this.periodStart).toString(),
        periodEnd = java.time.Instant.ofEpochMilli(this.periodEnd).toString(),
        alertThresholdPct = this.alertThresholdPct,
        includeRecurring = this.includeRecurring
    )
}