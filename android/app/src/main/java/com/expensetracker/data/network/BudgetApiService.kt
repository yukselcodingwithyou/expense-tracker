package com.expensetracker.data.network

import com.expensetracker.data.BudgetDTO
import com.expensetracker.data.BudgetSpendDTO
import retrofit2.Response
import retrofit2.http.*

interface BudgetApiService {
    
    @POST("budget")
    suspend fun createBudget(@Body request: BudgetDTO): Response<BudgetResponse>
    
    @GET("budget")
    suspend fun getBudgets(): Response<List<BudgetResponse>>
    
    @GET("budget/{id}")
    suspend fun getBudget(@Path("id") id: String): Response<BudgetResponse>
    
    @PUT("budget/{id}")
    suspend fun updateBudget(
        @Path("id") id: String,
        @Body request: BudgetDTO
    ): Response<BudgetResponse>
    
    @DELETE("budget/{id}")
    suspend fun deleteBudget(@Path("id") id: String): Response<Unit>
    
    @GET("budget/{id}/spending")
    suspend fun getBudgetSpending(
        @Path("id") id: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<BudgetSpendDTO>
}

data class BudgetResponse(
    val id: String,
    val familyId: String,
    val name: String,
    val overallLimitMinor: Long,
    val period: com.expensetracker.data.PeriodDTO,
    val currency: String,
    val includeRecurring: Boolean,
    val alertThresholdPct: Int,
    val perCategory: List<com.expensetracker.data.CategoryBudgetDTO>,
    val createdAt: String,
    val updatedAt: String
)