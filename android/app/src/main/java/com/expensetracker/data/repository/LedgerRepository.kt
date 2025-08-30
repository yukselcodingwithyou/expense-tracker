package com.expensetracker.data.repository

import com.expensetracker.data.LedgerCreateDTO
import com.expensetracker.data.network.LedgerApiService
import com.expensetracker.data.network.LedgerEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedgerRepository @Inject constructor(
    private val ledgerApiService: LedgerApiService
) {

    suspend fun createEntry(ledgerEntry: LedgerCreateDTO): Result<Unit> {
        return try {
            val response = ledgerApiService.createLedgerEntry(ledgerEntry)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create entry: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEntries(page: Int = 0, size: Int = 20): Result<List<LedgerEntry>> {
        return try {
            val response = ledgerApiService.getLedgerEntries(page, size)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get entries: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEntry(id: String): Result<LedgerEntry> {
        return try {
            val response = ledgerApiService.getLedgerEntry(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get entry: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEntry(id: String, ledgerEntry: LedgerCreateDTO): Result<Unit> {
        return try {
            val response = ledgerApiService.updateLedgerEntry(id, ledgerEntry)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update entry: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEntry(id: String): Result<Unit> {
        return try {
            val response = ledgerApiService.deleteLedgerEntry(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete entry: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}