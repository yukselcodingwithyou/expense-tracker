package com.expensetracker.data.repository

import com.expensetracker.data.BudgetDTO
import com.expensetracker.data.BudgetSpendDTO
import com.expensetracker.data.network.BudgetApiService
import com.expensetracker.data.network.BudgetResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetApiService: BudgetApiService
) {

    suspend fun createBudget(budget: BudgetDTO): Result<BudgetResponse> {
        return try {
            val response = budgetApiService.createBudget(budget)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create budget: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBudgets(): Result<List<BudgetResponse>> {
        return try {
            val response = budgetApiService.getBudgets()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get budgets: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBudget(id: String): Result<BudgetResponse> {
        return try {
            val response = budgetApiService.getBudget(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get budget: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBudget(id: String, budget: BudgetDTO): Result<BudgetResponse> {
        return try {
            val response = budgetApiService.updateBudget(id, budget)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update budget: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBudget(id: String): Result<Unit> {
        return try {
            val response = budgetApiService.deleteBudget(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete budget: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBudgetSpending(id: String, startDate: String? = null, endDate: String? = null): Result<BudgetSpendDTO> {
        return try {
            val response = budgetApiService.getBudgetSpending(id, startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get budget spending: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}