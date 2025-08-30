package com.expensetracker.data.repository

import com.expensetracker.data.model.*
import com.expensetracker.data.network.VoiceExpenseApiService
import com.expensetracker.dto.ledger.LedgerEntryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceExpenseRepository @Inject constructor(
    private val voiceExpenseApiService: VoiceExpenseApiService
) {
    
    suspend fun processVoiceExpense(request: VoiceExpenseRequest): Result<VoiceExpenseData> {
        return try {
            val response = voiceExpenseApiService.processVoiceExpense(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to process voice expense: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createExpenseFromVoice(request: CreateExpenseFromVoiceRequest): Result<LedgerEntryResponse> {
        return try {
            val response = voiceExpenseApiService.createExpenseFromVoice(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create expense: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserVoiceExpenses(): Result<List<VoiceExpenseData>> {
        return try {
            val response = voiceExpenseApiService.getUserVoiceExpenses()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get voice expenses: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getVoiceExpenseData(voiceExpenseDataId: String): Result<VoiceExpenseData> {
        return try {
            val response = voiceExpenseApiService.getVoiceExpenseData(voiceExpenseDataId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get voice expense data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}