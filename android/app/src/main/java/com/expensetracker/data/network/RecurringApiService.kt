package com.expensetracker.data.network

import com.expensetracker.data.RecurringRuleDTO
import retrofit2.Response
import retrofit2.http.*

interface RecurringApiService {
    
    @POST("recurring")
    suspend fun createRecurringRule(@Body request: RecurringRuleDTO): Response<RecurringRuleResponse>
    
    @GET("recurring")
    suspend fun getRecurringRules(): Response<List<RecurringRuleResponse>>
    
    @GET("recurring/{id}")
    suspend fun getRecurringRule(@Path("id") id: String): Response<RecurringRuleResponse>
    
    @PUT("recurring/{id}")
    suspend fun updateRecurringRule(
        @Path("id") id: String,
        @Body request: RecurringRuleDTO
    ): Response<RecurringRuleResponse>
    
    @DELETE("recurring/{id}")
    suspend fun deleteRecurringRule(@Path("id") id: String): Response<Unit>
}

data class RecurringRuleResponse(
    val id: String,
    val familyId: String,
    val name: String,
    val type: String,
    val amountMinor: Long,
    val currency: String,
    val categoryId: String,
    val memberId: String,
    val frequency: com.expensetracker.data.FrequencyDTO,
    val startDate: String,
    val endDate: String?,
    val timezone: String,
    val nextRunAt: String?,
    val isPaused: Boolean
)