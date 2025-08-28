import SwiftUI

// MARK: - Theme Shapes and Spacing
extension Theme {
    struct Shapes {
        static let cornerRadius: CGFloat = 12
        static let buttonCornerRadius: CGFloat = 8
        static let cardCornerRadius: CGFloat = 12
    }
    
    struct Spacing {
        static let xs: CGFloat = 4
        static let sm: CGFloat = 8
        static let md: CGFloat = 12
        static let lg: CGFloat = 16
        static let xl: CGFloat = 20
        static let xxl: CGFloat = 24
        static let xxxl: CGFloat = 32
        
        // Grid base
        static let grid: CGFloat = 8
    }
    
    struct Elevation {
        static let low = 2.0
        static let medium = 4.0
        static let high = 8.0
    }
}