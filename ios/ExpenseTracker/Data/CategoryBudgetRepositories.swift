import Foundation
import CoreData
import Combine

// MARK: - Category Repository Implementation
class CategoryRepository: CategoryRepositoryProtocol {
    private let apiService = APIService()
    private let persistenceController = PersistenceController.shared
    private let networkConnectivity = NetworkConnectivity.shared
    
    func getCategories(familyId: String) -> AnyPublisher<[Category], Error> {
        // Sync from API if connected
        if networkConnectivity.isConnected() {
            Task {
                await syncCategoriesFromAPI(familyId: familyId)
            }
        }
        
        return NotificationCenter.default
            .publisher(for: .NSManagedObjectContextDidSave)
            .map { _ in
                self.fetchLocalCategories(familyId: familyId)
            }
            .prepend(fetchLocalCategories(familyId: familyId))
            .eraseToAnyPublisher()
    }
    
    private func fetchLocalCategories(familyId: String) -> [Category] {
        let request = CategoryEntity.categoriesForFamily(familyId)
        do {
            let entities = try persistenceController.context.fetch(request)
            return entities.map { $0.toDomainModel() }
        } catch {
            print("Failed to fetch local categories: \(error)")
            return []
        }
    }
    
    func createCategory(_ request: CreateCategoryRequest) async throws -> Category {
        if networkConnectivity.isConnected() {
            do {
                let category = try await apiService.createCategory(request)
                saveCategoryLocally(category, isOnline: true, needsSync: false)
                return category
            } catch {
                return saveOfflineCategory(request)
            }
        } else {
            return saveOfflineCategory(request)
        }
    }
    
    private func saveOfflineCategory(_ request: CreateCategoryRequest) -> Category {
        let context = persistenceController.context
        let entity = CategoryEntity(context: context)
        
        entity.id = UUID().uuidString
        entity.familyId = request.familyId
        entity.name = request.name
        entity.icon = request.icon
        entity.color = request.color
        entity.type = request.type.rawValue
        entity.archived = false
        entity.isOnline = false
        entity.needsSync = true
        entity.createdAt = Date()
        entity.updatedAt = Date()
        
        persistenceController.save()
        
        return entity.toDomainModel()
    }
    
    private func saveCategoryLocally(_ category: Category, isOnline: Bool, needsSync: Bool) {
        let context = persistenceController.context
        
        let request = CategoryEntity.fetchRequest()
        request.predicate = NSPredicate(format: "id == %@", category.id)
        
        let entity: CategoryEntity
        if let existingEntity = try? context.fetch(request).first {
            entity = existingEntity
        } else {
            entity = CategoryEntity(context: context)
        }
        
        entity.updateFromAPI(category)
        entity.isOnline = isOnline
        entity.needsSync = needsSync
        
        persistenceController.save()
    }
    
    func updateCategory(_ categoryId: String, request: CreateCategoryRequest) async throws -> Category {
        if networkConnectivity.isConnected() {
            do {
                let category = try await apiService.updateCategory(categoryId, request: request)
                saveCategoryLocally(category, isOnline: true, needsSync: false)
                return category
            } catch {
                markCategoryForSync(categoryId: categoryId)
                throw error
            }
        } else {
            markCategoryForSync(categoryId: categoryId)
            throw APIError.networkUnavailable
        }
    }
    
    private func markCategoryForSync(categoryId: String) {
        let context = persistenceController.context
        let fetchRequest = CategoryEntity.fetchRequest()
        fetchRequest.predicate = NSPredicate(format: "id == %@", categoryId)
        
        if let entity = try? context.fetch(fetchRequest).first {
            entity.needsSync = true
            entity.updatedAt = Date()
            persistenceController.save()
        }
    }
    
    func deleteCategory(_ categoryId: String) async throws {
        if networkConnectivity.isConnected() {
            do {
                try await apiService.deleteCategory(categoryId)
                deleteCategoryLocally(categoryId)
            } catch {
                markCategoryForDeletion(categoryId)
                throw error
            }
        } else {
            markCategoryForDeletion(categoryId)
            throw APIError.networkUnavailable
        }
    }
    
    private func deleteCategoryLocally(_ categoryId: String) {
        let context = persistenceController.context
        let request = CategoryEntity.fetchRequest()
        request.predicate = NSPredicate(format: "id == %@", categoryId)
        
        if let entity = try? context.fetch(request).first {
            context.delete(entity)
            persistenceController.save()
        }
    }
    
    private func markCategoryForDeletion(_ categoryId: String) {
        deleteCategoryLocally(categoryId)
    }
    
    private func syncCategoriesFromAPI(familyId: String) async {
        do {
            let categories = try await apiService.getCategories(familyId: familyId)
            
            for category in categories {
                saveCategoryLocally(category, isOnline: true, needsSync: false)
            }
        } catch {
            print("Failed to sync categories from API: \(error)")
        }
    }
}

// MARK: - Budget Repository Implementation
class BudgetRepository: BudgetRepositoryProtocol {
    private let apiService = APIService()
    private let persistenceController = PersistenceController.shared
    private let networkConnectivity = NetworkConnectivity.shared
    
