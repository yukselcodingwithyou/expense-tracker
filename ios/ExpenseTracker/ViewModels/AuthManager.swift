import Foundation
import Combine

class AuthManager: ObservableObject {
    @Published var isAuthenticated = false
    @Published var user: User?
    @Published var errorMessage: String?
    @Published var isLoading = false
    
    private let apiService = APIService()
    
    init() {
        // Check if user is already authenticated
        isAuthenticated = apiService.isAuthenticated()
    }
    
    @MainActor
    func login(email: String, password: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            let response = try await apiService.login(email: email, password: password)
            user = response.user
            isAuthenticated = true
        } catch {
            errorMessage = error.localizedDescription
            isAuthenticated = false
        }
        
        isLoading = false
    }
    
    @MainActor
    func signup(email: String, password: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            let response = try await apiService.signup(email: email, password: password)
            user = response.user
            isAuthenticated = true
        } catch {
            errorMessage = error.localizedDescription
            isAuthenticated = false
        }
        
        isLoading = false
    }
    
    @MainActor
    func logout() async {
        isLoading = true
        
        do {
            try await apiService.logout()
        } catch {
            // Even if logout API fails, clear local state
            print("Logout API failed: \(error.localizedDescription)")
        }
        
        isAuthenticated = false
        user = nil
        errorMessage = nil
        isLoading = false
    }
    
    func clearError() {
        errorMessage = nil
    }
}