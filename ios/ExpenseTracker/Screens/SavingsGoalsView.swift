import SwiftUI

struct SavingsGoalsView: View {
    private let demoData = DemoData.shared
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: Theme.Spacing.lg) {
                    ForEach(demoData.savingsGoals) { goal in
                        PastelCard {
                            VStack(alignment: .leading, spacing: Theme.Spacing.md) {
                                HStack {
                                    Text(goal.name)
                                        .font(Theme.Typography.titleM)
                                        .foregroundColor(Theme.Colors.onSurface)
                                    
                                    Spacer()
                                    
                                    Text(goal.progressPercent)
                                        .font(Theme.Typography.label)
                                        .foregroundColor(Theme.Colors.success)
                                }
                                
                                Text("\(goal.currentAmount.formatAsCurrency()) / \(goal.targetAmount.formatAsCurrency())")
                                    .font(Theme.Typography.body)
                                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                                
                                ProgressBarWithLabel(progress: goal.progress, label: goal.progressPercent)
                            }
                        }
                    }
                }
                .padding(Theme.Spacing.lg)
            }
            .background(Theme.Colors.surface)
            .navigationTitle("Savings Goals")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Add Goal") {
                        // Handle add goal
                    }
                    .foregroundColor(Theme.Colors.primary)
                }
            }
        }
    }
}

#Preview {
    SavingsGoalsView()
}