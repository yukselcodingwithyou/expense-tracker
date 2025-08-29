import SwiftUI

// MARK: - Budget Settings View

struct BudgetSettingsView: View {
    @State private var showEditDialog = false
    @State private var overallBudget = 3000.0
    @State private var currentSpent = 2420.0
    
    // Demo category budgets
    @State private var categoryBudgets = [
        (name: "Groceries", limit: 500.0, spent: 325.0),
        (name: "Transportation", limit: 300.0, spent: 245.0),
        (name: "Dining", limit: 200.0, spent: 89.0),
        (name: "Entertainment", limit: 150.0, spent: 67.50)
    ]
    
    private var overallProgress: Double {
        currentSpent / overallBudget
    }
    
    private var shouldShowAlert: Bool {
        overallProgress >= 0.8
    }
    
    var body: some View {
        ScrollView {
            VStack(spacing: Theme.Spacing.lg) {
                // Budget Overview
                BudgetOverviewCard(
                    budgetLimit: overallBudget,
                    spent: currentSpent,
                    progress: overallProgress
                )
                
                // Budget Alert if over threshold
                if shouldShowAlert {
                    BudgetAlertCard()
                }
                
                // Category Budgets Section
                VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                    Text("Category Budgets")
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                        .padding(.horizontal, Theme.Spacing.lg)
                    
                    ForEach(categoryBudgets.indices, id: \.self) { index in
                        CategoryBudgetCard(
                            name: categoryBudgets[index].name,
                            limit: categoryBudgets[index].limit,
                            spent: categoryBudgets[index].spent
                        )
                    }
                }
            }
            .padding(Theme.Spacing.lg)
        }
        .background(Theme.Colors.surface)
        .navigationTitle("Budget Settings")
        .navigationBarTitleDisplayMode(.large)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Edit") {
                    showEditDialog = true
                }
                .foregroundColor(Theme.Colors.primary)
            }
        }
        .sheet(isPresented: $showEditDialog) {
            BudgetEditView(
                isPresented: $showEditDialog,
                overallBudget: $overallBudget
            )
        }
    }
}

struct BudgetOverviewCard: View {
    let budgetLimit: Double
    let spent: Double
    let progress: Double
    
    private var progressColor: Color {
        switch progress {
        case 1.0...: return Theme.Colors.expense
        case 0.8..<1.0: return Theme.Colors.warning
        default: return Theme.Colors.income
        }
    }
    
    var body: some View {
        PastelCard {
            VStack(spacing: Theme.Spacing.lg) {
                HStack {
                    Text("Monthly Budget")
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    Spacer()
                    
                    Text("\(Int(progress * 100))%")
                        .font(Theme.Typography.labelM)
                        .foregroundColor(progressColor)
                }
                
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Spent")
                            .font(Theme.Typography.caption)
                            .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                        
                        Text(spent.formatAsCurrency())
                            .font(Theme.Typography.titleL)
                            .foregroundColor(Theme.Colors.onSurface)
                    }
                    
                    Spacer()
                    
                    VStack(alignment: .trailing, spacing: 4) {
                        Text("Budget")
                            .font(Theme.Typography.caption)
                            .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                        
                        Text(budgetLimit.formatAsCurrency())
                            .font(Theme.Typography.titleL)
                            .foregroundColor(Theme.Colors.onSurface)
                    }
                }
                
                ProgressView(value: min(progress, 1.0))
                    .progressViewStyle(LinearProgressViewStyle(tint: progressColor))
                
                Text("Remaining: \((budgetLimit - spent).formatAsCurrency())")
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }
}

struct BudgetAlertCard: View {
    var body: some View {
        HStack(spacing: Theme.Spacing.md) {
            Image(systemName: "exclamationmark.triangle.fill")
                .foregroundColor(Theme.Colors.warning)
                .font(.title2)
            
            VStack(alignment: .leading, spacing: 4) {
                Text("Budget Alert")
                    .font(Theme.Typography.labelM)
                    .foregroundColor(Theme.Colors.warning)
                
                Text("You've exceeded 80% of your monthly budget")
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.8))
            }
            
