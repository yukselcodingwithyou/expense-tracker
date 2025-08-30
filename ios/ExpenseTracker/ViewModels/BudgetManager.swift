import Foundation
import Combine

class BudgetManager: ObservableObject {
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var budgets: [Budget] = []
    
    private let apiService = APIService()
    
    @MainActor
    func createBudget(_ budget: CreateBudgetRequest) async {
        isLoading = true
        errorMessage = nil
        
        do {
            let newBudget = try await apiService.createBudget(budget)
            budgets.append(newBudget)
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
    
    @MainActor
    func loadBudgets() async {
        isLoading = true
        errorMessage = nil
        
        do {
            budgets = try await apiService.getBudgets()
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
    
    @MainActor
    func deleteBudget(id: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            try await apiService.deleteBudget(id: id)
            budgets.removeAll { $0.id == id }
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
    
    func clearError() {
        errorMessage = nil
    }
}