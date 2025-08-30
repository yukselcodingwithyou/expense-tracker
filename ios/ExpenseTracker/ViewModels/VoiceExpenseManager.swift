import Foundation
import SwiftUI
import Combine

@MainActor
class VoiceExpenseManager: ObservableObject {
    @Published var isProcessing = false
    @Published var isCreatingExpense = false
    @Published var currentVoiceExpense: VoiceExpenseData?
    @Published var showExpenseDetails = false
    @Published var expenseCreated = false
    @Published var createdExpense: LedgerEntryResponse?
    @Published var voiceExpenses: [VoiceExpenseData] = []
    @Published var error: String?
    
    private let voiceExpenseAPIService = VoiceExpenseAPIService()
    
    func processVoiceExpense(spokenText: String, preferredCurrency: String = "USD") {
        isProcessing = true
        error = nil
        
        let request = VoiceExpenseRequest(spokenText: spokenText, preferredCurrency: preferredCurrency)
        
        Task {
            do {
                let voiceExpenseData = try await voiceExpenseAPIService.processVoiceExpense(request)
                
                DispatchQueue.main.async {
                    self.isProcessing = false
                    self.currentVoiceExpense = voiceExpenseData
                    self.showExpenseDetails = true
                }
            } catch {
                DispatchQueue.main.async {
                    self.isProcessing = false
                    self.error = error.localizedDescription
                }
            }
        }
    }
    
    func createExpenseFromVoice(_ request: CreateExpenseFromVoiceRequest) {
        isCreatingExpense = true
        error = nil
        
        Task {
            do {
                let ledgerEntry = try await voiceExpenseAPIService.createExpenseFromVoice(request)
                
                DispatchQueue.main.async {
                    self.isCreatingExpense = false
                    self.expenseCreated = true
                    self.createdExpense = ledgerEntry
                }
            } catch {
                DispatchQueue.main.async {
                    self.isCreatingExpense = false
                    self.error = error.localizedDescription
                }
            }
        }
    }
    
    func loadUserVoiceExpenses() {
        Task {
            do {
                let voiceExpenseList = try await voiceExpenseAPIService.getUserVoiceExpenses()
                
                DispatchQueue.main.async {
                    self.voiceExpenses = voiceExpenseList
                }
            } catch {
                DispatchQueue.main.async {
                    self.error = error.localizedDescription
                }
            }
        }
    }
    
    func clearError() {
        error = nil
    }
    
    func clearExpenseDetails() {
        showExpenseDetails = false
        currentVoiceExpense = nil
        expenseCreated = false
        createdExpense = nil
    }
    
    func updateParseResult(_ updatedResult: ExpenseParseResult) {
        guard let currentExpense = currentVoiceExpense else { return }
        
        currentVoiceExpense = VoiceExpenseData(
            id: currentExpense.id,
            userId: currentExpense.userId,
            originalText: currentExpense.originalText,
            parseResult: updatedResult,
            overallConfidence: currentExpense.overallConfidence,
            suggestions: currentExpense.suggestions,
            fieldConfidences: currentExpense.fieldConfidences,
            createdAt: currentExpense.createdAt
        )
    }
    
    // Helper method to format amount from minor units
    func formatAmountFromMinor(_ amountMinor: Int64?) -> String {
        guard let amountMinor = amountMinor else { return "$0.00" }
        
        let amount = Double(amountMinor) / 100.0
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        return formatter.string(from: NSNumber(value: amount)) ?? "$0.00"
    }
    
    // Helper method to format confidence as percentage
    func formatConfidence(_ confidence: Double) -> String {
        let percentage = Int(confidence * 100)
        return "\(percentage)%"
    }
    
    // Helper method to get field confidence
    func getFieldConfidence(for field: String) -> String {
        guard let currentExpense = currentVoiceExpense,
              let confidence = currentExpense.fieldConfidences[field] else {
            return "0%"
        }
        return formatConfidence(confidence)
    }
    
    // Helper method to convert amount from dollars to minor units
    func convertToMinorUnits(_ amount: Double) -> Int64 {
        return Int64(amount * 100)
    }
}