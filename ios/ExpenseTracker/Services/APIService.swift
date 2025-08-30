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
    
    func getLedgerEntries() async throws -> [LedgerEntry] {
        return try await performRequest(
            endpoint: "/ledger",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func createLedgerEntry(_ request: CreateLedgerEntryRequest) async throws {
        let _: EmptyResponse = try await performRequest(
            endpoint: "/ledger",
            method: "POST",
            body: request,
            requiresAuth: true
        )
    }
    
    func updateLedgerEntry(id: String, request: CreateLedgerEntryRequest) async throws {
        let _: EmptyResponse = try await performRequest(
            endpoint: "/ledger/\(id)",
            method: "PUT",
            body: request,
            requiresAuth: true
        )
    }
    
    func deleteLedgerEntry(id: String) async throws {
        let _: EmptyResponse = try await performRequest(
            endpoint: "/ledger/\(id)",
            method: "DELETE",
            requiresAuth: true
        )
    }
    
    func createBudget(_ request: CreateBudgetRequest) async throws -> Budget {
        return try await performRequest(
            endpoint: "/budget",
            method: "POST",
            body: request,
            requiresAuth: true
        )
    }
    
    func getBudgets() async throws -> [Budget] {
        return try await performRequest(
            endpoint: "/budget",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func deleteBudget(id: String) async throws {
        let _: EmptyResponse = try await performRequest(
            endpoint: "/budget/\(id)",
            method: "DELETE",
            requiresAuth: true
        )
    }
    
    func getReportSummary(startDate: Date, endDate: Date) async throws -> ReportSummary {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime]
        
        let startDateString = formatter.string(from: startDate)
        let endDateString = formatter.string(from: endDate)
        
        return try await performRequest(
            endpoint: "/reports/summary?startDate=\(startDateString)&endDate=\(endDateString)",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func exportData(startDate: Date, endDate: Date, format: String = "CSV") async throws -> ExportResponse {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime]
        
        let startDateString = formatter.string(from: startDate)
        let endDateString = formatter.string(from: endDate)
        
        return try await performRequest(
            endpoint: "/reports/export?startDate=\(startDateString)&endDate=\(endDateString)&format=\(format)",
            method: "GET",
            requiresAuth: true
        )
    }
    
    // MARK: - Notifications
    func getNotifications() async throws -> [Notification] {
        return try await performRequest(
            endpoint: "/notifications",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func getUnreadNotifications() async throws -> [Notification] {
        return try await performRequest(
            endpoint: "/notifications/unread",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func getUnreadCount() async throws -> UnreadCountResponse {
        return try await performRequest(
            endpoint: "/notifications/unread/count",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func markNotificationAsRead(id: String) async throws {
        let _: EmptyResponse = try await performRequest(
            endpoint: "/notifications/\(id)/read",
            method: "POST",
            requiresAuth: true
        )
    }
    
    // MARK: - File Upload
    func uploadFile(_ fileData: Data, fileName: String, contentType: String, for ledgerEntryId: String) async throws -> Attachment {
        guard let url = URL(string: baseURL + "/files/upload/\(ledgerEntryId)") else {
            throw APIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        if let token = accessToken {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"\(fileName)\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: \(contentType)\r\n\r\n".data(using: .utf8)!)
        body.append(fileData)
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
        
        request.httpBody = body
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard 200...299 ~= httpResponse.statusCode else {
            if httpResponse.statusCode == 401 {
                accessToken = nil
                refreshToken = nil
                throw APIError.unauthorized
            }
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        return try decoder.decode(Attachment.self, from: data)
    }
    
    func getAttachments(for ledgerEntryId: String) async throws -> [Attachment] {
        return try await performRequest(
            endpoint: "/files/ledger/\(ledgerEntryId)",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func deleteAttachment(id: String) async throws {
        let _: EmptyResponse = try await performRequest(
            endpoint: "/files/\(id)",
            method: "DELETE",
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