import CoreData
import Foundation

// MARK: - Core Data Entity Base Class
@objc(BaseEntity)
public class BaseEntity: NSManagedObject {
    @NSManaged public var id: String
    @NSManaged public var isOnline: Bool
    @NSManaged public var needsSync: Bool
    @NSManaged public var createdAt: Date
    @NSManaged public var updatedAt: Date
}

// MARK: - Ledger Entry Entity
@objc(LedgerEntryEntity)
public class LedgerEntryEntity: BaseEntity {
    @NSManaged public var familyId: String
    @NSManaged public var memberId: String
    @NSManaged public var type: String // INCOME, EXPENSE
    @NSManaged public var amountMinor: Int64
    @NSManaged public var currency: String
    @NSManaged public var categoryId: String
    @NSManaged public var occurredAt: Date
    @NSManaged public var notes: String?
    
    // Convenience computed properties
    var amount: Double {
        return Double(amountMinor) / 100.0
    }
    
    var entryType: LedgerEntryType {
        return LedgerEntryType(rawValue: type) ?? .expense
    }
}

// MARK: - Category Entity
@objc(CategoryEntity)
public class CategoryEntity: BaseEntity {
    @NSManaged public var familyId: String
    @NSManaged public var name: String
    @NSManaged public var icon: String
    @NSManaged public var color: String
    @NSManaged public var type: String // INCOME, EXPENSE
    @NSManaged public var archived: Bool
    
    var categoryType: LedgerEntryType {
        return LedgerEntryType(rawValue: type) ?? .expense
    }
}

// MARK: - Budget Entity
@objc(BudgetEntity)
public class BudgetEntity: BaseEntity {
    @NSManaged public var familyId: String
    @NSManaged public var name: String
    @NSManaged public var overallLimitMinor: Int64
    @NSManaged public var periodType: String // MONTHLY, WEEKLY, YEARLY
    @NSManaged public var periodStart: Date
    @NSManaged public var periodEnd: Date
    @NSManaged public var alertThresholdPct: Double
    @NSManaged public var includeRecurring: Bool
    
    var overallLimit: Double {
        return Double(overallLimitMinor) / 100.0
    }
}

// MARK: - Notification Entity
@objc(NotificationEntity)
public class NotificationEntity: BaseEntity {
    @NSManaged public var userId: String
    @NSManaged public var title: String
    @NSManaged public var message: String
    @NSManaged public var type: String
    @NSManaged public var isRead: Bool
    @NSManaged public var actionUrl: String?
}

// MARK: - Data Model Extensions for API Conversion
extension LedgerEntryEntity {
    func toDomainModel() -> LedgerEntry {
        return LedgerEntry(
            id: self.id,
            familyId: self.familyId,
            memberId: self.memberId,
            type: self.entryType,
            amountMinor: Int(self.amountMinor),
            currency: self.currency,
            categoryId: self.categoryId,
            occurredAt: ISO8601DateFormatter().string(from: self.occurredAt),
            notes: self.notes
        )
    }
    
    func updateFromAPI(_ apiModel: LedgerEntry) {
        self.id = apiModel.id
        self.familyId = apiModel.familyId
        self.memberId = apiModel.memberId
        self.type = apiModel.type.rawValue
        self.amountMinor = Int64(apiModel.amountMinor)
        self.currency = apiModel.currency
        self.categoryId = apiModel.categoryId
        self.occurredAt = ISO8601DateFormatter().date(from: apiModel.occurredAt) ?? Date()
        self.notes = apiModel.notes
        self.updatedAt = Date()
        self.isOnline = true
        self.needsSync = false
    }
}

extension CategoryEntity {
    func toDomainModel() -> Category {
        return Category(
            id: self.id,
            familyId: self.familyId,
            name: self.name,
            icon: self.icon,
            color: self.color,
            type: self.categoryType,
            archived: self.archived
        )
    }
    
    func updateFromAPI(_ apiModel: Category) {
        self.id = apiModel.id
        self.familyId = apiModel.familyId
        self.name = apiModel.name
        self.icon = apiModel.icon
        self.color = apiModel.color
        self.type = apiModel.type.rawValue
        self.archived = apiModel.archived
        self.updatedAt = Date()
        self.isOnline = true
        self.needsSync = false
    }
}

extension BudgetEntity {
    func toDomainModel() -> Budget {
        return Budget(
            id: self.id,
            familyId: self.familyId,
            name: self.name,
            overallLimitMinor: Int(self.overallLimitMinor),
            periodType: BudgetPeriodType(rawValue: self.periodType) ?? .monthly,
            periodStart: ISO8601DateFormatter().string(from: self.periodStart),
            periodEnd: ISO8601DateFormatter().string(from: self.periodEnd),
            alertThresholdPct: self.alertThresholdPct,
            includeRecurring: self.includeRecurring
        )
    }
    
    func updateFromAPI(_ apiModel: Budget) {
        self.id = apiModel.id
        self.familyId = apiModel.familyId
        self.name = apiModel.name
        self.overallLimitMinor = Int64(apiModel.overallLimitMinor)
        self.periodType = apiModel.periodType.rawValue
        self.periodStart = ISO8601DateFormatter().date(from: apiModel.periodStart) ?? Date()
        self.periodEnd = ISO8601DateFormatter().date(from: apiModel.periodEnd) ?? Date()
        self.alertThresholdPct = apiModel.alertThresholdPct
        self.includeRecurring = apiModel.includeRecurring
        self.updatedAt = Date()
        self.isOnline = true
        self.needsSync = false
    }
}