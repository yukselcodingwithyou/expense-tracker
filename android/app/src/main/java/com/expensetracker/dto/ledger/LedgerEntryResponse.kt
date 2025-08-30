package com.expensetracker.dto.ledger

import kotlinx.serialization.Serializable

@Serializable
data class LedgerEntryResponse(
    val id: String,
    val memberId: String,
    val type: String, // "EXPENSE" or "INCOME"
    val amountMinor: Long,
    val currency: String,
    val categoryId: String,
    val categoryName: String? = null,
    val occurredAt: String, // ISO timestamp
    val notes: String? = null,
    val attachments: List<String> = emptyList(),
    val recurringId: String? = null,
    val createdAt: String, // ISO timestamp
    val updatedAt: String // ISO timestamp
)