    func getBudgets(familyId: String) -> AnyPublisher<[Budget], Error> {
        // Sync from API if connected
        if networkConnectivity.isConnected() {
            Task {
                await syncBudgetsFromAPI(familyId: familyId)
            }
        }
        
        return NotificationCenter.default
            .publisher(for: .NSManagedObjectContextDidSave)
            .map { _ in
                self.fetchLocalBudgets(familyId: familyId)
            }
            .prepend(fetchLocalBudgets(familyId: familyId))
            .eraseToAnyPublisher()
    }
    
    private func fetchLocalBudgets(familyId: String) -> [Budget] {
        let request = BudgetEntity.budgetsForFamily(familyId)
        do {
            let entities = try persistenceController.context.fetch(request)
            return entities.map { $0.toDomainModel() }
        } catch {
            print("Failed to fetch local budgets: \(error)")
            return []
        }
    }
    
    func createBudget(_ request: CreateBudgetRequest) async throws -> Budget {
        if networkConnectivity.isConnected() {
            do {
                let budget = try await apiService.createBudget(request)
                saveBudgetLocally(budget, isOnline: true, needsSync: false)
                return budget
            } catch {
                return saveOfflineBudget(request)
            }
        } else {
            return saveOfflineBudget(request)
        }
    }
    
    private func saveOfflineBudget(_ request: CreateBudgetRequest) -> Budget {
        let context = persistenceController.context
        let entity = BudgetEntity(context: context)
        
        entity.id = UUID().uuidString
        entity.familyId = request.familyId
        entity.name = request.name
        entity.overallLimitMinor = Int64(request.overallLimitMinor)
        entity.periodType = request.periodType.rawValue
        entity.periodStart = ISO8601DateFormatter().date(from: request.periodStart) ?? Date()
        entity.periodEnd = ISO8601DateFormatter().date(from: request.periodEnd) ?? Date()
        entity.alertThresholdPct = request.alertThresholdPct
        entity.includeRecurring = request.includeRecurring
        entity.isOnline = false
        entity.needsSync = true
        entity.createdAt = Date()
        entity.updatedAt = Date()
        
        persistenceController.save()
        
        return entity.toDomainModel()
    }
    
    private func saveBudgetLocally(_ budget: Budget, isOnline: Bool, needsSync: Bool) {
        let context = persistenceController.context
        
        let request = BudgetEntity.fetchRequest()
        request.predicate = NSPredicate(format: "id == %@", budget.id)
        
        let entity: BudgetEntity
        if let existingEntity = try? context.fetch(request).first {
            entity = existingEntity
        } else {
            entity = BudgetEntity(context: context)
        }
        
        entity.updateFromAPI(budget)
        entity.isOnline = isOnline
        entity.needsSync = needsSync
        
        persistenceController.save()
    }
    
    func updateBudget(_ budgetId: String, request: CreateBudgetRequest) async throws -> Budget {
        if networkConnectivity.isConnected() {
            do {
                let budget = try await apiService.updateBudget(budgetId, request: request)
                saveBudgetLocally(budget, isOnline: true, needsSync: false)
                return budget
            } catch {
                markBudgetForSync(budgetId: budgetId)
                throw error
            }
        } else {
            markBudgetForSync(budgetId: budgetId)
            throw APIError.networkUnavailable
        }
    }
    
    private func markBudgetForSync(budgetId: String) {
        let context = persistenceController.context
        let fetchRequest = BudgetEntity.fetchRequest()
        fetchRequest.predicate = NSPredicate(format: "id == %@", budgetId)
        
        if let entity = try? context.fetch(fetchRequest).first {
            entity.needsSync = true
            entity.updatedAt = Date()
            persistenceController.save()
        }
    }
    
    func deleteBudget(_ budgetId: String) async throws {
        if networkConnectivity.isConnected() {
            do {
                try await apiService.deleteBudget(budgetId)
                deleteBudgetLocally(budgetId)
            } catch {
                markBudgetForDeletion(budgetId)
                throw error
            }
        } else {
            markBudgetForDeletion(budgetId)
            throw APIError.networkUnavailable
        }
    }
    
    private func deleteBudgetLocally(_ budgetId: String) {
        let context = persistenceController.context
        let request = BudgetEntity.fetchRequest()
        request.predicate = NSPredicate(format: "id == %@", budgetId)
        
        if let entity = try? context.fetch(request).first {
            context.delete(entity)
            persistenceController.save()
        }
    }
    
    private func markBudgetForDeletion(_ budgetId: String) {
        deleteBudgetLocally(budgetId)
    }
    
    private func syncBudgetsFromAPI(familyId: String) async {
        do {
            let budgets = try await apiService.getBudgets(familyId: familyId)
            
            for budget in budgets {
                saveBudgetLocally(budget, isOnline: true, needsSync: false)
            }
        } catch {
            print("Failed to sync budgets from API: \(error)")
        }
    }
}