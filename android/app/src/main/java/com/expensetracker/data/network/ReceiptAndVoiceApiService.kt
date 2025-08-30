package com.expensetracker.data.network

import com.expensetracker.data.model.*
import com.expensetracker.dto.ledger.LedgerEntryResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ReceiptApiService {
    
    @Multipart
    @POST("receipts/process")
    suspend fun processReceipt(
        @Part file: MultipartBody.Part
    ): Response<ReceiptData>
    
    @POST("receipts/create-expense")
    suspend fun createExpenseFromReceipt(
        @Body request: CreateExpenseFromReceiptRequest
    ): Response<LedgerEntryResponse>
    
    @GET("receipts")
    suspend fun getUserReceipts(): Response<List<ReceiptData>>
    
    @GET("receipts/{receiptDataId}")
    suspend fun getReceiptData(
        @Path("receiptDataId") receiptDataId: String
    ): Response<ReceiptData>
}

interface VoiceExpenseApiService {
    
    @POST("voice/process")
    suspend fun processVoiceExpense(
        @Body request: VoiceExpenseRequest
    ): Response<VoiceExpenseData>
    
    @POST("voice/create-expense")
    suspend fun createExpenseFromVoice(
        @Body request: CreateExpenseFromVoiceRequest
    ): Response<LedgerEntryResponse>
    
    @GET("voice")
    suspend fun getUserVoiceExpenses(): Response<List<VoiceExpenseData>>
    
    @GET("voice/{voiceExpenseDataId}")
    suspend fun getVoiceExpenseData(
        @Path("voiceExpenseDataId") voiceExpenseDataId: String
    ): Response<VoiceExpenseData>
}