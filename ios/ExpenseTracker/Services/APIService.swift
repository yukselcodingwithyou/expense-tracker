import Foundation

class APIService {
    private let baseURL = "http://localhost:8080/api/v1"
    private let session = URLSession.shared
    private let keychainService = "com.expensetracker.tokens"
    
    private var accessToken: String? {
        get {
            KeychainHelper.shared.loadString(service: keychainService, key: "accessToken")
        }
        set {
            if let token = newValue {
                KeychainHelper.shared.save(token, service: keychainService, key: "accessToken")
            } else {
                KeychainHelper.shared.delete(service: keychainService, key: "accessToken")
            }
        }
    }
    
    private var refreshToken: String? {
        get {
            KeychainHelper.shared.loadString(service: keychainService, key: "refreshToken")
        }
        set {
            if let token = newValue {
                KeychainHelper.shared.save(token, service: keychainService, key: "refreshToken")
            } else {
                KeychainHelper.shared.delete(service: keychainService, key: "refreshToken")
            }
        }
    }
    
    func login(email: String, password: String) async throws -> AuthResponse {
        let request = LoginRequest(email: email, password: password)
        let response: AuthResponse = try await performRequest(
            endpoint: "/auth/login",
            method: "POST",
            body: request
        )
        
        // Store tokens securely in Keychain
        self.accessToken = response.accessToken
        self.refreshToken = response.refreshToken
        
        return response
    }
    
    func signup(email: String, password: String) async throws -> AuthResponse {
        let request = SignupRequest(email: email, password: password)
        let response: AuthResponse = try await performRequest(
            endpoint: "/auth/signup",
            method: "POST",
            body: request
        )
        
        // Store tokens securely in Keychain
        self.accessToken = response.accessToken
        self.refreshToken = response.refreshToken
        
        return response
    }
    
    func logout() async throws {
        // Call logout API to blacklist token on server
        if accessToken != nil {
            try? await performRequest<EmptyBody, EmptyResponse>(
                endpoint: "/auth/logout",
                method: "POST",
                requiresAuth: true
            )
        }
        
        // Clear tokens from Keychain
        self.accessToken = nil
        self.refreshToken = nil
    }
    
    func isAuthenticated() -> Bool {
        return accessToken != nil
    }
    
    func getCategories() async throws -> [Category] {
        return try await performRequest(
            endpoint: "/categories",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func getRecentTransactions() async throws -> [LedgerEntry] {
        return try await performRequest(
            endpoint: "/ledger/recent",
            method: "GET",
            requiresAuth: true
        )
    }
    
    private func performRequest<T: Codable, R: Codable>(
        endpoint: String,
        method: String,
        body: T? = nil,
        requiresAuth: Bool = false
    ) async throws -> R {
        guard let url = URL(string: baseURL + endpoint) else {
            throw APIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if requiresAuth, let token = accessToken {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        if let body = body {
            request.httpBody = try JSONEncoder().encode(body)
        }
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard 200...299 ~= httpResponse.statusCode else {
            if httpResponse.statusCode == 401 {
                // Token might be expired, clear it
                accessToken = nil
                refreshToken = nil
                throw APIError.unauthorized
            }
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        
        // Handle empty responses
        if data.isEmpty {
            return EmptyResponse() as! R
        }
        
        return try decoder.decode(R.self, from: data)
    }
}

// Helper structures for empty requests/responses
struct EmptyBody: Codable {}
struct EmptyResponse: Codable {}

enum APIError: Error, LocalizedError {
    case invalidURL
    case invalidResponse
    case unauthorized
    case serverError(Int)
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid URL"
        case .invalidResponse:
            return "Invalid response"
        case .unauthorized:
            return "Unauthorized - please log in again"
        case .serverError(let code):
            return "Server error: \(code)"
        }
    }
}