import SwiftUI

struct CategoriesView: View {
    @State private var showAddDialog = false
    private let demoData = DemoData.shared
    
    var body: some View {
        ScrollView {
            VStack(spacing: Theme.Spacing.lg) {
                // Expense Categories Section
                VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                    Text("Expense Categories")
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                        .padding(.horizontal, Theme.Spacing.lg)
                    
                    ForEach(demoData.categories.filter(\.isExpense)) { category in
                        CategoryItem(category: category)
                    }
                }
                
                // Income Categories Section
                VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                    Text("Income Categories")
                        .font(Theme.Typography.titleM)
                        .foregroundColor(Theme.Colors.onSurface)
                        .padding(.horizontal, Theme.Spacing.lg)
                    
                    ForEach(demoData.categories.filter { !$0.isExpense }) { category in
                        CategoryItem(category: category)
                    }
                }
            }
            .padding(Theme.Spacing.lg)
        }
        .background(Theme.Colors.surface)
        .navigationTitle("Categories")
        .navigationBarTitleDisplayMode(.large)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Add") {
                    showAddDialog = true
                }
                .foregroundColor(Theme.Colors.primary)
            }
        }
        .sheet(isPresented: $showAddDialog) {
            AddCategoryView(isPresented: $showAddDialog)
        }
    }
}

struct CategoryItem: View {
    let category: Category
    @State private var showActionSheet = false
    
    var body: some View {
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
                
                Button(action: { showActionSheet = true }) {
                    Image(systemName: "ellipsis")
                        .font(.caption)
                        .foregroundColor(Theme.Colors.onSurface.opacity(0.5))
                }
            }
        }
        .actionSheet(isPresented: $showActionSheet) {
            ActionSheet(
                title: Text(category.name),
                buttons: [
                    .default(Text("Edit")) {
                        // Handle edit
                    },
                    .destructive(Text("Archive")) {
                        // Handle archive
                    },
                    .cancel()
                ]
            )
        }
    }
}

struct AddCategoryView: View {
    @Binding var isPresented: Bool
    @State private var name = ""
    @State private var isExpense = true
    @State private var selectedIcon = "cart"
    @State private var showIconPicker = false
    
    private let availableIcons = [
        "cart", "car", "fork.knife", "tv", "house", "heart", "briefcase",
        "airplane", "phone", "book", "gamecontroller", "gift"
    ]
    
    var body: some View {
        NavigationView {
            VStack(spacing: Theme.Spacing.lg) {
                FormTextField(
                    label: "Category Name",
                    text: $name,
                    placeholder: "Enter category name"
                )
                
                VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                    Text("Type")
                        .font(Theme.Typography.label)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    HStack(spacing: Theme.Spacing.md) {
                        Button(action: { isExpense = true }) {
                            Text("Expense")
                                .padding(.horizontal, Theme.Spacing.md)
                                .padding(.vertical, Theme.Spacing.xs)
                                .background(
                                    RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                        .fill(isExpense ? Theme.Colors.primary : Theme.Colors.card)
                                )
                                .foregroundColor(isExpense ? .white : Theme.Colors.onSurface)
                        }
                        
                        Button(action: { isExpense = false }) {
                            Text("Income")
                                .padding(.horizontal, Theme.Spacing.md)
                                .padding(.vertical, Theme.Spacing.xs)
                                .background(
                                    RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                        .fill(!isExpense ? Theme.Colors.primary : Theme.Colors.card)
                                )
                                .foregroundColor(!isExpense ? .white : Theme.Colors.onSurface)
                        }
                    }
                }
                
                VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                    Text("Icon")
                        .font(Theme.Typography.label)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    Button(action: { showIconPicker = true }) {
                        HStack {
                            Image(systemName: selectedIcon)
                                .font(.title2)
                                .foregroundColor(Theme.Colors.primary)
                            
                            Text("Select Icon")
                                .foregroundColor(Theme.Colors.onSurface)
                            
                            Spacer()
                            
                            Image(systemName: "chevron.right")
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
                
                Spacer()
            }
            .padding(Theme.Spacing.lg)
            .navigationTitle("Add Category")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        isPresented = false
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Save") {
                        // Handle save
                        isPresented = false
                    }
                    .disabled(name.isEmpty)
                }
            }
        }
        .sheet(isPresented: $showIconPicker) {
            IconPickerView(selectedIcon: $selectedIcon, availableIcons: availableIcons)
        }
    }
}

struct IconPickerView: View {
    @Binding var selectedIcon: String
    @Environment(\.presentationMode) var presentationMode
    let availableIcons: [String]
    
    let columns = Array(repeating: GridItem(.flexible()), count: 4)
    
    var body: some View {
        NavigationView {
            ScrollView {
                LazyVGrid(columns: columns, spacing: Theme.Spacing.md) {
                    ForEach(availableIcons, id: \.self) { icon in
                        Button(action: {
                            selectedIcon = icon
                            presentationMode.wrappedValue.dismiss()
                        }) {
                            Image(systemName: icon)
                                .font(.title2)
                                .foregroundColor(selectedIcon == icon ? .white : Theme.Colors.primary)
                                .frame(width: 60, height: 60)
                                .background(
                                    RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                        .fill(selectedIcon == icon ? Theme.Colors.primary : Theme.Colors.primary.opacity(0.1))
                                )
                        }
                    }
                }
                .padding(Theme.Spacing.lg)
            }
            .navigationTitle("Select Icon")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
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