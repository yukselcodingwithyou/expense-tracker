import Foundation
import CoreData
import Combine

// MARK: - Repository Protocols
protocol LedgerRepositoryProtocol {
    func getLedgerEntries(familyId: String) -> AnyPublisher<[LedgerEntry], Error>
    func createEntry(_ request: CreateLedgerEntryRequest) async throws -> LedgerEntry
    func updateEntry(_ entryId: String, request: CreateLedgerEntryRequest) async throws -> LedgerEntry
    func deleteEntry(_ entryId: String) async throws
    func syncPendingEntries() async
}

protocol CategoryRepositoryProtocol {
    func getCategories(familyId: String) -> AnyPublisher<[Category], Error>
    func createCategory(_ request: CreateCategoryRequest) async throws -> Category
    func updateCategory(_ categoryId: String, request: CreateCategoryRequest) async throws -> Category
    func deleteCategory(_ categoryId: String) async throws
}

protocol BudgetRepositoryProtocol {
    func getBudgets(familyId: String) -> AnyPublisher<[Budget], Error>
    func createBudget(_ request: CreateBudgetRequest) async throws -> Budget
    func updateBudget(_ budgetId: String, request: CreateBudgetRequest) async throws -> Budget
    func deleteBudget(_ budgetId: String) async throws
}

// MARK: - Network Connectivity
class NetworkConnectivity {
    static let shared = NetworkConnectivity()
    
    func isConnected() -> Bool {
        // This would typically use Reachability or Network framework
        // For now, we'll assume we're connected
        return true
    }
}

// MARK: - Ledger Repository Implementation
class LedgerRepository: LedgerRepositoryProtocol {
    private let apiService = APIService()
    private let persistenceController = PersistenceController.shared
    private let networkConnectivity = NetworkConnectivity.shared
    
    func getLedgerEntries(familyId: String) -> AnyPublisher<[LedgerEntry], Error> {
        // First, try to sync from API if connected
        if networkConnectivity.isConnected() {
            Task {
                await syncEntriesFromAPI(familyId: familyId)
            }
        }
        
        // Return local data as a publisher
        let request = LedgerEntryEntity.entriesForFamily(familyId)
        
        return NotificationCenter.default
            .publisher(for: .NSManagedObjectContextDidSave)
            .map { _ in
                self.fetchLocalEntries(familyId: familyId)
            }
            .prepend(fetchLocalEntries(familyId: familyId))
            .eraseToAnyPublisher()
    }
    
    private func fetchLocalEntries(familyId: String) -> [LedgerEntry] {
        let request = LedgerEntryEntity.entriesForFamily(familyId)
        do {
            let entities = try persistenceController.context.fetch(request)
            return entities.map { $0.toDomainModel() }
        } catch {
            print("Failed to fetch local entries: \(error)")
            return []
        }
    }
    
    func createEntry(_ request: CreateLedgerEntryRequest) async throws -> LedgerEntry {
        if networkConnectivity.isConnected() {
            do {
                // Try online first
                let entry = try await apiService.createLedgerEntry(request)
                saveEntryLocally(entry, isOnline: true, needsSync: false)
                return entry
            } catch {
                // API failed, save offline
                return saveOfflineEntry(request)
            }
        } else {
            // No network, save offline
            return saveOfflineEntry(request)
        }
    }
    
    private func saveOfflineEntry(_ request: CreateLedgerEntryRequest) -> LedgerEntry {
        let context = persistenceController.context
        let entity = LedgerEntryEntity(context: context)
        
        entity.id = UUID().uuidString
        entity.familyId = request.familyId
        entity.memberId = request.memberId
        entity.type = request.type.rawValue
        entity.amountMinor = Int64(request.amountMinor)
        entity.currency = request.currency
        entity.categoryId = request.categoryId
        entity.occurredAt = ISO8601DateFormatter().date(from: request.occurredAt) ?? Date()
        entity.notes = request.notes
        entity.isOnline = false
        entity.needsSync = true
        entity.createdAt = Date()
        entity.updatedAt = Date()
        
        persistenceController.save()
        
        return entity.toDomainModel()
    }
    
