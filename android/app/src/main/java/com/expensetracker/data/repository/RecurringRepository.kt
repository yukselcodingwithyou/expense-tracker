package com.expensetracker.data.repository

import com.expensetracker.data.RecurringRuleDTO
import com.expensetracker.data.network.RecurringApiService
import com.expensetracker.data.network.RecurringRuleResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringRepository @Inject constructor(
    private val recurringApiService: RecurringApiService
) {

    suspend fun createRecurringRule(rule: RecurringRuleDTO): Result<RecurringRuleResponse> {
        return try {
            val response = recurringApiService.createRecurringRule(rule)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create recurring rule: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecurringRules(): Result<List<RecurringRuleResponse>> {
        return try {
            val response = recurringApiService.getRecurringRules()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get recurring rules: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecurringRule(id: String): Result<RecurringRuleResponse> {
        return try {
            val response = recurringApiService.getRecurringRule(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get recurring rule: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRecurringRule(id: String, rule: RecurringRuleDTO): Result<RecurringRuleResponse> {
        return try {
            val response = recurringApiService.updateRecurringRule(id, rule)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update recurring rule: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecurringRule(id: String): Result<Unit> {
        return try {
            val response = recurringApiService.deleteRecurringRule(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete recurring rule: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}