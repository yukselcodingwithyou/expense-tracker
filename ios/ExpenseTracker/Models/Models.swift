import Foundation

struct User: Codable, Identifiable {
    let id: String
    let email: String
    let families: [FamilyMembership]?
    let preferredFamilyId: String?
    
    init(id: String, email: String, families: [FamilyMembership]? = nil, preferredFamilyId: String? = nil) {
        self.id = id
        self.email = email
        self.families = families
        self.preferredFamilyId = preferredFamilyId
    }
}

struct FamilyMembership: Codable {
    let familyId: String
    let role: String // "ADMIN" or "MEMBER"
}

struct AuthResponse: Codable {
    let accessToken: String
    let refreshToken: String
    let user: User
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

struct Budget: Codable, Identifiable {
    let id: String
    let familyId: String
    let name: String
    let overallLimitMinor: Int64
    let period: BudgetPeriod
    let currency: String
    let includeRecurring: Bool
    let alertThresholdPct: Int
    let perCategory: [CategoryBudget]
    let createdAt: Date
    let updatedAt: Date
}

struct BudgetPeriod: Codable {
    let type: String // "MONTH", "QUARTER", "YEAR", "CUSTOM"
    let start: Date
    let end: Date
}

struct CategoryBudget: Codable {
    let categoryId: String
    let limitMinor: Int64
}

struct CreateBudgetRequest: Codable {
    let name: String
    let overallLimitMinor: Int64
    let period: BudgetPeriod
    let currency: String
    let includeRecurring: Bool
    let alertThresholdPct: Int
    let perCategory: [CategoryBudget]
}

struct ReportSummary: Codable {
    let totalIncomeMinor: Int64
    let totalExpensesMinor: Int64
    let balanceMinor: Int64
    let perCategory: [CategorySummary]
    let perMonth: [MonthlySummary]
    let recentTransactions: [TransactionSummary]
}

struct CategorySummary: Codable {
    let categoryId: String
    let spentMinor: Int64
}

struct MonthlySummary: Codable {
    let month: String // "2025-10"
    let incomeMinor: Int64
    let expenseMinor: Int64
}

struct TransactionSummary: Codable {
    let id: String
    let categoryId: String
    let amountMinor: Int64
}

struct ExportResponse: Codable {
    let content: String
    let contentType: String
    let filename: String
}

// MARK: - Notifications
struct Notification: Codable, Identifiable {
    let id: String
    let userId: String
    let familyId: String
    let type: NotificationType
    let title: String
    let message: String
    let data: [String: String]?
    let isRead: Bool
    let emailSent: Bool
    let createdAt: Date
    
    enum NotificationType: String, Codable {
        case budgetAlert = "BUDGET_ALERT"
        case budgetExceeded = "BUDGET_EXCEEDED"
        case weeklySummary = "WEEKLY_SUMMARY"
        case monthlyReport = "MONTHLY_REPORT"
    }
}

struct UnreadCountResponse: Codable {
    let count: Int64
}

// MARK: - File Attachments
struct Attachment: Codable, Identifiable {
    let id: String
    let ledgerEntryId: String
    let filename: String
    let originalFilename: String
    let contentType: String
    let size: Int64
    let storageKey: String
    let uploadedAt: Date
}
}