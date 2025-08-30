package com.expensetracker.data.network

import com.expensetracker.data.LedgerCreateDTO
import retrofit2.Response
import retrofit2.http.*

interface LedgerApiService {
    
    @POST("ledger")
    suspend fun createLedgerEntry(@Body request: LedgerCreateDTO): Response<Unit>
    
    @GET("ledger")
    suspend fun getLedgerEntries(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<LedgerEntry>>
    
    @GET("ledger/{id}")
    suspend fun getLedgerEntry(@Path("id") id: String): Response<LedgerEntry>
    
    @PUT("ledger/{id}")
    suspend fun updateLedgerEntry(
        @Path("id") id: String,
        @Body request: LedgerCreateDTO
    ): Response<Unit>
    
    @DELETE("ledger/{id}")
    suspend fun deleteLedgerEntry(@Path("id") id: String): Response<Unit>
}

data class LedgerEntry(
    val id: String,
    val familyId: String,
    val memberId: String,
    val type: String,
    val amountMinor: Long,
    val currency: String,
    val categoryId: String,
    val notes: String?,
    val occurredAt: String,
    val createdAt: String
)