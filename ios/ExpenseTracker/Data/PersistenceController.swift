import CoreData
import Foundation

// MARK: - Core Data Stack
class PersistenceController: ObservableObject {
    static let shared = PersistenceController()
    
    lazy var container: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "ExpenseTracker")
        
        container.loadPersistentStores { [weak self] _, error in
            if let error = error as NSError? {
                // In production, this would be better handled with proper error reporting
                print("Core Data error: \(error), \(error.userInfo)")
            }
        }
        
        container.viewContext.automaticallyMergesChangesFromParent = true
        
        return container
    }()
    
    var context: NSManagedObjectContext {
        return container.viewContext
    }
    
    func save() {
        let context = container.viewContext
        
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                print("Save error: \(error)")
            }
        }
    }
}

// MARK: - Core Data Entity Extensions
extension LedgerEntryEntity {
    static func fetchRequest() -> NSFetchRequest<LedgerEntryEntity> {
        return NSFetchRequest<LedgerEntryEntity>(entityName: "LedgerEntryEntity")
    }
    
    static func allEntries() -> NSFetchRequest<LedgerEntryEntity> {
        let request: NSFetchRequest<LedgerEntryEntity> = LedgerEntryEntity.fetchRequest()
        request.sortDescriptors = [NSSortDescriptor(keyPath: \LedgerEntryEntity.occurredAt, ascending: false)]
        return request
    }
    
    static func entriesForFamily(_ familyId: String) -> NSFetchRequest<LedgerEntryEntity> {
        let request: NSFetchRequest<LedgerEntryEntity> = LedgerEntryEntity.fetchRequest()
        request.predicate = NSPredicate(format: "familyId == %@", familyId)
        request.sortDescriptors = [NSSortDescriptor(keyPath: \LedgerEntryEntity.occurredAt, ascending: false)]
        return request
    }
    
    static func entriesNeedingSync() -> NSFetchRequest<LedgerEntryEntity> {
        let request: NSFetchRequest<LedgerEntryEntity> = LedgerEntryEntity.fetchRequest()
        request.predicate = NSPredicate(format: "needsSync == true")
        return request
    }
}

extension CategoryEntity {
    static func fetchRequest() -> NSFetchRequest<CategoryEntity> {
        return NSFetchRequest<CategoryEntity>(entityName: "CategoryEntity")
    }
    
    static func categoriesForFamily(_ familyId: String) -> NSFetchRequest<CategoryEntity> {
        let request: NSFetchRequest<CategoryEntity> = CategoryEntity.fetchRequest()
        request.predicate = NSPredicate(format: "familyId == %@ AND archived == false", familyId)
        request.sortDescriptors = [NSSortDescriptor(keyPath: \CategoryEntity.name, ascending: true)]
        return request
    }
}

extension BudgetEntity {
    static func fetchRequest() -> NSFetchRequest<BudgetEntity> {
        return NSFetchRequest<BudgetEntity>(entityName: "BudgetEntity")
    }
    
    static func budgetsForFamily(_ familyId: String) -> NSFetchRequest<BudgetEntity> {
        let request: NSFetchRequest<BudgetEntity> = BudgetEntity.fetchRequest()
        request.predicate = NSPredicate(format: "familyId == %@", familyId)
        request.sortDescriptors = [NSSortDescriptor(keyPath: \BudgetEntity.name, ascending: true)]
        return request
    }
}