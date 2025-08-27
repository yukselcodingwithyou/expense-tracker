import Foundation

class APIService {
    private let baseURL = "http://localhost:8080/api/v1"
    private let session = URLSession.shared
    
    // TODO: Implement token storage and refresh
    private var accessToken: String?
    
    func login(email: String, password: String) async throws -> AuthResponse {
        let request = LoginRequest(email: email, password: password)
        let response: AuthResponse = try await performRequest(
            endpoint: "/auth/login",
            method: "POST",
            body: request
        )
        self.accessToken = response.accessToken
        return response
    }
    
    func signup(email: String, password: String) async throws -> AuthResponse {
        let request = SignupRequest(email: email, password: password)
        let response: AuthResponse = try await performRequest(
            endpoint: "/auth/signup",
            method: "POST",
            body: request
        )
        self.accessToken = response.accessToken
        return response
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
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        
        return try decoder.decode(R.self, from: data)
    }
}

enum APIError: Error, LocalizedError {
    case invalidURL
    case invalidResponse
    case serverError(Int)
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid URL"
        case .invalidResponse:
            return "Invalid response"
        case .serverError(let code):
            return "Server error: \(code)"
        }
    }
}