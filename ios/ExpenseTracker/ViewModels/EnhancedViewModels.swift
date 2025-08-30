import Foundation
import SwiftUI
import Combine
import CoreData

// MARK: - Enhanced Dashboard ViewModel
class DashboardViewModel: ObservableObject {
    @Published var recentTransactions: [LedgerEntry] = []
    @Published var budgets: [Budget] = []
    @Published var categories: [Category] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var totalExpenses: Double = 0
    @Published var totalIncome: Double = 0
    @Published var currentMonth: String = ""
    
    private let ledgerRepository = LedgerRepository()
    private let budgetRepository = BudgetRepository()
    private let categoryRepository = CategoryRepository()
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        currentMonth = DateFormatter().string(from: Date())
        loadDashboardData()
    }
    
    func loadDashboardData() {
        isLoading = true
        errorMessage = nil
        
        // Assuming we have a current family ID
        let familyId = "current-family-id" // This would come from auth state
        
        // Combine all data loading
        Publishers.CombineLatest3(
            ledgerRepository.getLedgerEntries(familyId: familyId),
            budgetRepository.getBudgets(familyId: familyId),
            categoryRepository.getCategories(familyId: familyId)
        )
        .receive(on: DispatchQueue.main)
        .sink(
            receiveCompletion: { [weak self] completion in
                self?.isLoading = false
                if case .failure(let error) = completion {
                    self?.errorMessage = error.localizedDescription
                }
            },
            receiveValue: { [weak self] entries, budgets, categories in
                self?.recentTransactions = Array(entries.prefix(10))
                self?.budgets = budgets
                self?.categories = categories
                self?.calculateTotals(from: entries)
            }
        )
        .store(in: &cancellables)
    }
    
    private func calculateTotals(from entries: [LedgerEntry]) {
        let currentMonthEntries = entries.filter { entry in
            // Filter for current month - simplified logic
            return true // In real app, filter by date
        }
        
        totalExpenses = currentMonthEntries
            .filter { $0.type == .expense }
            .reduce(0) { $0 + Double($1.amountMinor) / 100.0 }
        
        totalIncome = currentMonthEntries
            .filter { $0.type == .income }
            .reduce(0) { $0 + Double($1.amountMinor) / 100.0 }
    }
    
    func refreshData() {
        loadDashboardData()
    }
}

// MARK: - Enhanced Expenses ViewModel
class ExpensesViewModel: ObservableObject {
    @Published var expenses: [LedgerEntry] = []
    @Published var categories: [Category] = []
    @Published var selectedCategory: Category?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var searchText = ""
    @Published var sortOption = SortOption.date
    
    private let ledgerRepository = LedgerRepository()
    private let categoryRepository = CategoryRepository()
    private var cancellables = Set<AnyCancellable>()
    
    enum SortOption: String, CaseIterable {
        case date = "Date"
        case amount = "Amount"
        case category = "Category"
    }
    
    init() {
        loadData()
        setupSearch()
    }
    
    private func setupSearch() {
        $searchText
            .debounce(for: .milliseconds(500), scheduler: RunLoop.main)
            .sink { [weak self] _ in
                self?.filterExpenses()
            }
            .store(in: &cancellables)
    }
    
    func loadData() {
        isLoading = true
        errorMessage = nil
        
        let familyId = "current-family-id"
        
        Publishers.CombineLatest(
            ledgerRepository.getLedgerEntries(familyId: familyId),
            categoryRepository.getCategories(familyId: familyId)
        )
        .receive(on: DispatchQueue.main)
        .sink(
            receiveCompletion: { [weak self] completion in
                self?.isLoading = false
                if case .failure(let error) = completion {
                    self?.errorMessage = error.localizedDescription
                }
            },
            receiveValue: { [weak self] entries, categories in
                self?.expenses = entries
                self?.categories = categories
                self?.filterExpenses()
            }
        )
        .store(in: &cancellables)
    }
    
    private func filterExpenses() {
        // Implementation would filter and sort expenses based on searchText and sortOption
        // For now, just keep the logic simple
    }
    
    func addExpense(amount: Double, categoryId: String, notes: String?) async {
        do {
            let request = CreateLedgerEntryRequest(
                familyId: "current-family-id",
                memberId: "current-member-id",
                type: .expense,
                amountMinor: Int(amount * 100),
                currency: "USD",
                categoryId: categoryId,
                occurredAt: ISO8601DateFormatter().string(from: Date()),
                notes: notes
            )
            
            let _ = try await ledgerRepository.createEntry(request)
            // Success - data will auto-refresh through reactive streams
        } catch {
            DispatchQueue.main.async {
                self.errorMessage = error.localizedDescription
            }
        }
    }
}

// MARK: - Enhanced Budgets ViewModel
class BudgetsViewModel: ObservableObject {
    @Published var budgets: [Budget] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var showingAddBudget = false
    
