import SwiftUI

struct AddExpenseView: View {
    @State private var amount = ""
    @State private var selectedCategory = "Groceries"
    @State private var selectedMember = "John"
    @State private var selectedDate = Date()
    @State private var notes = ""
    @State private var isExpense = true
    
    private let demoData = DemoData.shared
    
    var categories: [String] {
        demoData.categories.filter { $0.isExpense == isExpense }.map(\.name)
    }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: Theme.Spacing.lg) {
                    // Expense/Income Toggle
                    Picker("Type", selection: $isExpense) {
                        Text("Expense").tag(true)
                        Text("Income").tag(false)
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    .padding(.horizontal, Theme.Spacing.lg)
                    
                    VStack(spacing: Theme.Spacing.lg) {
                        // Amount Field
                        MoneyField(label: "Amount", amount: $amount)
                        
                        // Category Selector
                        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                            Text("Category")
                                .font(Theme.Typography.label)
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            Menu {
                                ForEach(categories, id: \.self) { category in
                                    Button(category) {
                                        selectedCategory = category
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
                        
                        // Date Picker
                        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                            Text("Date")
                                .font(Theme.Typography.label)
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            DatePicker("", selection: $selectedDate, displayedComponents: .date)
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
                        
                        // Family Member Selector
                        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                            Text("Family Member")
                                .font(Theme.Typography.label)
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            Menu {
                                ForEach(demoData.familyMembers) { member in
                                    Button(member.name) {
                                        selectedMember = member.name
                                    }
                                }
                            } label: {
                                HStack {
                                    Text(selectedMember)
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
                        
                        // Notes Field
                        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                            Text("Notes (Optional)")
                                .font(Theme.Typography.label)
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            TextField("Add notes...", text: $notes, axis: .vertical)
                                .font(Theme.Typography.body)
                                .padding(Theme.Spacing.md)
                                .frame(minHeight: 80, alignment: .topLeading)
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
                    .padding(.horizontal, Theme.Spacing.lg)
                    
                    Spacer(minLength: 50)
                }
            }
            .background(Theme.Colors.surface)
            .navigationTitle(isExpense ? "Add Expense" : "Add Income")
            .navigationBarTitleDisplayMode(.large)
            .navigationBarBackButtonHidden(false)
            .safeAreaInset(edge: .bottom) {
                PrimaryButton(title: "Add \(isExpense ? "Expense" : "Income")") {
                    // Handle add action
                }
                .padding(Theme.Spacing.lg)
                .background(Theme.Colors.surface)
            }
        }
    }
}

#Preview {
    NavigationStack {
        AddExpenseView()
    }
}