package com.expensetracker.data.repository

import com.expensetracker.data.dao.*
import com.expensetracker.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineLedgerRepository @Inject constructor(
    private val ledgerEntryDao: LedgerEntryDao,
    private val apiService: LedgerApiService, // Inject your API service
    private val networkConnectivity: NetworkConnectivity // Inject network checker
) {
    
    fun getEntries(familyId: String): Flow<List<LedgerEntry>> = 
        ledgerEntryDao.getEntries(familyId).map { entities -> 
            entities.map { it.toDomainModel() } 
        }
    
    fun getRecentEntries(familyId: String, type: String): Flow<List<LedgerEntry>> =
        ledgerEntryDao.getRecentEntries(familyId, type).map { entities ->
            entities.map { it.toDomainModel() }
        }
    
    suspend fun createEntry(request: CreateLedgerEntryRequest): Result<LedgerEntry> {
        return try {
            if (networkConnectivity.isConnected()) {
                // Try online first
                val response = apiService.createEntry(request)
                if (response.isSuccessful) {
                    val entry = response.body()!!
                    // Save to local database
                    ledgerEntryDao.insertEntry(entry.toEntity().copy(isOnline = true, needsSync = false))
                    Result.success(entry)
                } else {
                    // API failed, save offline
                    saveOfflineEntry(request)
                }
            } else {
                // No network, save offline
                saveOfflineEntry(request)
            }
        } catch (e: Exception) {
            // Exception occurred, save offline
            saveOfflineEntry(request)
        }
    }
    
    private suspend fun saveOfflineEntry(request: CreateLedgerEntryRequest): Result<LedgerEntry> {
        val entry = request.toEntity().copy(
            id = generateLocalId(),
            isOnline = false,
            needsSync = true
        )
        ledgerEntryDao.insertEntry(entry)
        return Result.success(entry.toDomainModel())
    }
    
    suspend fun syncPendingEntries() {
        val pendingEntries = ledgerEntryDao.getEntriesNeedingSync()
        
        pendingEntries.forEach { entry ->
            try {
                val request = entry.toCreateRequest()
                val response = apiService.createEntry(request)
                
                if (response.isSuccessful) {
                    val onlineEntry = response.body()!!
                    // Update local entry with server data
                    ledgerEntryDao.updateEntry(
                        entry.copy(
                            id = onlineEntry.id,
                            isOnline = true,
                            needsSync = false
                        )
                    )
                }
            } catch (e: Exception) {
                // Sync failed, will retry later
                println("Failed to sync entry ${entry.id}: ${e.message}")
            }
        }
    }
    
    suspend fun updateEntry(entryId: String, request: CreateLedgerEntryRequest): Result<LedgerEntry> {
        return try {
            if (networkConnectivity.isConnected()) {
                val response = apiService.updateEntry(entryId, request)
                if (response.isSuccessful) {
                    val entry = response.body()!!
                    ledgerEntryDao.updateEntry(entry.toEntity().copy(isOnline = true, needsSync = false))
                    Result.success(entry)
                } else {
                    // Mark for sync
                    markForSync(entryId, request)
                }
            } else {
                markForSync(entryId, request)
            }
        } catch (e: Exception) {
            markForSync(entryId, request)
        }
    }
    
    private suspend fun markForSync(entryId: String, request: CreateLedgerEntryRequest): Result<LedgerEntry> {
        val entity = request.toEntity().copy(
            id = entryId,
            isOnline = false,
            needsSync = true,
            updatedAt = System.currentTimeMillis()
        )
        ledgerEntryDao.updateEntry(entity)
        return Result.success(entity.toDomainModel())
    }
    
    suspend fun deleteEntry(entryId: String): Result<Unit> {
        return try {
            if (networkConnectivity.isConnected()) {
                val response = apiService.deleteEntry(entryId)
                if (response.isSuccessful) {
                    ledgerEntryDao.deleteEntryById(entryId)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to delete entry"))
                }
            } else {
                // Mark as deleted locally (you might want a deletedAt field)
                ledgerEntryDao.deleteEntryById(entryId)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateLocalId(): String = "local_${System.currentTimeMillis()}_${(1000..9999).random()}"
}

// Extension functions for conversions
private fun LedgerEntryEntity.toDomainModel(): LedgerEntry {
    return LedgerEntry(
        id = id,
        familyId = familyId,
        memberId = memberId,
        type = TransactionType.valueOf(type),
        amount = MoneyAmount(amountMinor, currency),
        categoryId = categoryId,
        occurredAt = Instant.ofEpochMilli(occurredAt),
        notes = notes,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

private fun LedgerEntry.toEntity(): LedgerEntryEntity {
    return LedgerEntryEntity(
        id = id,
        familyId = familyId,
        memberId = memberId,
        type = type.name,
        amountMinor = amount.minor,
        currency = amount.currency,
        categoryId = categoryId,
        occurredAt = occurredAt.toEpochMilli(),
        notes = notes,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}

private fun CreateLedgerEntryRequest.toEntity(): LedgerEntryEntity {
    return LedgerEntryEntity(
        id = "", // Will be set later
        familyId = "", // Will be set later
        memberId = memberId,
        type = type.name,
        amountMinor = amountMinor,
        currency = currency,
        categoryId = categoryId,
        occurredAt = occurredAt.toEpochMilli(),
        notes = notes
    )
}

private fun LedgerEntryEntity.toCreateRequest(): CreateLedgerEntryRequest {
    return CreateLedgerEntryRequest(
        memberId = memberId,
        type = TransactionType.valueOf(type),
        amountMinor = amountMinor,
        currency = currency,
        categoryId = categoryId,
        occurredAt = Instant.ofEpochMilli(occurredAt),
        notes = notes
    )
}

// Placeholder interfaces and classes that would be implemented elsewhere
interface LedgerApiService {
    suspend fun createEntry(request: CreateLedgerEntryRequest): ApiResponse<LedgerEntry>
    suspend fun updateEntry(id: String, request: CreateLedgerEntryRequest): ApiResponse<LedgerEntry>
    suspend fun deleteEntry(id: String): ApiResponse<Unit>
}

interface NetworkConnectivity {
    fun isConnected(): Boolean
}

data class ApiResponse<T>(
    val isSuccessful: Boolean,
    val body: T?
)

// Domain models (these would be defined elsewhere in your app)
data class LedgerEntry(
    val id: String,
    val familyId: String,
    val memberId: String,
    val type: TransactionType,
    val amount: MoneyAmount,
    val categoryId: String,
    val occurredAt: java.time.Instant,
    val notes: String?,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant
)

data class MoneyAmount(
    val minor: Long,
    val currency: String
)

enum class TransactionType { INCOME, EXPENSE }

data class CreateLedgerEntryRequest(
    val memberId: String,
    val type: TransactionType,
    val amountMinor: Long,
    val currency: String,
    val categoryId: String,
    val occurredAt: java.time.Instant,
    val notes: String?
)