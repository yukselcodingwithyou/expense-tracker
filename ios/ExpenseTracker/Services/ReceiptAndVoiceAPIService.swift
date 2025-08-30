import Foundation
import UIKit

class ReceiptAPIService {
    private let baseURL: String
    private let session: URLSession
    
    init(baseURL: String = "http://localhost:8080/api/v1", session: URLSession = .shared) {
        self.baseURL = baseURL
        self.session = session
    }
    
    func processReceipt(image: UIImage) async throws -> ReceiptData {
        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            throw APIError.invalidImage
        }
        
        let url = URL(string: "\(baseURL)/receipts/process")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        // Create multipart form data
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        let body = createMultipartBody(imageData: imageData, boundary: boundary)
        request.httpBody = body
        
        // Add authorization header if available
        if let token = await KeychainHelper.shared.getAccessToken() {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard httpResponse.statusCode == 200 || httpResponse.statusCode == 201 else {
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let receiptData = try JSONDecoder().decode(ReceiptData.self, from: data)
        return receiptData
    }
    
    func createExpenseFromReceipt(_ request: CreateExpenseFromReceiptRequest) async throws -> LedgerEntryResponse {
        let url = URL(string: "\(baseURL)/receipts/create-expense")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        // Add authorization header if available
        if let token = await KeychainHelper.shared.getAccessToken() {
            urlRequest.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let jsonData = try JSONEncoder().encode(request)
        urlRequest.httpBody = jsonData
        
        let (data, response) = try await session.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard httpResponse.statusCode == 200 || httpResponse.statusCode == 201 else {
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let ledgerEntry = try JSONDecoder().decode(LedgerEntryResponse.self, from: data)
        return ledgerEntry
    }
    
    func getUserReceipts() async throws -> [ReceiptData] {
        let url = URL(string: "\(baseURL)/receipts")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        // Add authorization header if available
        if let token = await KeychainHelper.shared.getAccessToken() {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard httpResponse.statusCode == 200 else {
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let receipts = try JSONDecoder().decode([ReceiptData].self, from: data)
        return receipts
    }
    
    private func createMultipartBody(imageData: Data, boundary: String) -> Data {
        var body = Data()
        
        // Add file field
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"receipt.jpg\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
        body.append(imageData)
        body.append("\r\n".data(using: .utf8)!)
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        
        return body
    }
}

class VoiceExpenseAPIService {
    private let baseURL: String
    private let session: URLSession
    
    init(baseURL: String = "http://localhost:8080/api/v1", session: URLSession = .shared) {
        self.baseURL = baseURL
        self.session = session
    }
    
    func processVoiceExpense(_ request: VoiceExpenseRequest) async throws -> VoiceExpenseData {
        let url = URL(string: "\(baseURL)/voice/process")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        // Add authorization header if available
        if let token = await KeychainHelper.shared.getAccessToken() {
            urlRequest.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let jsonData = try JSONEncoder().encode(request)
        urlRequest.httpBody = jsonData
        
        let (data, response) = try await session.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard httpResponse.statusCode == 200 || httpResponse.statusCode == 201 else {
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let voiceExpenseData = try JSONDecoder().decode(VoiceExpenseData.self, from: data)
        return voiceExpenseData
    }
    
    func createExpenseFromVoice(_ request: CreateExpenseFromVoiceRequest) async throws -> LedgerEntryResponse {
        let url = URL(string: "\(baseURL)/voice/create-expense")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        // Add authorization header if available
        if let token = await KeychainHelper.shared.getAccessToken() {
            urlRequest.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let jsonData = try JSONEncoder().encode(request)
        urlRequest.httpBody = jsonData
        
        let (data, response) = try await session.data(for: urlRequest)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard httpResponse.statusCode == 200 || httpResponse.statusCode == 201 else {
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let ledgerEntry = try JSONDecoder().decode(LedgerEntryResponse.self, from: data)
        return ledgerEntry
    }
    
    func getUserVoiceExpenses() async throws -> [VoiceExpenseData] {
        let url = URL(string: "\(baseURL)/voice")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        // Add authorization header if available
        if let token = await KeychainHelper.shared.getAccessToken() {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard httpResponse.statusCode == 200 else {
            throw APIError.serverError(httpResponse.statusCode)
        }
        
        let voiceExpenses = try JSONDecoder().decode([VoiceExpenseData].self, from: data)
        return voiceExpenses
    }
}

enum APIError: Error, LocalizedError {
    case invalidImage
    case invalidResponse
    case serverError(Int)
    case decodingError
    case encodingError
    
    var errorDescription: String? {
        switch self {
        case .invalidImage:
            return "Invalid image data"
        case .invalidResponse:
            return "Invalid server response"
        case .serverError(let code):
            return "Server error with code: \(code)"
        case .decodingError:
            return "Failed to decode response"
        case .encodingError:
            return "Failed to encode request"
        }
    }
}