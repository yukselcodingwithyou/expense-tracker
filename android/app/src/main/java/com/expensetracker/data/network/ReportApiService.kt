package com.expensetracker.data.network

import com.expensetracker.data.ReportSummaryDTO
import retrofit2.Response
import retrofit2.http.*

interface ReportApiService {
    
    @GET("reports/summary")
    suspend fun getReportSummary(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<ReportSummaryDTO>
    
    @GET("reports/export")
    suspend fun exportData(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("format") format: String = "CSV"
    ): Response<ExportResponse>
}

data class ExportResponse(
    val content: String,
    val contentType: String,
    val filename: String
)