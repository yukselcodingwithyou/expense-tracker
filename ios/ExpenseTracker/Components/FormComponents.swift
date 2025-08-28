import SwiftUI

// MARK: - List Row

struct ListRow: View {
    let leadingIcon: String
    let title: String
    let subtitle: String?
    let rightValue: String?
    let showChevron: Bool
    let action: (() -> Void)?
    
    init(
        leadingIcon: String,
        title: String,
        subtitle: String? = nil,
        rightValue: String? = nil,
        showChevron: Bool = true,
        action: (() -> Void)? = nil
    ) {
        self.leadingIcon = leadingIcon
        self.title = title
        self.subtitle = subtitle
        self.rightValue = rightValue
        self.showChevron = showChevron
        self.action = action
    }
    
    var body: some View {
        let content = HStack(spacing: Theme.Spacing.md) {
            // Leading icon
            Image(systemName: leadingIcon)
                .font(.title3)
                .foregroundColor(Theme.Colors.primary)
                .frame(width: 24, height: 24)
            
            // Title and subtitle
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface)
                
                if let subtitle = subtitle {
                    Text(subtitle)
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.onSurface.opacity(0.6))
                }
            }
            
            Spacer()
            
            // Right value
            if let rightValue = rightValue {
                Text(rightValue)
                    .font(Theme.Typography.label)
                    .foregroundColor(Theme.Colors.onSurface)
            }
            
            // Chevron
            if showChevron {
                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.5))
            }
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
        
        if let action = action {
            Button(action: action) {
                content
            }
            .buttonStyle(PlainButtonStyle())
        } else {
            content
        }
    }
}

// MARK: - Form Fields

struct FormTextField: View {
    let label: String
    @Binding var text: String
    let placeholder: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
            Text(label)
                .font(Theme.Typography.label)
                .foregroundColor(Theme.Colors.onSurface)
            
            TextField(placeholder, text: $text)
                .font(Theme.Typography.body)
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
}

struct MoneyField: View {
    let label: String
    @Binding var amount: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
            Text(label)
                .font(Theme.Typography.label)
                .foregroundColor(Theme.Colors.onSurface)
            
            HStack {
                Text("$")
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                
                TextField("0.00", text: $amount)
                    .font(Theme.Typography.body)
                    .keyboardType(.decimalPad)
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
}

struct ToggleRow: View {
    let title: String
    let subtitle: String?
    @Binding var isOn: Bool
    
    init(title: String, subtitle: String? = nil, isOn: Binding<Bool>) {
        self.title = title
        self.subtitle = subtitle
        self._isOn = isOn
    }
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface)
                
                if let subtitle = subtitle {
                    Text(subtitle)
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.onSurface.opacity(0.6))
                }
            }
            
            Spacer()
            
            Toggle("", isOn: $isOn)
                .toggleStyle(SwitchToggleStyle(tint: Theme.Colors.primary))
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

#Preview {
    VStack(spacing: 16) {
        ListRow(
            leadingIcon: "creditcard",
            title: "Groceries",
            subtitle: "Today, 2:30 PM",
            rightValue: "-$45.67"
        ) {}
        
        ListRow(
            leadingIcon: "house",
            title: "Family Members",
            showChevron: true
        ) {}
        
        FormTextField(
            label: "Transaction Name",
            text: .constant(""),
            placeholder: "Enter name"
        )
        
        MoneyField(
            label: "Amount",
            amount: .constant("25.50")
        )
        
        ToggleRow(
            title: "Include Recurring Expenses",
            subtitle: "Add recurring expenses to budget calculations",
            isOn: .constant(true)
        )
    }
    .padding()
    .background(Theme.Colors.surface)
}