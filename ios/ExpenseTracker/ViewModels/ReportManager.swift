import Foundation
import Combine

class ReportManager: ObservableObject {
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var reportSummary: ReportSummary?
    
    private let apiService = APIService()
    
    @MainActor
    func generateReport(startDate: Date, endDate: Date) async {
        isLoading = true
        errorMessage = nil
        
        do {
            reportSummary = try await apiService.getReportSummary(startDate: startDate, endDate: endDate)
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
    
    @MainActor
    func exportData(startDate: Date, endDate: Date, format: String = "CSV") async -> String? {
        isLoading = true
        errorMessage = nil
        
        do {
            let exportResponse = try await apiService.exportData(startDate: startDate, endDate: endDate, format: format)
            isLoading = false
            return exportResponse.content
        } catch {
            errorMessage = error.localizedDescription
            isLoading = false
            return nil
        }
    }
    
    func clearError() {
        errorMessage = nil
    }
}