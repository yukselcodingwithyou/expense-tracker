import SwiftUI

// MARK: - Stat Tile

struct StatTileView: View {
    let title: String
    let amount: String
    let color: Color = Theme.Colors.primary
    
    var body: some View {
        VStack(spacing: Theme.Spacing.xs) {
            Text(title)
                .font(Theme.Typography.statTitle)
                .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
            
            Text(amount)
                .font(Theme.Typography.statValue)
                .fontWeight(.semibold)
                .foregroundColor(color)
        }
        .padding(Theme.Spacing.lg)
        .frame(maxWidth: .infinity)
        .background(
            RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                .fill(Theme.Colors.card)
                .shadow(color: .black.opacity(0.05), radius: Theme.Elevation.low, x: 0, y: 1)
                .overlay(
                    RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                        .stroke(Theme.Colors.cardBorder, lineWidth: 1)
                )
        )
    }
}

// MARK: - Card Container

struct PastelCard<Content: View>: View {
    let content: Content
    
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    
    var body: some View {
        content
            .padding(Theme.Spacing.lg)
            .background(
                RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                    .fill(Theme.Colors.card)
                    .shadow(color: .black.opacity(0.05), radius: Theme.Elevation.low, x: 0, y: 1)
                    .overlay(
                        RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                            .stroke(Theme.Colors.cardBorder, lineWidth: 1)
                    )
            )
    }
}

#Preview {
    VStack(spacing: 16) {
        HStack(spacing: 12) {
            StatTileView(title: "Total Income", amount: "$12,500")
            StatTileView(title: "Total Expenses", amount: "$8,200", color: Theme.Colors.expense)
        }
        
        StatTileView(title: "Balance", amount: "$4,300", color: Theme.Colors.success)
        
        PastelCard {
            VStack(alignment: .leading, spacing: 8) {
                Text("Sample Card")
                    .font(Theme.Typography.titleM)
                Text("This is a sample card with pastel styling")
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
            }
        }
    }
    .padding()
    .background(Theme.Colors.surface)
}