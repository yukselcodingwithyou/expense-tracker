package com.expensetracker.data.dao

import androidx.room.*
import com.expensetracker.data.entities.LedgerEntryEntity
import com.expensetracker.data.entities.CategoryEntity
import com.expensetracker.data.entities.BudgetEntity
import com.expensetracker.data.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerEntryDao {
    @Query("SELECT * FROM ledger_entries WHERE familyId = :familyId ORDER BY occurredAt DESC")
    fun getEntries(familyId: String): Flow<List<LedgerEntryEntity>>

    @Query("SELECT * FROM ledger_entries WHERE needsSync = 1")
    suspend fun getEntriesNeedingSync(): List<LedgerEntryEntity>

    @Query("SELECT * FROM ledger_entries WHERE familyId = :familyId AND type = :type ORDER BY occurredAt DESC LIMIT 10")
    fun getRecentEntries(familyId: String, type: String): Flow<List<LedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LedgerEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<LedgerEntryEntity>)

    @Update
    suspend fun updateEntry(entry: LedgerEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: LedgerEntryEntity)

    @Query("DELETE FROM ledger_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: String)

    @Query("DELETE FROM ledger_entries WHERE familyId = :familyId")
    suspend fun deleteAllForFamily(familyId: String)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE familyId = :familyId AND archived = 0 ORDER BY name ASC")
    fun getCategories(familyId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE needsSync = 1")
    suspend fun getCategoriesNeedingSync(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
    
    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: String)

    @Query("DELETE FROM categories WHERE familyId = :familyId")
    suspend fun deleteAllForFamily(familyId: String)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE familyId = :familyId ORDER BY name ASC")
    fun getBudgets(familyId: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE needsSync = 1")
    suspend fun getBudgetsNeedingSync(): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)
    
    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteByIdMethod(budgetId: String)

    @Query("DELETE FROM budgets WHERE familyId = :familyId")
    suspend fun deleteAllForFamily(familyId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}