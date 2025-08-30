package com.expensetracker.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ReceiptData(
    val id: String? = null,
    val userId: String? = null,
    val storeName: String? = null,
    val storeAddress: String? = null,
    val totalAmount: Double = 0.0,
    val currency: String = "USD",
    val date: String? = null, // ISO string
    val items: List<ReceiptItem> = emptyList(),
    val suggestedCategory: String? = null,
    val confidence: Double = 0.0,
    val metadata: Map<String, String> = emptyMap(),
    val attachmentId: String? = null,
    val createdAt: String? = null // ISO string
)

@Serializable
data class ReceiptItem(
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val category: String? = null
)

@Serializable
data class CreateExpenseFromReceiptRequest(
    val receiptDataId: String,
    val amount: Double,
    val categoryId: String,
    val storeName: String?,
    val date: String?, // ISO string
    val items: List<ReceiptItem> = emptyList(),
    val description: String?
)

@Serializable
data class VoiceExpenseData(
    val id: String? = null,
    val userId: String? = null,
    val originalText: String,
    val parseResult: ExpenseParseResult,
    val overallConfidence: Double = 0.0,
    val suggestions: List<String> = emptyList(),
    val fieldConfidences: Map<String, Double> = emptyMap(),
    val createdAt: String? = null // ISO string
)

@Serializable
data class ExpenseParseResult(
    val amountMinor: Long? = null,
    val currency: String = "USD",
    val categoryId: String? = null,
    val description: String = "",
    val occurredAt: String? = null, // ISO string
    val merchant: String? = null,
    val confidence: Double = 0.0
)

@Serializable
data class VoiceExpenseRequest(
    val spokenText: String,
    val preferredCurrency: String = "USD"
)

@Serializable
data class CreateExpenseFromVoiceRequest(
    val voiceExpenseDataId: String,
    val amountMinor: Long,
    val currency: String,
    val categoryId: String,
    val description: String,
    val merchant: String?
)