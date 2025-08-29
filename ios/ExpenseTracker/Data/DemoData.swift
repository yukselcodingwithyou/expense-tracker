import Foundation

// MARK: - Data Models

struct Transaction: Identifiable {
    let id = UUID()
    let title: String
    let amount: Double
    let isExpense: Bool
    let category: String
    let date: Date
    let familyMember: String
}

struct Category: Identifiable {
    let id = UUID()
    let name: String
    let icon: String
    let color: String
    let isExpense: Bool
}

struct SavingsGoal: Identifiable {
    let id = UUID()
    let name: String
    let currentAmount: Double
    let targetAmount: Double
    
    var progress: Double {
        return currentAmount / targetAmount
    }
    
    var progressPercent: String {
        return "\(Int(progress * 100))%"
    }
}

struct FamilyMember: Identifiable {
    let id = UUID()
    let name: String
    let role: String
    let avatar: String
}

struct BudgetCategory: Identifiable {
    let id = UUID()
    let name: String
    let limit: Double
    let spent: Double
    
    var progress: Double {
        return spent / limit
    }
}

struct RecurringExpense: Identifiable {
    let id = UUID()
    let name: String
    let amount: Double
    let category: String
    let frequency: String
    let nextDate: Date
}

// MARK: - Backend DTO Models

struct LedgerCreateDTO: Codable {
    let type: String // "EXPENSE" or "INCOME"
    let amountMinor: Int64
    let currency: String
    let categoryId: String
    let memberId: String
    let occurredAt: String // ISO 8601 timestamp
    let notes: String?
}

struct RecurringRuleDTO: Codable {
    let id: String?
    let familyId: String
    let name: String
    let type: String // "EXPENSE" or "INCOME"
    let amountMinor: Int64
    let currency: String
    let categoryId: String
    let memberId: String
    let frequency: FrequencyDTO
    let startDate: String // ISO date
    let endDate: String?
    let timezone: String
    let nextRunAt: String?
    let isPaused: Bool
}

struct FrequencyDTO: Codable {
    let unit: String // "WEEKLY", "MONTHLY", "YEARLY"
    let interval: Int
    let byMonthDay: [Int]?
}

struct BudgetDTO: Codable {
    let id: String?
    let name: String
    let period: PeriodDTO
    let overallLimitMinor: Int64
    let includeRecurring: Bool
    let alertThresholdPct: Int
    let perCategory: [CategoryBudgetDTO]
}

struct PeriodDTO: Codable {
    let type: String // "MONTH", "QUARTER", "YEAR", "CUSTOM"
    let start: String // ISO date
    let end: String // ISO date
}

struct CategoryBudgetDTO: Codable {
    let categoryId: String
    let limitMinor: Int64
}

struct BudgetSpendDTO: Codable {
    let period: PeriodDTO
    let overall: SpendingSummaryDTO
    let byCategory: [CategorySpendingDTO]
}

struct SpendingSummaryDTO: Codable {
    let limitMinor: Int64
    let spentMinor: Int64
}

struct CategorySpendingDTO: Codable {
    let categoryId: String
    let limitMinor: Int64
    let spentMinor: Int64
}

struct ReportSummaryDTO: Codable {
    let totalIncomeMinor: Int64
    let totalExpensesMinor: Int64
    let balanceMinor: Int64
    let perCategory: [CategorySummaryDTO]
    let perMonth: [MonthlySummaryDTO]
    let recentTransactions: [TransactionSummaryDTO]
}

struct CategorySummaryDTO: Codable {
    let categoryId: String
    let spentMinor: Int64
}

struct MonthlySummaryDTO: Codable {
    let month: String // "2025-10"
    let incomeMinor: Int64
    let expenseMinor: Int64
}

struct TransactionSummaryDTO: Codable {
    let id: String
    let categoryId: String
    let amountMinor: Int64
}

struct ExportResponse: Codable {
    let exportId: String
}

// MARK: - Demo Data

class DemoData {
    static let shared = DemoData()
    
    private init() {}
    
    let totalIncome: Double = 12500.00
    let totalExpenses: Double = 8200.00
    
    var balance: Double {
        return totalIncome - totalExpenses
    }
    
    let transactions = [
        Transaction(title: "Salary", amount: 5000.00, isExpense: false, category: "Income", date: Date(), familyMember: "John"),
        Transaction(title: "Grocery Shopping", amount: 156.78, isExpense: true, category: "Groceries", date: Date(), familyMember: "Sarah"),
        Transaction(title: "Gas Station", amount: 45.50, isExpense: true, category: "Transportation", date: Date(), familyMember: "John"),
        Transaction(title: "Restaurant", amount: 89.32, isExpense: true, category: "Dining", date: Date(), familyMember: "Sarah"),
        Transaction(title: "Freelance Work", amount: 800.00, isExpense: false, category: "Income", date: Date(), familyMember: "John"),
    ]
    
    let categories = [
        Category(name: "Groceries", icon: "cart", color: "accentSuccess", isExpense: true),
        Category(name: "Transportation", icon: "car", color: "accentWarning", isExpense: true),
        Category(name: "Dining", icon: "fork.knife", color: "accentInfo", isExpense: true),
        Category(name: "Entertainment", icon: "tv", color: "primary", isExpense: true),
        Category(name: "Utilities", icon: "house", color: "expense", isExpense: true),
        Category(name: "Salary", icon: "dollarsign.circle", color: "income", isExpense: false),
        Category(name: "Freelance", icon: "briefcase", color: "income", isExpense: false),
    ]
    
    let savingsGoals = [
        SavingsGoal(name: "Emergency Fund", currentAmount: 2500.00, targetAmount: 5000.00),
        SavingsGoal(name: "Vacation", currentAmount: 1200.00, targetAmount: 3000.00),
        SavingsGoal(name: "New Car", currentAmount: 4500.00, targetAmount: 6000.00),
        SavingsGoal(name: "Home Renovation", currentAmount: 900.00, targetAmount: 3000.00),
    ]
    
    let familyMembers = [
        FamilyMember(name: "John Smith", role: "Admin", avatar: "person.circle.fill"),
        FamilyMember(name: "Sarah Smith", role: "Member", avatar: "person.circle.fill"),
        FamilyMember(name: "Emma Smith", role: "Member", avatar: "person.circle.fill"),
    ]
    
    let budgetCategories = [
        BudgetCategory(name: "Groceries", limit: 500.00, spent: 325.00),
        BudgetCategory(name: "Transportation", limit: 300.00, spent: 245.00),
        BudgetCategory(name: "Dining", limit: 200.00, spent: 89.32),
        BudgetCategory(name: "Entertainment", limit: 150.00, spent: 67.50),
    ]
    
    let recurringExpenses = [
        RecurringExpense(name: "Netflix", amount: 15.99, category: "Entertainment", frequency: "Monthly", nextDate: Calendar.current.date(byAdding: .day, value: 5, to: Date()) ?? Date()),
        RecurringExpense(name: "Gym Membership", amount: 29.99, category: "Health", frequency: "Monthly", nextDate: Calendar.current.date(byAdding: .day, value: 12, to: Date()) ?? Date()),
        RecurringExpense(name: "Phone Bill", amount: 89.00, category: "Utilities", frequency: "Monthly", nextDate: Calendar.current.date(byAdding: .day, value: 8, to: Date()) ?? Date()),
    ]
}