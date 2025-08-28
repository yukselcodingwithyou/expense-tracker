import SwiftUI

struct ReportsView: View {
    @State private var selectedTab = 0
    @State private var selectedDateRange = "This Month"
    @State private var selectedCategory = "All Categories"
    @State private var selectedMember = "All Members"
    
    private let tabs = ["Summary", "Detailed", "Visualizations"]
    private let demoData = DemoData.shared
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: Theme.Spacing.lg) {
                    // Filters
                    VStack(spacing: Theme.Spacing.md) {
                        FilterRow(label: "Date Range", value: selectedDateRange) {
                            // Handle date range selection
                        }
                        
                        FilterRow(label: "Category", value: selectedCategory) {
                            // Handle category selection
                        }
                        
                        FilterRow(label: "Family Member", value: selectedMember) {
                            // Handle member selection
                        }
                    }
                    
                    // Segmented Control
                    SegmentedTabs(tabs: tabs, selection: $selectedTab)
                    
                    // Content based on selected tab
                    switch selectedTab {
                    case 0:
                        summaryView
                    case 1:
                        detailedView
                    case 2:
                        visualizationsView
                    default:
                        summaryView
                    }
                    
                    // Export buttons
                    HStack(spacing: Theme.Spacing.md) {
                        SecondaryButton(title: "Export PDF") {
                            // Handle PDF export
                        }
                        
                        SecondaryButton(title: "Export CSV") {
                            // Handle CSV export
                        }
                    }
                }
                .padding(Theme.Spacing.lg)
            }
            .background(Theme.Colors.surface)
            .navigationTitle("Reports")
            .navigationBarTitleDisplayMode(.large)
        }
    }
    
    private var summaryView: some View {
        VStack(spacing: Theme.Spacing.lg) {
            // Summary tiles
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
                title: "Net Income",
                amount: demoData.balance.formatAsCurrency(),
                color: Theme.Colors.success
            )
            
            // Category breakdown
            PastelCard {
                VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                    Text("Category Breakdown")
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    ForEach(demoData.categories.filter(\.isExpense).prefix(5)) { category in
                        HStack {
                            Image(systemName: category.icon)
                                .foregroundColor(Theme.Colors.primary)
                                .frame(width: 20)
                            
                            Text(category.name)
                                .font(Theme.Typography.body)
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            Spacer()
                            
                            VStack(alignment: .trailing, spacing: 2) {
                                Text("$\(Int.random(in: 200...800))")
                                    .font(Theme.Typography.label)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                Text("\(Int.random(in: 15...35))%")
                                    .font(Theme.Typography.caption)
                                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                            }
                        }
                    }
                }
            }
        }
    }
    
    private var detailedView: some View {
        PastelCard {
            VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                Text("Transaction Details")
                    .font(Theme.Typography.titleM)
                    .foregroundColor(Theme.Colors.onSurface)
                
                ForEach(demoData.transactions) { transaction in
                    ListRow(
                        leadingIcon: "creditcard",
                        title: transaction.title,
                        subtitle: "\(transaction.category) • \(transaction.familyMember) • \(transaction.date.formatAsShort())",
                        rightValue: "\(transaction.isExpense ? "-" : "+")\(transaction.amount.formatAsCurrency())",
                        showChevron: false
                    )
                }
            }
        }
    }
    
    private var visualizationsView: some View {
        VStack(spacing: Theme.Spacing.lg) {
            // Placeholder for charts
            PastelCard {
                VStack(spacing: Theme.Spacing.lg) {
                    Text("Monthly Trends")
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    // Simple bar chart placeholder
                    HStack(alignment: .bottom, spacing: 8) {
                        ForEach(0..<6) { index in
                            RoundedRectangle(cornerRadius: 4)
                                .fill(Theme.Colors.primary)
                                .frame(width: 30, height: CGFloat.random(in: 40...120))
                        }
                    }
                    .frame(height: 140)
                    
                    Text("Income vs Expenses (Last 6 Months)")
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                }
            }
            
            PastelCard {
                VStack(spacing: Theme.Spacing.lg) {
                    Text("Spending by Category")
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    // Simple pie chart placeholder
                    Circle()
                        .fill(
                            AngularGradient(
                                colors: [Theme.Colors.primary, Theme.Colors.success, Theme.Colors.warning, Theme.Colors.info],
                                center: .center
                            )
                        )
                        .frame(width: 150, height: 150)
                    
                    Text("Expense Distribution")
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                }
            }
        }
    }
}

#Preview {
    ReportsView()
}