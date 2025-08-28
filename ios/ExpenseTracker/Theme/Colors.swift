import SwiftUI

// MARK: - Theme Colors
extension Color {
    // Primary pastel colors
    static let primaryBlue = Color(red: 0.341, green: 0.769, blue: 0.898) // #57C4E5
    static let onPrimary = Color.white
    
    // Surface colors
    static let surfaceBackground = Color(red: 0.969, green: 0.973, blue: 0.980) // #F7F8FA
    static let onSurface = Color(red: 0.110, green: 0.110, blue: 0.118) // #1C1C1E
    
    // Accent colors
    static let accentSuccess = Color(red: 0.482, green: 0.827, blue: 0.537) // #7BD389
    static let accentWarning = Color(red: 0.969, green: 0.784, blue: 0.451) // #F7C873
    static let accentInfo = Color(red: 0.682, green: 0.663, blue: 0.969) // #AEA9F7
    
    // Card colors
    static let cardBackground = Color.white
    static let cardBorder = Color(red: 0.902, green: 0.910, blue: 0.925) // #E6E8EC
    
    // Semantic colors
    static let expenseRed = Color(red: 0.898, green: 0.451, blue: 0.451) // #E57373
    static let incomeGreen = Color(red: 0.506, green: 0.784, blue: 0.522) // #81C784
}

// MARK: - Theme namespace
struct Theme {
    struct Colors {
        static let primary = Color.primaryBlue
        static let onPrimary = Color.onPrimary
        static let surface = Color.surfaceBackground
        static let onSurface = Color.onSurface
        static let success = Color.accentSuccess
        static let warning = Color.accentWarning
        static let info = Color.accentInfo
        static let card = Color.cardBackground
        static let cardBorder = Color.cardBorder
        static let expense = Color.expenseRed
        static let income = Color.incomeGreen
    }
}