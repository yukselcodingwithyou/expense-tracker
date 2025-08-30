package com.expensetracker.data.sync

import com.expensetracker.data.repository.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val offlineLedgerRepository: OfflineLedgerRepository,
    private val offlineBudgetRepository: OfflineBudgetRepository,
    private val offlineCategoryRepository: OfflineCategoryRepository,
    private val networkConnectivity: NetworkConnectivity
) {
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    fun startPeriodicSync() {
        syncScope.launch {
            while (true) {
                if (networkConnectivity.isConnected()) {
                    try {
                        syncAllPendingData()
                    } catch (e: Exception) {
                        println("Sync failed: ${e.message}")
                    }
                }
                // Wait 5 minutes before next sync attempt
                delay(5 * 60 * 1000)
            }
        }
    }
    
    suspend fun syncAllPendingData() {
        if (!networkConnectivity.isConnected()) return
        
        try {
            // Sync in parallel for better performance
            coroutineScope {
                launch { offlineLedgerRepository.syncPendingEntries() }
                launch { offlineBudgetRepository.syncPendingBudgets() }
                launch { offlineCategoryRepository.syncPendingCategories() }
            }
            println("Sync completed successfully")
        } catch (e: Exception) {
            println("Sync failed: ${e.message}")
            throw e
        }
    }
    
    suspend fun forceSync(familyId: String) {
        if (!networkConnectivity.isConnected()) return
        
        try {
            coroutineScope {
                launch { offlineLedgerRepository.syncPendingEntries() }
                launch { offlineBudgetRepository.syncPendingBudgets() }
                launch { offlineCategoryRepository.syncPendingCategories() }
                launch { offlineBudgetRepository.refreshFromAPI(familyId) }
                launch { offlineCategoryRepository.refreshFromAPI(familyId) }
            }
            println("Force sync completed successfully")
        } catch (e: Exception) {
            println("Force sync failed: ${e.message}")
            throw e
        }
    }
    
    fun stopSync() {
        syncScope.cancel()
    }
}

// Network connectivity checker interface
interface NetworkConnectivity {
    fun isConnected(): Boolean
}

// Simple implementation - in a real app this would use ConnectivityManager
@Singleton
class NetworkConnectivityImpl @Inject constructor() : NetworkConnectivity {
    override fun isConnected(): Boolean {
        // For demo purposes, assume always connected
        // In real implementation, use Android's ConnectivityManager
        return true
    }
}