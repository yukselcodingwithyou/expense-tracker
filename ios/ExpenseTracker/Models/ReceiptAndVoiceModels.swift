import Foundation

// MARK: - Receipt Models
struct ReceiptData: Codable, Identifiable {
    let id: String?
    let userId: String?
    let storeName: String?
    let storeAddress: String?
    let totalAmount: Double
    let currency: String
    let date: String? // ISO string
    let items: [ReceiptItem]
    let suggestedCategory: String?
    let confidence: Double
    let metadata: [String: String]
    let attachmentId: String?
    let createdAt: String? // ISO string
    
    init(id: String? = nil,
         userId: String? = nil,
         storeName: String? = nil,
         storeAddress: String? = nil,
         totalAmount: Double = 0.0,
         currency: String = "USD",
         date: String? = nil,
         items: [ReceiptItem] = [],
         suggestedCategory: String? = nil,
         confidence: Double = 0.0,
         metadata: [String: String] = [:],
         attachmentId: String? = nil,
         createdAt: String? = nil) {
        self.id = id
        self.userId = userId
        self.storeName = storeName
        self.storeAddress = storeAddress
        self.totalAmount = totalAmount
        self.currency = currency
        self.date = date
        self.items = items
        self.suggestedCategory = suggestedCategory
        self.confidence = confidence
        self.metadata = metadata
        self.attachmentId = attachmentId
        self.createdAt = createdAt
    }
}

struct ReceiptItem: Codable, Identifiable {
    let id = UUID()
    let name: String
    let price: Double
    let quantity: Int
    let category: String?
    
    private enum CodingKeys: String, CodingKey {
        case name, price, quantity, category
    }
    
    init(name: String, price: Double, quantity: Int = 1, category: String? = nil) {
        self.name = name
        self.price = price
        self.quantity = quantity
        self.category = category
    }
}

struct CreateExpenseFromReceiptRequest: Codable {
    let receiptDataId: String
    let amount: Double
    let categoryId: String
    let storeName: String?
    let date: String? // ISO string
    let items: [ReceiptItem]
    let description: String?
}

// MARK: - Voice Expense Models
struct VoiceExpenseData: Codable, Identifiable {
    let id: String?
    let userId: String?
    let originalText: String
    let parseResult: ExpenseParseResult
    let overallConfidence: Double
    let suggestions: [String]
    let fieldConfidences: [String: Double]
    let createdAt: String? // ISO string
    
    init(id: String? = nil,
         userId: String? = nil,
         originalText: String,
         parseResult: ExpenseParseResult,
         overallConfidence: Double = 0.0,
         suggestions: [String] = [],
         fieldConfidences: [String: Double] = [:],
         createdAt: String? = nil) {
        self.id = id
        self.userId = userId
        self.originalText = originalText
        self.parseResult = parseResult
        self.overallConfidence = overallConfidence
        self.suggestions = suggestions
        self.fieldConfidences = fieldConfidences
        self.createdAt = createdAt
    }
}

struct ExpenseParseResult: Codable {
    let amountMinor: Int64?
    let currency: String
    let categoryId: String?
    let description: String
    let occurredAt: String? // ISO string
    let merchant: String?
    let confidence: Double
    
    init(amountMinor: Int64? = nil,
         currency: String = "USD",
         categoryId: String? = nil,
         description: String = "",
         occurredAt: String? = nil,
         merchant: String? = nil,
         confidence: Double = 0.0) {
        self.amountMinor = amountMinor
        self.currency = currency
        self.categoryId = categoryId
        self.description = description
        self.occurredAt = occurredAt
        self.merchant = merchant
        self.confidence = confidence
    }
}

struct VoiceExpenseRequest: Codable {
    let spokenText: String
    let preferredCurrency: String
    
    init(spokenText: String, preferredCurrency: String = "USD") {
        self.spokenText = spokenText
        self.preferredCurrency = preferredCurrency
    }
}

struct CreateExpenseFromVoiceRequest: Codable {
    let voiceExpenseDataId: String
    let amountMinor: Int64
    let currency: String
    let categoryId: String
    let description: String
    let merchant: String?
}

// MARK: - Response Models
struct LedgerEntryResponse: Codable, Identifiable {
    let id: String
    let memberId: String
    let type: String // "EXPENSE" or "INCOME"
    let amountMinor: Int64
    let currency: String
    let categoryId: String
    let categoryName: String?
    let occurredAt: String // ISO timestamp
    let notes: String?
    let attachments: [String]
    let recurringId: String?
    let createdAt: String // ISO timestamp
    let updatedAt: String // ISO timestamp
    
    init(id: String,
         memberId: String,
         type: String,
         amountMinor: Int64,
         currency: String,
         categoryId: String,
         categoryName: String? = nil,
         occurredAt: String,
         notes: String? = nil,
         attachments: [String] = [],
         recurringId: String? = nil,
         createdAt: String,
         updatedAt: String) {
        self.id = id
        self.memberId = memberId
        self.type = type
        self.amountMinor = amountMinor
        self.currency = currency
        self.categoryId = categoryId
        self.categoryName = categoryName
        self.occurredAt = occurredAt
        self.notes = notes
        self.attachments = attachments
        self.recurringId = recurringId
        self.createdAt = createdAt
        self.updatedAt = updatedAt
    }
}