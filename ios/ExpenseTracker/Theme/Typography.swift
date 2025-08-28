import SwiftUI

// MARK: - Theme Typography
extension Theme {
    struct Typography {
        // Title styles
        static let titleXL = Font.system(size: 32, weight: .bold, design: .default)
        static let titleL = Font.system(size: 28, weight: .bold, design: .default)
        static let titleM = Font.system(size: 22, weight: .semibold, design: .default)
        
        // Body styles
        static let body = Font.system(size: 16, weight: .regular, design: .default)
        static let label = Font.system(size: 14, weight: .medium, design: .default)
        static let caption = Font.system(size: 12, weight: .regular, design: .default)
        
        // Stat tile specific
        static let statValue = Font.system(size: 24, weight: .semibold, design: .default)
        static let statTitle = Font.system(size: 14, weight: .medium, design: .default)
    }
}