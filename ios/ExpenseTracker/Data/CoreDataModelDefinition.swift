import Foundation
import CoreData

// MARK: - Core Data Model Generator
// This file contains the programmatic Core Data model creation
// In a real iOS project, you would create ExpenseTracker.xcdatamodeld using Xcode's Data Model Editor

extension PersistenceController {
    
    // This method would be used to create the NSManagedObjectModel programmatically
    // In practice, you'd create a .xcdatamodeld file in Xcode instead
    static func createModel() -> NSManagedObjectModel {
        let model = NSManagedObjectModel()
        
        // Create entities
        let ledgerEntity = createLedgerEntryEntity()
        let categoryEntity = createCategoryEntity()
        let budgetEntity = createBudgetEntity()
        let notificationEntity = createNotificationEntity()
        
        model.entities = [ledgerEntity, categoryEntity, budgetEntity, notificationEntity]
        
        return model
    }
    
    private static func createLedgerEntryEntity() -> NSEntityDescription {
        let entity = NSEntityDescription()
        entity.name = "LedgerEntryEntity"
        entity.managedObjectClassName = "LedgerEntryEntity"
        
        // Create attributes
        var properties: [NSPropertyDescription] = []
        
        // Base entity properties
        properties.append(createAttribute(name: "id", type: .stringAttributeType))
        properties.append(createAttribute(name: "isOnline", type: .booleanAttributeType))
        properties.append(createAttribute(name: "needsSync", type: .booleanAttributeType))
        properties.append(createAttribute(name: "createdAt", type: .dateAttributeType))
        properties.append(createAttribute(name: "updatedAt", type: .dateAttributeType))
        
        // Ledger specific properties
        properties.append(createAttribute(name: "familyId", type: .stringAttributeType))
        properties.append(createAttribute(name: "memberId", type: .stringAttributeType))
        properties.append(createAttribute(name: "type", type: .stringAttributeType))
        properties.append(createAttribute(name: "amountMinor", type: .integer64AttributeType))
        properties.append(createAttribute(name: "currency", type: .stringAttributeType))
        properties.append(createAttribute(name: "categoryId", type: .stringAttributeType))
        properties.append(createAttribute(name: "occurredAt", type: .dateAttributeType))
        properties.append(createOptionalAttribute(name: "notes", type: .stringAttributeType))
        
        entity.properties = properties
        
        return entity
    }
    
    private static func createCategoryEntity() -> NSEntityDescription {
        let entity = NSEntityDescription()
        entity.name = "CategoryEntity"
        entity.managedObjectClassName = "CategoryEntity"
        
        var properties: [NSPropertyDescription] = []
        
        // Base entity properties
        properties.append(createAttribute(name: "id", type: .stringAttributeType))
        properties.append(createAttribute(name: "isOnline", type: .booleanAttributeType))
        properties.append(createAttribute(name: "needsSync", type: .booleanAttributeType))
        properties.append(createAttribute(name: "createdAt", type: .dateAttributeType))
        properties.append(createAttribute(name: "updatedAt", type: .dateAttributeType))
        
        // Category specific properties
        properties.append(createAttribute(name: "familyId", type: .stringAttributeType))
        properties.append(createAttribute(name: "name", type: .stringAttributeType))
        properties.append(createAttribute(name: "icon", type: .stringAttributeType))
        properties.append(createAttribute(name: "color", type: .stringAttributeType))
        properties.append(createAttribute(name: "type", type: .stringAttributeType))
        properties.append(createAttribute(name: "archived", type: .booleanAttributeType))
        
        entity.properties = properties
        
        return entity
    }
    
