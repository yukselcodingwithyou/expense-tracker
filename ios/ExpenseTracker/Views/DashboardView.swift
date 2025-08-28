import SwiftUI

struct DashboardView: View {
    private let demoData = DemoData.shared
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: Theme.Spacing.lg) {
                    // Main stat tiles
                    VStack(spacing: Theme.Spacing.md) {
                        HStack(spacing: Theme.Spacing.md) {
                            StatTileView(
                                title: "Total Income",
                                amount: demoData.totalIncome.formatAsCurrency(),
                                color: Theme.Colors.income
                            )
                            
                            StatTileView(
                                title: "Total Expenses", 
                                amount: demoData.totalExpenses.formatAsCurrency(),
                                color: Theme.Colors.expense
                            )
                        }
                        
                        StatTileView(
                            title: "Balance",
                            amount: demoData.balance.formatAsCurrency(),
                            color: Theme.Colors.success
                        )
                    }
                    
                    // Expense Breakdown
                    PastelCard {
                        VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                            Text("Expense Breakdown")
                                .font(Theme.Typography.titleM)
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            // Simple bar chart placeholder
                            VStack(spacing: Theme.Spacing.sm) {
                                ForEach(demoData.categories.filter(\.isExpense).prefix(4)) { category in
                                    HStack {
                                        Image(systemName: category.icon)
                                            .foregroundColor(Theme.Colors.primary)
                                            .frame(width: 20)
                                        
                                        Text(category.name)
                                            .font(Theme.Typography.body)
                                            .foregroundColor(Theme.Colors.onSurface)
                                        
                                        Spacer()
                                        
                                        Text("$\(Int.random(in: 200...800))")
                                            .font(Theme.Typography.label)
                                            .foregroundColor(Theme.Colors.onSurface)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Savings Goals Preview
                    PastelCard {
                        VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                            HStack {
                                Text("Savings Goals")
                                    .font(Theme.Typography.titleM)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                Spacer()
                                
                                NavigationLink("View All") {
                                    SavingsGoalsView()
                                }
                                .font(Theme.Typography.caption)
                                .foregroundColor(Theme.Colors.primary)
                            }
                            
                            ForEach(demoData.savingsGoals.prefix(2)) { goal in
                                VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                                    HStack {
                                        Text(goal.name)
                                            .font(Theme.Typography.body)
                                            .foregroundColor(Theme.Colors.onSurface)
                                        
                                        Spacer()
                                        
                                        Text(goal.progressPercent)
                                            .font(Theme.Typography.caption)
                                            .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                                    }
                                    
                                    Text("\(goal.currentAmount.formatAsCurrency()) / \(goal.targetAmount.formatAsCurrency())")
                                        .font(Theme.Typography.caption)
                                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                                    
                                    ProgressBarWithLabel(progress: goal.progress, label: goal.progressPercent)
                                }
                                .padding(.vertical, Theme.Spacing.xs)
                            }
                        }
                    }
                    
                    // Recent Transactions
                    PastelCard {
                        VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                            Text("Recent Transactions")
                                .font(Theme.Typography.titleM)
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            ForEach(demoData.transactions.prefix(5)) { transaction in
                                ListRow(
                                    leadingIcon: "creditcard",
                                    title: transaction.title,
                                    subtitle: "\(transaction.category) • \(transaction.date.formatAsTime())",
                                    rightValue: "\(transaction.isExpense ? "-" : "+")\(transaction.amount.formatAsCurrency())",
                                    showChevron: false
                                )
                            }
                        }
                    }
                }
                .padding(Theme.Spacing.lg)
            }
            .background(Theme.Colors.surface)
            .navigationTitle("Dashboard")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    NavigationLink(destination: AddExpenseView()) {
                        Image(systemName: "plus")
                            .foregroundColor(Theme.Colors.primary)
                    }
                }
            }
        }
    }
}

struct BalanceCard: View {
    let balance: Double
    let income: Double
    let expenses: Double
    
    var body: some View {
        VStack(spacing: 16) {
            Text("Current Balance")
                .font(.title2)
                .foregroundColor(.secondary)
            
            Text("$\(balance, specifier: "%.2f")")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)
            
            HStack(spacing: 40) {
                BalanceItem(
                    title: "Income",
                    amount: income,
                    color: .green
                )
                
                BalanceItem(
                    title: "Expenses",
                    amount: expenses,
                    color: .red
                )
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}

struct BalanceItem: View {
    let title: String
    let amount: Double
    let color: Color
    
    var body: some View {
        VStack {
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
            
            Text("$\(amount, specifier: "%.2f")")
                .font(.title3)
                .fontWeight(.semibold)
                .foregroundColor(color)
        }
    }
}

struct TransactionRow: View {
    let title: String
    let amount: Double
    let isExpense: Bool
    let category: String
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                
                Text(category)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Text("\(isExpense ? "-" : "+")$\(amount, specifier: "%.2f")")
                .font(.headline)
                .fontWeight(.semibold)
                .foregroundColor(isExpense ? .red : .green)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(8)
    }
}

struct DashboardView_Previews: PreviewProvider {
    static var previews: some View {
        DashboardView()
    }
}