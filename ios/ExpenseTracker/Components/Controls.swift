import SwiftUI

// MARK: - Segmented Control

struct SegmentedTabs: View {
    let tabs: [String]
    @Binding var selection: Int
    
    var body: some View {
        HStack(spacing: 0) {
            ForEach(0..<tabs.count, id: \.self) { index in
                Button(action: {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        selection = index
                    }
                }) {
                    Text(tabs[index])
                        .font(Theme.Typography.label)
                        .foregroundColor(selection == index ? Theme.Colors.onPrimary : Theme.Colors.onSurface)
                        .padding(.vertical, Theme.Spacing.sm)
                        .padding(.horizontal, Theme.Spacing.md)
                        .frame(maxWidth: .infinity)
                        .background(
                            RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                .fill(selection == index ? Theme.Colors.primary : Color.clear)
                        )
                }
            }
        }
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

// MARK: - Progress Bar

struct ProgressBarWithLabel: View {
    let progress: Double
    let label: String
    
    var body: some View {
        HStack {
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: 4)
                    .fill(Theme.Colors.cardBorder)
                    .frame(height: 8)
                
                RoundedRectangle(cornerRadius: 4)
                    .fill(Theme.Colors.success)
                    .frame(width: max(0, CGFloat(progress) * 200), height: 8)
            }
            .frame(width: 200)
            
            Text(label)
                .font(Theme.Typography.caption)
                .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                .frame(width: 40, alignment: .trailing)
        }
    }
}

// MARK: - Filter Row

struct FilterRow: View {
    let label: String
    let value: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack {
                Text(label)
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface)
                
                Spacer()
                
                Text(value)
                    .font(Theme.Typography.label)
                    .foregroundColor(Theme.Colors.primary)
                
                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.5))
            }
            .padding(Theme.Spacing.lg)
            .background(
                RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                    .fill(Theme.Colors.card)
                    .overlay(
                        RoundedRectangle(cornerRadius: Theme.Shapes.cardCornerRadius)
                            .stroke(Theme.Colors.cardBorder, lineWidth: 1)
                    )
            )
        }
    }
}

#Preview {
    VStack(spacing: 20) {
        SegmentedTabs(
            tabs: ["Summary", "Detailed", "Visualizations"],
            selection: .constant(0)
        )
        
        ProgressBarWithLabel(progress: 0.65, label: "65%")
        
        FilterRow(label: "Date Range", value: "This Month") {}
        FilterRow(label: "Category", value: "All Categories") {}
    }
    .padding()
    .background(Theme.Colors.surface)
}