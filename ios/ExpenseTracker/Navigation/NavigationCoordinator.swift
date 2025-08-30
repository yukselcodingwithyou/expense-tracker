import SwiftUI
import Combine

// MARK: - Navigation Coordinator
class NavigationCoordinator: ObservableObject {
    @Published var currentTab: MainTab = .dashboard
    @Published var navigationPath = NavigationPath()
    @Published var showingAddExpense = false
    @Published var showingSettings = false
    @Published var selectedCategory: Category?
    @Published var selectedBudget: Budget?
    
    // Auth-related navigation
    @Published var isAuthenticated = false
    @Published var showingLogin = false
    @Published var showingWelcome = true
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        // Listen to auth state changes
        NotificationCenter.default
            .publisher(for: .authStateChanged)
            .sink { [weak self] notification in
                if let isAuth = notification.object as? Bool {
                    self?.isAuthenticated = isAuth
                    if isAuth {
                        self?.showingWelcome = false
                        self?.showingLogin = false
                    }
                }
            }
            .store(in: &cancellables)
    }
    
    // MARK: - Navigation Actions
    func navigateToTab(_ tab: MainTab) {
        currentTab = tab
        navigationPath = NavigationPath() // Clear any nested navigation
    }
    
    func showAddExpense() {
        showingAddExpense = true
    }
    
    func hideAddExpense() {
        showingAddExpense = false
    }
    
    func showSettings() {
        showingSettings = true
    }
    
    func hideSettings() {
        showingSettings = false
    }
    
    func showCategoryDetail(_ category: Category) {
        selectedCategory = category
        navigationPath.append(NavigationDestination.categoryDetail(category))
    }
    
    func showBudgetDetail(_ budget: Budget) {
        selectedBudget = budget
        navigationPath.append(NavigationDestination.budgetDetail(budget))
    }
    
    func showExpenseDetail(_ expense: LedgerEntry) {
        navigationPath.append(NavigationDestination.expenseDetail(expense))
    }
    
    func navigateBack() {
        if !navigationPath.isEmpty {
            navigationPath.removeLast()
        }
    }
    
    func navigateToRoot() {
        navigationPath = NavigationPath()
    }
    
    // MARK: - Auth Navigation
    func showLogin() {
        showingLogin = true
        showingWelcome = false
    }
    
    func showWelcome() {
        showingWelcome = true
        showingLogin = false
    }
    
    func handleAuthSuccess() {
        isAuthenticated = true
        showingLogin = false
        showingWelcome = false
        currentTab = .dashboard
    }
    
    func handleLogout() {
        isAuthenticated = false
        showingLogin = true
        showingWelcome = false
        navigationPath = NavigationPath()
        currentTab = .dashboard
    }
}

// MARK: - Main Tab Enum
enum MainTab: String, CaseIterable, Identifiable {
    case dashboard = "Dashboard"
    case expenses = "Expenses"
    case budgets = "Budgets"
    case insights = "Insights"
    
    var id: String { rawValue }
    
    var systemImage: String {
        switch self {
        case .dashboard:
            return "house.fill"
        case .expenses:
            return "list.bullet.rectangle.fill"
        case .budgets:
            return "chart.pie.fill"
        case .insights:
            return "chart.line.uptrend.xyaxis"
        }
    }
}

// MARK: - Navigation Destinations
enum NavigationDestination: Hashable {
    case categoryDetail(Category)
    case budgetDetail(Budget)
    case expenseDetail(LedgerEntry)
    case addExpense
    case editExpense(LedgerEntry)
    case settings
    case profile
    case familySettings
}

// MARK: - Notification Extensions
extension Notification.Name {
    static let authStateChanged = Notification.Name("authStateChanged")
}

// MARK: - Root Navigation View
struct RootNavigationView: View {
    @StateObject private var coordinator = NavigationCoordinator()
    @StateObject private var authManager = AuthManager()
    
    var body: some View {
        Group {
            if coordinator.isAuthenticated {
                MainTabView()
                    .environmentObject(coordinator)
                    .environmentObject(authManager)
            } else if coordinator.showingWelcome {
                WelcomeScreen {
                    coordinator.showLogin()
                }
                .environmentObject(coordinator)
            } else {
                EnhancedLoginView()
                    .environmentObject(coordinator)
                    .environmentObject(authManager)
            }
        }
        .onAppear {
            // Check if user is already authenticated
            coordinator.isAuthenticated = authManager.isAuthenticated
        }
        .sheet(isPresented: $coordinator.showingAddExpense) {
            AddExpenseView()
                .environmentObject(coordinator)
        }
        .sheet(isPresented: $coordinator.showingSettings) {
            SettingsView()
                .environmentObject(coordinator)
                .environmentObject(authManager)
        }
    }
}

