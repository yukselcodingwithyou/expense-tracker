package com.expensetracker.data.repository

import com.expensetracker.data.ReportSummaryDTO
import com.expensetracker.data.network.ExportResponse
import com.expensetracker.data.network.ReportApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportApiService: ReportApiService
) {

    suspend fun getReportSummary(startDate: String, endDate: String): Result<ReportSummaryDTO> {
        return try {
            val response = reportApiService.getReportSummary(startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get report summary: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportData(startDate: String, endDate: String, format: String = "CSV"): Result<ExportResponse> {
        return try {
            val response = reportApiService.exportData(startDate, endDate, format)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to export data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}