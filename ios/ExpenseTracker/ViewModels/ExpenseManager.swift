import Foundation
import Combine

class ExpenseManager: ObservableObject {
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var entries: [LedgerEntry] = []
    
    private let apiService = APIService()
    
    @MainActor
    func createEntry(request: CreateLedgerEntryRequest) async {
        isLoading = true
        errorMessage = nil
        
        do {
            try await apiService.createLedgerEntry(request)
            // Refresh entries after successful creation
            await loadEntries()
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
    
    @MainActor
    func loadEntries() async {
        isLoading = true
        errorMessage = nil
        
        do {
            entries = try await apiService.getLedgerEntries()
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
    
    @MainActor
    func deleteEntry(id: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            try await apiService.deleteLedgerEntry(id: id)
            // Remove from local array
            entries.removeAll { $0.id == id }
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
    
    func clearError() {
        errorMessage = nil
    }
}