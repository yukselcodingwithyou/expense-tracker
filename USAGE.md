# Family Finance Tracker UI Design System

This document explains how to use the pastel design system implemented for the Family Finance Tracker app on both iOS (SwiftUI) and Android (Jetpack Compose).

## Overview

The design system implements a pastel aesthetic with consistent colors, typography, spacing, and components across both platforms. All screens match the visual specifications with pixel-faithful accuracy.

## iOS SwiftUI Implementation

### Opening Previews

To view the SwiftUI previews in Xcode:

1. Open `ios/ExpenseTracker.xcodeproj` in Xcode
2. Navigate to any `.swift` file in the project
3. Use `Cmd + Alt + Enter` to open the Canvas
4. Click "Resume" to start the preview
5. Use the live preview to interact with the components

### Key Files Structure

```
ios/ExpenseTracker/
├── Theme/
│   ├── Colors.swift          # Pastel color palette
│   ├── Typography.swift      # Font scales and styles
│   └── Shapes.swift          # Corner radius and spacing
├── Components/
│   ├── Buttons.swift         # Primary/Secondary buttons
│   ├── Cards.swift           # StatTile and PastelCard
│   ├── Controls.swift        # SegmentedTabs, ProgressBar, FilterRow
│   └── FormComponents.swift  # Form fields, toggles, list rows
├── Screens/
│   ├── DashboardView.swift   # Main dashboard with stats
│   ├── AddExpenseView.swift  # Add expense/income forms
│   ├── ReportsView.swift     # Reports with segmented control
│   ├── SettingsView.swift    # Settings and sub-screens
│   ├── AdditionalScreens.swift # Categories, Recurring, etc.
│   └── AuthScreens.swift     # Welcome, auth, onboarding
├── Data/
│   ├── DemoData.swift        # Sample data and models
│   └── Extensions.swift      # Formatting helpers
└── Navigation/
    └── MainTabView.swift     # Bottom tab navigation
```

### Using the Theme

```swift
// Colors
Theme.Colors.primary          // Pastel blue #57C4E5
Theme.Colors.success          // Pastel green #7BD389
Theme.Colors.warning          // Pastel yellow #F7C873
Theme.Colors.card             // White backgrounds
Theme.Colors.surface          // Light gray background

// Typography
Theme.Typography.titleXL      // 32pt bold
Theme.Typography.titleL       // 28pt bold
Theme.Typography.titleM       // 22pt semibold
Theme.Typography.body         // 16pt regular
Theme.Typography.label        // 14pt medium
Theme.Typography.caption      // 12pt regular

// Spacing
Theme.Spacing.xs              // 4pt
Theme.Spacing.sm              // 8pt
Theme.Spacing.md              // 12pt
Theme.Spacing.lg              // 16pt
Theme.Spacing.xl              // 20pt
```

### Using Components

```swift
// Stat Tiles
StatTileView(
    title: "Total Income",
    amount: "$12,500",
    color: Theme.Colors.income
)

// Cards
PastelCard {
    VStack {
        Text("Card Content")
    }
}

// Buttons
PrimaryButton(title: "Save") { /* action */ }
SecondaryButton(title: "Cancel") { /* action */ }

// Form Fields
MoneyField(label: "Amount", amount: $amount)
FormTextField(label: "Name", text: $name, placeholder: "Enter name")
ToggleRow(title: "Enable", isOn: $isEnabled)
```

## Android Jetpack Compose Implementation

### Opening Previews

To view the Compose previews in Android Studio:

1. Open `android/` folder in Android Studio
2. Navigate to any Composable file
3. Click the "Split" view button in the top right
4. The preview panel will show on the right
5. Click the "Interactive" button to test interactions

### Key Files Structure

```
android/app/src/main/java/com/expensetracker/
├── ui/theme/
│   ├── Color.kt             # Pastel color definitions
│   ├── Type.kt              # Typography scale
│   └── Theme.kt             # Material3 theme setup
├── ui/components/
│   ├── Buttons.kt           # Primary/Secondary buttons
│   ├── Cards.kt             # StatTile and PastelCard
│   ├── Controls.kt          # SegmentedTabs, ProgressBar, FilterRow
│   └── FormComponents.kt    # Form fields, toggles, list rows
├── ui/screens/
│   ├── AddExpenseScreen.kt  # Add expense/income forms
│   ├── ReportsScreen.kt     # Reports with segmented control
│   ├── SettingsScreen.kt    # Settings and sub-screens
│   └── AuthScreens.kt       # Welcome, auth, onboarding
├── ui/dashboard/
│   └── DashboardScreen.kt   # Main dashboard with stats
├── ui/navigation/
│   ├── MainBottomNavigation.kt # Bottom navigation
│   └── ExpenseTrackerNavigation.kt # Main app navigation
└── data/
    └── DemoData.kt          # Sample data and formatting
```

### Using the Theme