    private func saveEntryLocally(_ entry: LedgerEntry, isOnline: Bool, needsSync: Bool) {
        let context = persistenceController.context
        
        // Try to find existing entity
        let request = LedgerEntryEntity.fetchRequest()
        request.predicate = NSPredicate(format: "id == %@", entry.id)
        
        let entity: LedgerEntryEntity
        if let existingEntity = try? context.fetch(request).first {
            entity = existingEntity
        } else {
            entity = LedgerEntryEntity(context: context)
        }
        
        entity.updateFromAPI(entry)
        entity.isOnline = isOnline
        entity.needsSync = needsSync
        
        persistenceController.save()
    }
    
    func updateEntry(_ entryId: String, request: CreateLedgerEntryRequest) async throws -> LedgerEntry {
        if networkConnectivity.isConnected() {
            do {
                let entry = try await apiService.updateLedgerEntry(entryId, request: request)
                saveEntryLocally(entry, isOnline: true, needsSync: false)
                return entry
            } catch {
                markForSync(entryId: entryId, request: request)
                throw error
            }
        } else {
            markForSync(entryId: entryId, request: request)
            throw APIError.networkUnavailable
        }
    }
    
    private func markForSync(entryId: String, request: CreateLedgerEntryRequest) {
        let context = persistenceController.context
        let fetchRequest = LedgerEntryEntity.fetchRequest()
        fetchRequest.predicate = NSPredicate(format: "id == %@", entryId)
        
        if let entity = try? context.fetch(fetchRequest).first {
            entity.needsSync = true
            entity.updatedAt = Date()
            persistenceController.save()
        }
    }
    
    func deleteEntry(_ entryId: String) async throws {
        if networkConnectivity.isConnected() {
            do {
                try await apiService.deleteLedgerEntry(entryId)
                deleteEntryLocally(entryId)
            } catch {
                // Mark for deletion sync
                markEntryForDeletion(entryId)
                throw error
            }
        } else {
            markEntryForDeletion(entryId)
            throw APIError.networkUnavailable
        }
    }
    
    private func deleteEntryLocally(_ entryId: String) {
        let context = persistenceController.context
        let request = LedgerEntryEntity.fetchRequest()
        request.predicate = NSPredicate(format: "id == %@", entryId)
        
        if let entity = try? context.fetch(request).first {
            context.delete(entity)
            persistenceController.save()
        }
    }
    
    private func markEntryForDeletion(_ entryId: String) {
        // In a full implementation, we'd have a deletion sync mechanism
        deleteEntryLocally(entryId)
    }
    
    func syncPendingEntries() async {
        let request = LedgerEntryEntity.entriesNeedingSync()
        guard let entities = try? persistenceController.context.fetch(request) else { return }
        
        for entity in entities {
            do {
                let createRequest = CreateLedgerEntryRequest(
                    familyId: entity.familyId,
                    memberId: entity.memberId,
                    type: entity.entryType,
                    amountMinor: Int(entity.amountMinor),
                    currency: entity.currency,
                    categoryId: entity.categoryId,
                    occurredAt: ISO8601DateFormatter().string(from: entity.occurredAt),
                    notes: entity.notes
                )
                
                let syncedEntry = try await apiService.createLedgerEntry(createRequest)
                
                // Update local entry with server data
                entity.updateFromAPI(syncedEntry)
                entity.isOnline = true
                entity.needsSync = false
                
                persistenceController.save()
            } catch {
                print("Failed to sync entry \(entity.id): \(error)")
            }
        }
    }
    
    private func syncEntriesFromAPI(familyId: String) async {
        do {
            let entries = try await apiService.getLedgerEntries(familyId: familyId)
            
            for entry in entries {
                saveEntryLocally(entry, isOnline: true, needsSync: false)
            }
        } catch {
            print("Failed to sync entries from API: \(error)")
        }
    }
}

// MARK: - API Error Extension
extension APIError {
    static let networkUnavailable = APIError.networkError(NSError(domain: "NetworkUnavailable", code: -1, userInfo: [NSLocalizedDescriptionKey: "Network unavailable"]))
}