    private let budgetRepository = BudgetRepository()
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        loadBudgets()
    }
    
    func loadBudgets() {
        isLoading = true
        errorMessage = nil
        
        let familyId = "current-family-id"
        
        budgetRepository.getBudgets(familyId: familyId)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { [weak self] completion in
                    self?.isLoading = false
                    if case .failure(let error) = completion {
                        self?.errorMessage = error.localizedDescription
                    }
                },
                receiveValue: { [weak self] budgets in
                    self?.budgets = budgets
                }
            )
            .store(in: &cancellables)
    }
    
    func addBudget(name: String, limit: Double, periodType: BudgetPeriodType) async {
        do {
            let request = CreateBudgetRequest(
                familyId: "current-family-id",
                name: name,
                overallLimitMinor: Int(limit * 100),
                periodType: periodType,
                periodStart: ISO8601DateFormatter().string(from: Date()),
                periodEnd: ISO8601DateFormatter().string(from: Calendar.current.date(byAdding: .month, value: 1, to: Date()) ?? Date()),
                alertThresholdPct: 0.8,
                includeRecurring: true
            )
            
            let _ = try await budgetRepository.createBudget(request)
            // Success - data will auto-refresh
        } catch {
            DispatchQueue.main.async {
                self.errorMessage = error.localizedDescription
            }
        }
    }
    
    func deleteBudget(_ budget: Budget) async {
        do {
            try await budgetRepository.deleteBudget(budget.id)
            // Success - data will auto-refresh
        } catch {
            DispatchQueue.main.async {
                self.errorMessage = error.localizedDescription
            }
        }
    }
}

// MARK: - Enhanced Auth Manager Extension
extension AuthManager {
    func handleAuthenticationStateChange() {
        // Post notification for navigation coordinator
        NotificationCenter.default.post(
            name: .authStateChanged,
            object: isAuthenticated
        )
    }
}

// MARK: - Enhanced Views using the ViewModels
struct EnhancedDashboardView: View {
    @StateObject private var viewModel = DashboardViewModel()
    @EnvironmentObject var coordinator: NavigationCoordinator
    
    var body: some View {
        ScrollView {
            VStack(spacing: Theme.Spacing.lg) {
                // Summary Cards
                HStack(spacing: Theme.Spacing.md) {
                    SummaryCard(
                        title: "Total Income",
                        amount: viewModel.totalIncome,
                        color: .green
                    )
                    
                    SummaryCard(
                        title: "Total Expenses", 
                        amount: viewModel.totalExpenses,
                        color: .red
                    )
                }
                
                // Recent Transactions
                VStack(alignment: .leading, spacing: Theme.Spacing.sm) {
                    HStack {
                        Text("Recent Transactions")
                            .font(Theme.Typography.headline)
                        
                        Spacer()
                        
                        Button("See All") {
                            coordinator.navigateToTab(.expenses)
                        }
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.primary)
                    }
                    
                    ForEach(Array(viewModel.recentTransactions.prefix(5)), id: \.id) { transaction in
                        TransactionRow(transaction: transaction)
                            .onTapGesture {
                                coordinator.showExpenseDetail(transaction)
                            }
                    }
                }
                
                // Budget Overview
                if !viewModel.budgets.isEmpty {
                    VStack(alignment: .leading, spacing: Theme.Spacing.sm) {
                        Text("Budget Overview")
                            .font(Theme.Typography.headline)
                        
                        ForEach(Array(viewModel.budgets.prefix(3)), id: \.id) { budget in
                            BudgetRow(budget: budget)
                                .onTapGesture {
                                    coordinator.showBudgetDetail(budget)
                                }
                        }
                    }
                }
            }
            .padding(Theme.Spacing.lg)
        }
        .navigationTitle("Dashboard")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    coordinator.showSettings()
                } label: {
                    Image(systemName: "gearshape")
                }
            }
        }
        .onAppear {
            viewModel.loadDashboardData()
        }
        .refreshable {
            viewModel.refreshData()
        }
    }
}

// MARK: - Helper Views
struct SummaryCard: View {
    let title: String
    let amount: Double
    let color: Color
    
    var body: some View {
        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
            Text(title)
                .font(Theme.Typography.caption)
                .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
            
            Text("$\(amount, specifier: "%.2f")")
                .font(Theme.Typography.title2)
                .foregroundColor(color)
        }
        .padding(Theme.Spacing.md)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                .fill(Theme.Colors.surface)
                .shadow(color: Theme.Colors.onSurface.opacity(0.1), radius: 2, x: 0, y: 1)
        )
    }
}

struct TransactionRow: View {
    let transaction: LedgerEntry
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                Text(transaction.notes ?? "Transaction")
                    .font(Theme.Typography.body)
                
                Text("Category • \(transaction.occurredAt)")
                    .font(Theme.Typography.caption)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.6))
            }
            
            Spacer()
            
            Text("$\(Double(transaction.amountMinor) / 100.0, specifier: "%.2f")")
                .font(Theme.Typography.label)
                .foregroundColor(transaction.type == .expense ? .red : .green)
        }
        .padding(Theme.Spacing.sm)
        .background(
            RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                .fill(Theme.Colors.surface)
        )
    }
}

struct BudgetRow: View {
    let budget: Budget
    
    var body: some View {
        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
            HStack {
                Text(budget.name)
                    .font(Theme.Typography.body)
                
                Spacer()
                
                Text("$\(Double(budget.overallLimitMinor) / 100.0, specifier: "%.0f")")
                    .font(Theme.Typography.label)
            }
            
            ProgressView(value: 0.6) // This would be calculated based on actual spending
                .progressViewStyle(LinearProgressViewStyle(tint: Theme.Colors.primary))
        }
        .padding(Theme.Spacing.sm)
        .background(
            RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                .fill(Theme.Colors.surface)
        )
    }
}