import Foundation
import SwiftUI
import Combine

@MainActor
class ReceiptManager: ObservableObject {
    @Published var isProcessing = false
    @Published var isCreatingExpense = false
    @Published var currentReceipt: ReceiptData?
    @Published var showReceiptDetails = false
    @Published var expenseCreated = false
    @Published var createdExpense: LedgerEntryResponse?
    @Published var receipts: [ReceiptData] = []
    @Published var error: String?
    
    private let receiptAPIService = ReceiptAPIService()
    
    func processReceiptImage(_ image: UIImage) {
        isProcessing = true
        error = nil
        
        Task {
            do {
                let receiptData = try await receiptAPIService.processReceipt(image: image)
                
                DispatchQueue.main.async {
                    self.isProcessing = false
                    self.currentReceipt = receiptData
                    self.showReceiptDetails = true
                }
            } catch {
                DispatchQueue.main.async {
                    self.isProcessing = false
                    self.error = error.localizedDescription
                }
            }
        }
    }
    
    func createExpenseFromReceipt(_ request: CreateExpenseFromReceiptRequest) {
        isCreatingExpense = true
        error = nil
        
        Task {
            do {
                let ledgerEntry = try await receiptAPIService.createExpenseFromReceipt(request)
                
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
    
    func loadUserReceipts() {
        Task {
            do {
                let receiptList = try await receiptAPIService.getUserReceipts()
                
                DispatchQueue.main.async {
                    self.receipts = receiptList
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
    
    func clearReceiptDetails() {
        showReceiptDetails = false
        currentReceipt = nil
        expenseCreated = false
        createdExpense = nil
    }
    
    // Helper method to format amount for display
    func formatAmount(_ amount: Double) -> String {
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
}