```kotlin
// Colors
PrimaryBlue              // Pastel blue #57C4E5
AccentSuccess            // Pastel green #7BD389
AccentWarning            // Pastel yellow #F7C873
CardBackground           // White backgrounds
SurfaceBackground        // Light gray background

// Typography
MaterialTheme.typography.displayLarge     // 32sp bold
MaterialTheme.typography.displayMedium    // 28sp bold
MaterialTheme.typography.headlineMedium   // 22sp semibold
MaterialTheme.typography.bodyLarge        // 16sp regular
MaterialTheme.typography.labelLarge       // 14sp medium
MaterialTheme.typography.bodySmall        // 12sp regular

// Spacing
8.dp, 12.dp, 16.dp, 20.dp, 24.dp, 32.dp
```

### Using Components

```kotlin
// Stat Tiles
StatTile(
    title = "Total Income",
    amount = "$12,500",
    color = IncomeGreen
)

// Cards
PastelCard {
    Column {
        Text("Card Content")
    }
}

// Buttons
PrimaryButton(text = "Save", onClick = { /* action */ })
SecondaryButton(text = "Cancel", onClick = { /* action */ })

// Form Fields
MoneyTextField(label = "Amount", value = amount, onValueChange = { amount = it })
FormTextField(label = "Name", value = name, onValueChange = { name = it }, placeholder = "Enter name")
ToggleRow(title = "Enable", checked = isEnabled, onCheckedChange = { isEnabled = it })
```

## Demo Data

Both platforms use consistent demo data:

- **Income**: $12,500
- **Expenses**: $8,200  
- **Balance**: $4,300
- **Savings Goals**: Emergency Fund (50%), Vacation (40%), New Car (75%), Home Renovation (30%)
- **Family Members**: John (Admin), Sarah (Member), Emma (Member)
- **Categories**: Groceries, Transportation, Dining, Entertainment, Utilities, Salary, Freelance

## Screens Implemented

### Dashboard
- ✅ Stat tiles for Income, Expenses, Balance
- ✅ Expense breakdown list with categories
- ✅ Savings goals preview with progress bars
- ✅ Recent transactions list
- ✅ "+" FAB for adding transactions

### Add Expense/Income
- ✅ Amount field with currency symbol
- ✅ Category dropdown selector
- ✅ Date picker (simplified)
- ✅ Family member selector
- ✅ Notes text field
- ✅ Expense/Income toggle (Android) or separate screens (iOS)

### Reports
- ✅ Date Range, Category, Family Member filters
- ✅ Segmented control: Summary | Detailed | Visualizations
- ✅ Summary tiles and category breakdown
- ✅ Detailed transaction list
- ✅ Simple chart placeholders
- ✅ Export PDF/CSV buttons

### Settings
- ✅ Family section (Family Members)
- ✅ Preferences (Notifications, Currency, Appearance)
- ✅ Data (Backup, Restore)
- ✅ About (Help)
- ✅ List rows with icons, subtitles, chevrons

### Additional Screens
- ✅ Family Members list with avatars and roles
- ✅ Savings Goals with progress tracking
- ✅ Categories management
- ✅ Budget Settings with toggles and category limits
- ✅ Recurring Expenses form and list
- ✅ Welcome/Onboarding screens
- ✅ Authentication screens

### Navigation
- ✅ Bottom tab navigation: Dashboard | Expenses | Income | Reports | Settings
- ✅ Push navigation for detail screens
- ✅ Consistent back button behavior

## Customization

### Changing Colors

**iOS:**
Edit `ios/ExpenseTracker/Theme/Colors.swift` and update the color values.

**Android:**
Edit `android/app/src/main/java/com/expensetracker/ui/theme/Color.kt` and update the color values.

### Modifying Typography

**iOS:**
Edit `ios/ExpenseTracker/Theme/Typography.swift` to change font sizes and weights.

**Android:**
Edit `android/app/src/main/java/com/expensetracker/ui/theme/Type.kt` to modify the Material3 typography scale.

### Adjusting Spacing

**iOS:**
Update values in `ios/ExpenseTracker/Theme/Shapes.swift`.

**Android:**
Modify spacing values directly in component files using `.dp` units.

## Accessibility

Both implementations include:

- ✅ Proper content descriptions for all interactive elements
- ✅ Sufficient color contrast ratios
- ✅ Minimum touch target sizes (44x44pt iOS, 48x48dp Android)
- ✅ Dynamic Type/Font Scale support
- ✅ Screen reader compatibility

## Testing

To test the components:

1. **iOS**: Use Xcode Simulator with VoiceOver enabled
2. **Android**: Use Android Emulator with TalkBack enabled
3. Test with different font sizes and dark mode
4. Verify touch targets are accessible
5. Test navigation flow and screen transitions

The design system provides a solid foundation for expanding the app while maintaining visual consistency and accessibility standards across both platforms.