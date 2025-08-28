import Foundation

struct User: Codable, Identifiable {
    let id: String
    let email: String
}

struct AuthResponse: Codable {
    let accessToken: String
    let refreshToken: String
    let tokenType: String
    let expiresIn: Int
    let user: UserInfo
    
    struct UserInfo: Codable {
        let id: String
        let email: String
    }
}

struct LoginRequest: Codable {
    let email: String
    let password: String
}

struct SignupRequest: Codable {
    let email: String
    let password: String
}

struct Category: Codable, Identifiable {
    let id: String
    let name: String
    let type: CategoryType
    let icon: String?
    let color: String?
    let archived: Bool
    let createdAt: Date
    let updatedAt: Date
    
    enum CategoryType: String, Codable, CaseIterable {
        case expense = "EXPENSE"
        case income = "INCOME"
    }
}

struct LedgerEntry: Codable, Identifiable {
    let id: String
    let memberId: String
    let type: TransactionType
    let amountMinor: Int
    let currency: String
    let categoryId: String
    let categoryName: String?
    let occurredAt: Date
    let notes: String?
    let attachments: [String]?
    let recurringId: String?
    let createdAt: Date
    let updatedAt: Date
    
    enum TransactionType: String, Codable, CaseIterable {
        case expense = "EXPENSE"
        case income = "INCOME"
    }
    
    var amountInDollars: Double {
        return Double(amountMinor) / 100.0
    }
}

struct CreateLedgerEntryRequest: Codable {
    let memberId: String
    let type: LedgerEntry.TransactionType
    let amountMinor: Int
    let currency: String
    let categoryId: String
    let occurredAt: Date
    let notes: String?
    let attachments: [String]?
}

struct Family: Codable, Identifiable {
    let id: String
    let name: String
    let currency: String
    let createdAt: Date
    let updatedAt: Date
}