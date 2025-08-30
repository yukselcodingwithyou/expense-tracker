package com.expensetracker.data.repository

import com.expensetracker.data.dao.*
import com.expensetracker.data.entities.*
import com.expensetracker.data.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineBudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val budgetApiService: BudgetApiService,
    private val networkConnectivity: NetworkConnectivity
) {
    
    fun getBudgets(familyId: String): Flow<List<Budget>> = 
        budgetDao.getBudgets(familyId).map { entities -> 
            entities.map { it.toDomainModel() } 
        }
    
    suspend fun createBudget(request: CreateBudgetRequest): Result<Budget> {
        return try {
            if (networkConnectivity.isConnected()) {
                // Try online first
                val response = budgetApiService.createBudget(request)
                if (response.isSuccessful) {
                    val budget = response.body()!!
                    // Save to local database
                    budgetDao.insertBudget(budget.toEntity().copy(isOnline = true, needsSync = false))
                    Result.success(budget)
                } else {
                    // API failed, save offline
                    saveOfflineBudget(request)
                }
            } else {
                // No network, save offline
                saveOfflineBudget(request)
            }
        } catch (e: Exception) {
            // Exception occurred, save offline
            saveOfflineBudget(request)
        }
    }
    
    private suspend fun saveOfflineBudget(request: CreateBudgetRequest): Result<Budget> {
        val entity = request.toEntity().copy(
            id = generateLocalId(),
            isOnline = false,
            needsSync = true
        )
        budgetDao.insertBudget(entity)
        return Result.success(entity.toDomainModel())
    }
    
    suspend fun syncPendingBudgets() {
        val pendingBudgets = budgetDao.getBudgetsNeedingSync()
        
        pendingBudgets.forEach { budget ->
            try {
                val request = budget.toCreateRequest()
                val response = budgetApiService.createBudget(request)
                
                if (response.isSuccessful) {
                    val onlineBudget = response.body()!!
                    // Update local budget with server data
                    budgetDao.updateBudget(
                        budget.copy(
                            id = onlineBudget.id,
                            isOnline = true,
                            needsSync = false
                        )
                    )
                }
            } catch (e: Exception) {
                // Sync failed, will retry later
                println("Failed to sync budget ${budget.id}: ${e.message}")
            }
        }
    }
    
    suspend fun updateBudget(budgetId: String, request: CreateBudgetRequest): Result<Budget> {
        return try {
            if (networkConnectivity.isConnected()) {
                val response = budgetApiService.updateBudget(budgetId, request)
                if (response.isSuccessful) {
                    val budget = response.body()!!
                    budgetDao.updateBudget(budget.toEntity().copy(isOnline = true, needsSync = false))
                    Result.success(budget)
                } else {
                    // Mark for sync
                    markBudgetForSync(budgetId, request)
                }
            } else {
                markBudgetForSync(budgetId, request)
            }
        } catch (e: Exception) {
            markBudgetForSync(budgetId, request)
        }
    }
    
    private suspend fun markBudgetForSync(budgetId: String, request: CreateBudgetRequest): Result<Budget> {
        val entity = request.toEntity().copy(
            id = budgetId,
            isOnline = false,
            needsSync = true,
            updatedAt = System.currentTimeMillis()
        )
        budgetDao.updateBudget(entity)
        return Result.success(entity.toDomainModel())
    }
    
    suspend fun deleteBudget(budgetId: String): Result<Unit> {
        return try {
            if (networkConnectivity.isConnected()) {
                val response = budgetApiService.deleteBudget(budgetId)
                if (response.isSuccessful) {
                    budgetDao.deleteByIdMethod(budgetId)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to delete budget: ${response.message()}"))
                }
            } else {
                // Mark for deletion - in a real app you'd have a deletion sync mechanism
                budgetDao.deleteByIdMethod(budgetId)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateLocalId(): String = "local_${System.currentTimeMillis()}_${(1000..9999).random()}"
    
    suspend fun refreshFromAPI(familyId: String) {
        if (!networkConnectivity.isConnected()) return
        
        try {
            val response = budgetApiService.getBudgets(familyId)
            if (response.isSuccessful) {
                val budgets = response.body() ?: return
                
                // Clear old data and insert fresh data
                budgetDao.deleteAllForFamily(familyId)
                val entities = budgets.map { it.toEntity().copy(isOnline = true, needsSync = false) }
                budgetDao.insertBudgets(entities)
            }
        } catch (e: Exception) {
            println("Failed to refresh budgets from API: ${e.message}")
        }
    }
}

@Singleton
class OfflineCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryApiService: CategoryApiService,
    private val networkConnectivity: NetworkConnectivity
) {
    
    fun getCategories(familyId: String): Flow<List<Category>> = 
        categoryDao.getCategories(familyId).map { entities -> 
            entities.map { it.toDomainModel() } 
        }
    
    suspend fun createCategory(request: CreateCategoryRequest): Result<Category> {
        return try {
            if (networkConnectivity.isConnected()) {
                val response = categoryApiService.createCategory(request)
                if (response.isSuccessful) {
                    val category = response.body()!!
                    categoryDao.insertCategory(category.toEntity().copy(isOnline = true, needsSync = false))
                    Result.success(category)
                } else {
                    saveOfflineCategory(request)
                }
            } else {
                saveOfflineCategory(request)
            }
        } catch (e: Exception) {
            saveOfflineCategory(request)
        }
    }
    
    private suspend fun saveOfflineCategory(request: CreateCategoryRequest): Result<Category> {
        val entity = request.toEntity().copy(
            id = generateLocalId(),
            isOnline = false,
            needsSync = true
        )
        categoryDao.insertCategory(entity)
        return Result.success(entity.toDomainModel())
    }
    
    suspend fun syncPendingCategories() {
        val pendingCategories = categoryDao.getCategoriesNeedingSync()
        
        pendingCategories.forEach { category ->
            try {
                val request = category.toCreateRequest()
                val response = categoryApiService.createCategory(request)
                
                if (response.isSuccessful) {
                    val onlineCategory = response.body()!!
                    categoryDao.updateCategory(
                        category.copy(
                            id = onlineCategory.id,
                            isOnline = true,
                            needsSync = false
                        )
                    )
                }
            } catch (e: Exception) {
                println("Failed to sync category ${category.id}: ${e.message}")
            }
        }
    }
    
    suspend fun updateCategory(categoryId: String, request: CreateCategoryRequest): Result<Category> {
        return try {
            if (networkConnectivity.isConnected()) {
                val response = categoryApiService.updateCategory(categoryId, request)
                if (response.isSuccessful) {
                    val category = response.body()!!
                    categoryDao.updateCategory(category.toEntity().copy(isOnline = true, needsSync = false))
                    Result.success(category)
                } else {
                    markCategoryForSync(categoryId, request)
                }
            } else {
                markCategoryForSync(categoryId, request)
            }
        } catch (e: Exception) {
            markCategoryForSync(categoryId, request)
        }
    }
    
    private suspend fun markCategoryForSync(categoryId: String, request: CreateCategoryRequest): Result<Category> {
        val entity = request.toEntity().copy(
            id = categoryId,
            isOnline = false,
            needsSync = true
        )
        categoryDao.updateCategory(entity)
        return Result.success(entity.toDomainModel())
    }
    
    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            if (networkConnectivity.isConnected()) {
                val response = categoryApiService.deleteCategory(categoryId)
                if (response.isSuccessful) {
                    categoryDao.deleteCategoryById(categoryId)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to delete category: ${response.message()}"))
                }
            } else {
                categoryDao.deleteCategoryById(categoryId)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateLocalId(): String = "local_${System.currentTimeMillis()}_${(1000..9999).random()}"
    
    suspend fun refreshFromAPI(familyId: String) {
        if (!networkConnectivity.isConnected()) return
        
        try {
            val response = categoryApiService.getCategories(familyId)
            if (response.isSuccessful) {
                val categories = response.body() ?: return
                
                categoryDao.deleteAllForFamily(familyId)
                val entities = categories.map { it.toEntity().copy(isOnline = true, needsSync = false) }
                categoryDao.insertCategories(entities)
            }
        } catch (e: Exception) {
            println("Failed to refresh categories from API: ${e.message}")
        }
    }
}