            Spacer()
        }
        .padding(Theme.Spacing.lg)
        .background(
            RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                .fill(Theme.Colors.warning.opacity(0.1))
        )
    }
}

struct CategoryBudgetCard: View {
    let name: String
    let limit: Double
    let spent: Double
    
    private var progress: Double {
        spent / limit
    }
    
    private var progressColor: Color {
        switch progress {
        case 1.0...: return Theme.Colors.expense
        case 0.8..<1.0: return Theme.Colors.warning
        default: return Theme.Colors.income
        }
    }
    
    var body: some View {
        PastelCard {
            VStack(spacing: Theme.Spacing.md) {
                HStack {
                    HStack(spacing: Theme.Spacing.xs) {
                        Circle()
                            .fill(Theme.Colors.primary)
                            .frame(width: 8, height: 8)
                        
                        Text(name)
                            .font(Theme.Typography.body)
                            .foregroundColor(Theme.Colors.onSurface)
                    }
                    
                    Spacer()
                    
                    Button(action: {
                        // Handle edit category budget
                    }) {
                        Image(systemName: "pencil")
                            .font(.caption)
                            .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    }
                }
                
                HStack {
                    Text(spent.formatAsCurrency())
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    Spacer()
                    
                    Text(limit.formatAsCurrency())
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                }
                
                ProgressView(value: min(progress, 1.0))
                    .progressViewStyle(LinearProgressViewStyle(tint: progressColor))
                
                Text("\(Int(progress * 100))% used")
                    .font(Theme.Typography.caption)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }
}

struct BudgetEditView: View {
    @Binding var isPresented: Bool
    @Binding var overallBudget: Double
    @State private var budgetText = ""
    @State private var alertThreshold = 80
    @State private var includeRecurring = true
    
    var body: some View {
        NavigationView {
            VStack(spacing: Theme.Spacing.lg) {
                VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                    Text("Monthly Budget Limit")
                        .font(Theme.Typography.label)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    HStack {
                        Text("$")
                            .foregroundColor(Theme.Colors.onSurface)
                        
                        TextField("3000", text: $budgetText)
                            .keyboardType(.decimalPad)
                            .font(Theme.Typography.body)
                    }
                    .padding(Theme.Spacing.md)
                    .background(
                        RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                            .fill(Theme.Colors.card)
                            .overlay(
                                RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                    .stroke(Theme.Colors.cardBorder, lineWidth: 1)
                            )
                    )
                }
                
                VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                    Text("Alert Threshold")
                        .font(Theme.Typography.label)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    HStack {
                        Slider(value: Binding(
                            get: { Double(alertThreshold) },
                            set: { alertThreshold = Int($0) }
                        ), in: 50...100, step: 5)
                        
                        Text("\(alertThreshold)%")
                            .font(Theme.Typography.body)
                            .foregroundColor(Theme.Colors.onSurface)
                            .frame(width: 50)
                    }
                }
                
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Include Recurring Expenses")
                            .font(Theme.Typography.body)
                            .foregroundColor(Theme.Colors.onSurface)
                        
                        Text("When enabled, recurring expenses count towards budget limits")
                            .font(Theme.Typography.caption)
                            .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    }
                    
                    Spacer()
                    
                    Toggle("", isOn: $includeRecurring)
                }
                
                Spacer()
            }
            .padding(Theme.Spacing.lg)
            .navigationTitle("Budget Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        isPresented = false
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Save") {
                        if let newBudget = Double(budgetText) {
                            overallBudget = newBudget
                        }
                        isPresented = false
                    }
                }
            }
        }
        .onAppear {
            budgetText = String(format: "%.0f", overallBudget)
        }
    }
}

#Preview {
    NavigationView {
        BudgetSettingsView()
    }
}