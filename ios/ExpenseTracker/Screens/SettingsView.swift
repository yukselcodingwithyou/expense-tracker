import SwiftUI

struct SettingsView: View {
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: Theme.Spacing.lg) {
                    // Family Section
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("Family")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                            .padding(.horizontal, Theme.Spacing.lg)
                        
                        NavigationLink(destination: FamilyMembersView()) {
                            ListRow(
                                leadingIcon: "person.2",
                                title: "Family Members",
                                subtitle: "Manage family members",
                                showChevron: true
                            )
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                    
                    // Preferences Section
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("Preferences")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                            .padding(.horizontal, Theme.Spacing.lg)
                        
                        VStack(spacing: Theme.Spacing.sm) {
                            ListRow(
                                leadingIcon: "bell",
                                title: "Notifications",
                                subtitle: "Push notifications and alerts",
                                showChevron: true
                            ) {
                                // Handle notifications
                            }
                            
                            ListRow(
                                leadingIcon: "dollarsign.circle",
                                title: "Currency",
                                subtitle: "USD ($)",
                                showChevron: true
                            ) {
                                // Handle currency
                            }
                            
                            ListRow(
                                leadingIcon: "paintbrush",
                                title: "Appearance",
                                subtitle: "Light",
                                showChevron: true
                            ) {
                                // Handle appearance
                            }
                        }
                    }
                    
                    // Data Section
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("Data")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                            .padding(.horizontal, Theme.Spacing.lg)
                        
                        VStack(spacing: Theme.Spacing.sm) {
                            ListRow(
                                leadingIcon: "icloud.and.arrow.up",
                                title: "Backup",
                                subtitle: "Save your data to iCloud",
                                showChevron: true
                            ) {
                                // Handle backup
                            }
                            
                            ListRow(
                                leadingIcon: "icloud.and.arrow.down",
                                title: "Restore",
                                subtitle: "Restore from backup",
                                showChevron: true
                            ) {
                                // Handle restore
                            }
                        }
                    }
                    
                    // About Section
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("About")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                            .padding(.horizontal, Theme.Spacing.lg)
                        
                        ListRow(
                            leadingIcon: "questionmark.circle",
                            title: "Help",
                            subtitle: "Support and documentation",
                            showChevron: true
                        ) {
                            // Handle help
                        }
                    }
                }
                .padding(Theme.Spacing.lg)
            }
            .background(Theme.Colors.surface)
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.large)
        }
    }
}

struct FamilyMembersView: View {
    private let demoData = DemoData.shared
    
    var body: some View {
        ScrollView {
            VStack(spacing: Theme.Spacing.lg) {
                ForEach(demoData.familyMembers) { member in
                    PastelCard {
                        HStack(spacing: Theme.Spacing.lg) {
                            // Avatar placeholder
                            Circle()
                                .fill(Theme.Colors.primary.opacity(0.2))
                                .frame(width: 50, height: 50)
                                .overlay(
                                    Image(systemName: "person.fill")
                                        .foregroundColor(Theme.Colors.primary)
                                )
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(member.name)
                                    .font(Theme.Typography.body)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                Text(member.role)
                                    .font(Theme.Typography.caption)
                                    .foregroundColor(Theme.Colors.primary)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 2)
                                    .background(
                                        RoundedRectangle(cornerRadius: 4)
                                            .fill(Theme.Colors.primary.opacity(0.1))
                                    )
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
        .navigationTitle("Family Members")
        .navigationBarTitleDisplayMode(.large)
        .safeAreaInset(edge: .bottom) {
            PrimaryButton(title: "Add Member") {
                // Handle add member
            }
            .padding(Theme.Spacing.lg)
            .background(Theme.Colors.surface)
        }
    }
}

struct BudgetSettingsView: View {
    @State private var overallBudget = "2500"
    @State private var selectedPeriod = "Monthly"
    @State private var alertThreshold = "80"
    @State private var includeRecurring = true
    @State private var enableBudgetAlerts = true
    
    private let periods = ["Weekly", "Monthly", "Yearly"]
    private let demoData = DemoData.shared
    
    var body: some View {
        ScrollView {
            VStack(spacing: Theme.Spacing.lg) {
                // Overall Spending Card
                PastelCard {
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("Overall Spending")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                        
                        VStack(spacing: Theme.Spacing.md) {
                            MoneyField(label: "Amount", amount: $overallBudget)
                            
                            VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                                Text("Period")
                                    .font(Theme.Typography.label)
                                    .foregroundColor(Theme.Colors.onSurface)
                                
                                Menu {
                                    ForEach(periods, id: \.self) { period in
                                        Button(period) {
                                            selectedPeriod = period
                                        }
                                    }
                                } label: {
                                    HStack {
                                        Text(selectedPeriod)
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
                            
                            FormTextField(
                                label: "Alert Threshold (%)",
                                text: $alertThreshold,
                                placeholder: "80"
                            )
                        }
                    }
                }
                
                // Include Recurring Toggle
                ToggleRow(
                    title: "Include Recurring Expenses",
                    subtitle: "Add recurring expenses to budget calculations",
                    isOn: $includeRecurring
                )
                
                // Individual Categories
                PastelCard {
                    VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                        Text("Individual Categories")
                            .font(Theme.Typography.titleM)
                            .foregroundColor(Theme.Colors.onSurface)
                        
                        ForEach(demoData.budgetCategories) { category in
                            HStack {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(category.name)
                                        .font(Theme.Typography.body)
                                        .foregroundColor(Theme.Colors.onSurface)
                                    
                                    Text("$\(Int(category.spent)) / $\(Int(category.limit))")
                                        .font(Theme.Typography.caption)
                                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                                }
                                
                                Spacer()
                                
                                Button("Edit") {
                                    // Handle edit
                                }
                                .font(Theme.Typography.caption)
                                .foregroundColor(Theme.Colors.primary)
                            }
                            .padding(.vertical, Theme.Spacing.xs)
                        }
                    }
                }
                
                // Enable Budget Alerts
                ToggleRow(
                    title: "Enable Budget Alerts",
                    subtitle: "Get notified when approaching budget limits",
                    isOn: $enableBudgetAlerts
                )
            }
            .padding(Theme.Spacing.lg)
        }
        .background(Theme.Colors.surface)
        .navigationTitle("Budget Settings")
        .navigationBarTitleDisplayMode(.large)
    }
}

#Preview {
    SettingsView()
}

#Preview {
    NavigationStack {
        FamilyMembersView()
    }
}

#Preview {
    NavigationStack {
        BudgetSettingsView()
    }
}