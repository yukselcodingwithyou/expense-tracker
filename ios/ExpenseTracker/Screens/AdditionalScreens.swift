import SwiftUI

struct CategoriesView: View {
    private let demoData = DemoData.shared
    
    var body: some View {
        ScrollView {
            VStack(spacing: Theme.Spacing.lg) {
                ForEach(demoData.categories) { category in
                    PastelCard {
                        HStack(spacing: Theme.Spacing.lg) {
                            // Category icon
                            Image(systemName: category.icon)
                                .font(.title2)
                                .foregroundColor(Theme.Colors.primary)
                                .frame(width: 40, height: 40)
                                .background(
                                    Circle()
                                        .fill(Theme.Colors.primary.opacity(0.1))
                                )
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(category.name)
                                    .font(Theme.Typography.body)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                Text(category.isExpense ? "Expense" : "Income")
                                    .font(Theme.Typography.caption)
                                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                            }
                            
                            Spacer()
                            
                            Image(systemName: "chevron.right")
                                .font(.caption)
                                .foregroundColor(Theme.Colors.onSurface.opacity(0.5))
                        }
                    }
                }
            }
            .padding(Theme.Spacing.lg)
        }
        .background(Theme.Colors.surface)
        .navigationTitle("Categories")
        .navigationBarTitleDisplayMode(.large)
        .safeAreaInset(edge: .bottom) {
            PrimaryButton(title: "Add New Category") {
                // Handle add category
            }
            .padding(Theme.Spacing.lg)
            .background(Theme.Colors.surface)
        }
    }
}

struct RecurringExpensesView: View {
    @State private var name = ""
    @State private var amount = ""
    @State private var selectedCategory = "Entertainment"
    @State private var selectedFrequency = "Monthly"
    @State private var endDate = Date()
    
    private let frequencies = ["Weekly", "Monthly", "Yearly"]
    private let demoData = DemoData.shared
    
    var body: some View {
        ScrollView {
            VStack(spacing: Theme.Spacing.lg) {
                // Add Form
                PastelCard {
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("Add Recurring Expense")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                        
                        VStack(spacing: Theme.Spacing.md) {
                            FormTextField(
                                label: "Name",
                                text: $name,
                                placeholder: "Netflix Subscription"
                            )
                            
                            MoneyField(label: "Amount", amount: $amount)
                            
                            VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                                Text("Category")
                                    .font(Theme.Typography.label)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                Menu {
                                    ForEach(demoData.categories.filter(\.isExpense)) { category in
                                        Button(category.name) {
                                            selectedCategory = category.name
                                        }
                                    }
                                } label: {
                                    HStack {
                                        Text(selectedCategory)
                                            .font(Theme.Typography.body)
                                            .foregroundColor(Theme.Colors.onSurface)
                                        
                                        Spacer()
                                        
                                        Image(systemName: "chevron.down")
                                            .font(.caption)
                                            .foregroundColor(Theme.Colors.onSurface.opacity(0.5))
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
                            }
                            
                            VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                                Text("Frequency")
                                    .font(Theme.Typography.label)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                Menu {
                                    ForEach(frequencies, id: \.self) { frequency in
                                        Button(frequency) {
                                            selectedFrequency = frequency
                                        }
                                    }
                                } label: {
                                    HStack {
                                        Text(selectedFrequency)
                                            .font(Theme.Typography.body)
                                            .foregroundColor(Theme.Colors.onSurface)
                                        
                                        Spacer()
                                        
                                        Image(systemName: "chevron.down")
                                            .font(.caption)
                                            .foregroundColor(Theme.Colors.onSurface.opacity(0.5))
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
                            }
                            
                            VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                                Text("End Date (Optional)")
                                    .font(Theme.Typography.label)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                DatePicker("", selection: $endDate, displayedComponents: .date)
                                    .datePickerStyle(CompactDatePickerStyle())
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
                            
                            PrimaryButton(title: "Add Expense") {
                                // Handle add expense
                            }
                        }
                    }
                }
                
                // Upcoming Expenses List
                PastelCard {
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("Upcoming Expenses")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                        
                        ForEach(demoData.recurringExpenses) { expense in
                            VStack(alignment: .leading, spacing: 4) {
                                HStack {
                                    Text(expense.name)
                                        .font(Theme.Typography.body)
                                        .foregroundColor(Theme.Colors.onSurface)
                                    
                                    Spacer()
                                    
                                    Text("Next: \(expense.nextDate.formatAsMonthDay())")
                                        .font(Theme.Typography.caption)
                                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                                }
                                
                                HStack {
                                    Text("\(expense.frequency) – \(expense.amount.formatAsCurrency())")
                                        .font(Theme.Typography.caption)
                                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                                    
                                    Spacer()
                                }
                            }
                            .padding(.vertical, Theme.Spacing.xs)
                            
                            if expense.id != demoData.recurringExpenses.last?.id {
                                Divider()
                            }
                        }
                    }
                }
            }
            .padding(Theme.Spacing.lg)
        }
        .background(Theme.Colors.surface)
        .navigationTitle("Recurring Expenses")
        .navigationBarTitleDisplayMode(.large)
    }
}

#Preview {
    NavigationStack {
        CategoriesView()
    }
}

#Preview {
    NavigationStack {
        RecurringExpensesView()
    }
}