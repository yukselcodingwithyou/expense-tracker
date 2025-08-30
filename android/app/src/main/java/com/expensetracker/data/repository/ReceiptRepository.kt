package com.expensetracker.data.repository

import com.expensetracker.data.model.*
import com.expensetracker.data.network.ReceiptApiService
import com.expensetracker.dto.ledger.LedgerEntryResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRepository @Inject constructor(
    private val receiptApiService: ReceiptApiService
) {
    
    suspend fun processReceiptImage(imageFile: File): Result<ReceiptData> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            
            val response = receiptApiService.processReceipt(filePart)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to process receipt: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createExpenseFromReceipt(request: CreateExpenseFromReceiptRequest): Result<LedgerEntryResponse> {
        return try {
            val response = receiptApiService.createExpenseFromReceipt(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create expense: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserReceipts(): Result<List<ReceiptData>> {
        return try {
            val response = receiptApiService.getUserReceipts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get receipts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getReceiptData(receiptDataId: String): Result<ReceiptData> {
        return try {
            val response = receiptApiService.getReceiptData(receiptDataId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get receipt data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}