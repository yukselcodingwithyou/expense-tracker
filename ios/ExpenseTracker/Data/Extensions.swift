import Foundation

// MARK: - Formatting Helpers

extension Double {
    func formatAsCurrency() -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        formatter.maximumFractionDigits = 2
        return formatter.string(from: NSNumber(value: self)) ?? "$0.00"
    }
    
    func formatAsPercent() -> String {
        return "\(Int(self * 100))%"
    }
    
    // Convert to minor currency units (cents)
    func toAmountMinor() -> Int64 {
        return Int64(self * 100)
    }
}

extension Int64 {
    // Convert from minor currency units to dollars
    func fromAmountMinor() -> Double {
        return Double(self) / 100.0
    }
    
    func formatAmountMinorAsCurrency() -> String {
        return self.fromAmountMinor().formatAsCurrency()
    }
}

extension Date {
    func formatAsMonthDay() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MM/dd"
        return formatter.string(from: self)
    }
    
    func formatAsShort() -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        return formatter.string(from: self)
    }
    
    func formatAsTime() -> String {
        let formatter = DateFormatter()
        formatter.timeStyle = .short
        return formatter.string(from: self)
    }
}