    private static func createBudgetEntity() -> NSEntityDescription {
        let entity = NSEntityDescription()
        entity.name = "BudgetEntity"
        entity.managedObjectClassName = "BudgetEntity"
        
        var properties: [NSPropertyDescription] = []
        
        // Base entity properties
        properties.append(createAttribute(name: "id", type: .stringAttributeType))
        properties.append(createAttribute(name: "isOnline", type: .booleanAttributeType))
        properties.append(createAttribute(name: "needsSync", type: .booleanAttributeType))
        properties.append(createAttribute(name: "createdAt", type: .dateAttributeType))
        properties.append(createAttribute(name: "updatedAt", type: .dateAttributeType))
        
        // Budget specific properties
        properties.append(createAttribute(name: "familyId", type: .stringAttributeType))
        properties.append(createAttribute(name: "name", type: .stringAttributeType))
        properties.append(createAttribute(name: "overallLimitMinor", type: .integer64AttributeType))
        properties.append(createAttribute(name: "periodType", type: .stringAttributeType))
        properties.append(createAttribute(name: "periodStart", type: .dateAttributeType))
        properties.append(createAttribute(name: "periodEnd", type: .dateAttributeType))
        properties.append(createAttribute(name: "alertThresholdPct", type: .doubleAttributeType))
        properties.append(createAttribute(name: "includeRecurring", type: .booleanAttributeType))
        
        entity.properties = properties
        
        return entity
    }
    
    private static func createNotificationEntity() -> NSEntityDescription {
        let entity = NSEntityDescription()
        entity.name = "NotificationEntity"
        entity.managedObjectClassName = "NotificationEntity"
        
        var properties: [NSPropertyDescription] = []
        
        // Base entity properties
        properties.append(createAttribute(name: "id", type: .stringAttributeType))
        properties.append(createAttribute(name: "isOnline", type: .booleanAttributeType))
        properties.append(createAttribute(name: "needsSync", type: .booleanAttributeType))
        properties.append(createAttribute(name: "createdAt", type: .dateAttributeType))
        properties.append(createAttribute(name: "updatedAt", type: .dateAttributeType))
        
        // Notification specific properties
        properties.append(createAttribute(name: "userId", type: .stringAttributeType))
        properties.append(createAttribute(name: "title", type: .stringAttributeType))
        properties.append(createAttribute(name: "message", type: .stringAttributeType))
        properties.append(createAttribute(name: "type", type: .stringAttributeType))
        properties.append(createAttribute(name: "isRead", type: .booleanAttributeType))
        properties.append(createOptionalAttribute(name: "actionUrl", type: .stringAttributeType))
        
        entity.properties = properties
        
        return entity
    }
    
    private static func createAttribute(name: String, type: NSAttributeType) -> NSAttributeDescription {
        let attribute = NSAttributeDescription()
        attribute.name = name
        attribute.attributeType = type
        attribute.isOptional = false
        return attribute
    }
    
    private static func createOptionalAttribute(name: String, type: NSAttributeType) -> NSAttributeDescription {
        let attribute = NSAttributeDescription()
        attribute.name = name
        attribute.attributeType = type
        attribute.isOptional = true
        return attribute
    }
}

// MARK: - Instructions for .xcdatamodeld file
/*
 To complete the iOS Core Data setup, you would need to:
 
 1. Create a new Data Model file in Xcode:
    - File > New > File > Data Model
    - Name it "ExpenseTracker.xcdatamodeld"
 
 2. Add the following entities with their attributes:
 
 LedgerEntryEntity:
 - id: String
 - isOnline: Boolean
 - needsSync: Boolean  
 - createdAt: Date
 - updatedAt: Date
 - familyId: String
 - memberId: String
 - type: String
 - amountMinor: Integer 64
 - currency: String
 - categoryId: String
 - occurredAt: Date
 - notes: String (Optional)
 
 CategoryEntity:
 - id: String
 - isOnline: Boolean
 - needsSync: Boolean
 - createdAt: Date
 - updatedAt: Date
 - familyId: String
 - name: String
 - icon: String
 - color: String
 - type: String
 - archived: Boolean
 
 BudgetEntity:
 - id: String
 - isOnline: Boolean
 - needsSync: Boolean
 - createdAt: Date
 - updatedAt: Date
 - familyId: String
 - name: String
 - overallLimitMinor: Integer 64
 - periodType: String
 - periodStart: Date
 - periodEnd: Date
 - alertThresholdPct: Double
 - includeRecurring: Boolean
 
 NotificationEntity:
 - id: String
 - isOnline: Boolean
 - needsSync: Boolean
 - createdAt: Date
 - updatedAt: Date
 - userId: String
 - title: String
 - message: String
 - type: String
 - isRead: Boolean
 - actionUrl: String (Optional)
 
 3. Set the Codegen to "Manual/None" for each entity
 4. Use the classes defined in CoreDataEntities.swift
 */