// MARK: - Main Tab View
struct MainTabView: View {
    @EnvironmentObject var coordinator: NavigationCoordinator
    
    var body: some View {
        TabView(selection: $coordinator.currentTab) {
            ForEach(MainTab.allCases) { tab in
                NavigationStack(path: $coordinator.navigationPath) {
                    destinationView(for: tab)
                        .navigationDestination(for: NavigationDestination.self) { destination in
                            destinationView(for: destination)
                        }
                }
                .tabItem {
                    Image(systemName: tab.systemImage)
                    Text(tab.rawValue)
                }
                .tag(tab)
            }
        }
        .tint(Theme.Colors.primary)
    }
    
    @ViewBuilder
    private func destinationView(for tab: MainTab) -> some View {
        switch tab {
        case .dashboard:
            DashboardView()
        case .expenses:
            ExpensesView()
        case .budgets:
            BudgetsView()
        case .insights:
            InsightsView()
        }
    }
    
    @ViewBuilder
    private func destinationView(for destination: NavigationDestination) -> some View {
        switch destination {
        case .categoryDetail(let category):
            CategoryDetailView(category: category)
        case .budgetDetail(let budget):
            BudgetDetailView(budget: budget)
        case .expenseDetail(let expense):
            ExpenseDetailView(expense: expense)
        case .addExpense:
            AddExpenseView()
        case .editExpense(let expense):
            EditExpenseView(expense: expense)
        case .settings:
            SettingsView()
        case .profile:
            ProfileView()
        case .familySettings:
            FamilySettingsView()
        }
    }
}

// MARK: - Placeholder Views (These would be implemented separately)
struct DashboardView: View {
    var body: some View {
        Text("Dashboard View")
            .navigationTitle("Dashboard")
    }
}

struct ExpensesView: View {
    var body: some View {
        Text("Expenses View")
            .navigationTitle("Expenses")
    }
}

struct BudgetsView: View {
    var body: some View {
        Text("Budgets View")
            .navigationTitle("Budgets")
    }
}

struct InsightsView: View {
    var body: some View {
        Text("Insights View")
            .navigationTitle("Insights")
    }
}

struct CategoryDetailView: View {
    let category: Category
    
    var body: some View {
        Text("Category: \(category.name)")
            .navigationTitle(category.name)
    }
}

struct BudgetDetailView: View {
    let budget: Budget
    
    var body: some View {
        Text("Budget: \(budget.name)")
            .navigationTitle(budget.name)
    }
}

struct ExpenseDetailView: View {
    let expense: LedgerEntry
    
    var body: some View {
        Text("Expense: \(expense.notes ?? "No description")")
            .navigationTitle("Expense Detail")
    }
}

struct AddExpenseView: View {
    @EnvironmentObject var coordinator: NavigationCoordinator
    
    var body: some View {
        NavigationView {
            Text("Add Expense View")
                .navigationTitle("Add Expense")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button("Cancel") {
                            coordinator.hideAddExpense()
                        }
                    }
                }
        }
    }
}

struct EditExpenseView: View {
    let expense: LedgerEntry
    
    var body: some View {
        Text("Edit Expense: \(expense.notes ?? "No description")")
            .navigationTitle("Edit Expense")
    }
}

struct SettingsView: View {
    @EnvironmentObject var coordinator: NavigationCoordinator
    @EnvironmentObject var authManager: AuthManager
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Settings View")
                
                Button("Logout") {
                    Task {
                        await authManager.logout()
                        coordinator.handleLogout()
                    }
                }
                .foregroundColor(.red)
            }
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Done") {
                        coordinator.hideSettings()
                    }
                }
            }
        }
    }
}

struct ProfileView: View {
    var body: some View {
        Text("Profile View")
            .navigationTitle("Profile")
    }
}

struct FamilySettingsView: View {
    var body: some View {
        Text("Family Settings View")
            .navigationTitle("Family Settings")
    }
}