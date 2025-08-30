package com.expensetracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expensetracker.data.*
import com.expensetracker.data.repository.LedgerRepository
import com.expensetracker.data.repository.BudgetRepository
import com.expensetracker.data.repository.RecurringRepository
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    private val budgetRepository: BudgetRepository,
    private val recurringRepository: RecurringRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()
    
    fun addExpense(ledgerCreate: LedgerCreateDTO) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val result = ledgerRepository.createEntry(ledgerCreate)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to add expense"
                    )
                }
                
                // Analytics hook
                // Analytics.track("expense_add", mapOf(
                //     "type" to ledgerCreate.type,
                //     "amount_minor" to ledgerCreate.amountMinor,
                //     "category_id" to ledgerCreate.categoryId
                // ))
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add expense"
                )
            }
        }
    }
    
    fun addRecurring(recurringRule: RecurringRuleDTO) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val result = recurringRepository.createRecurringRule(recurringRule)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to add recurring transaction"
                    )
                }
                
                // Analytics hook
                // Analytics.track("recurring_add", mapOf(
                //     "type" to recurringRule.type,
                //     "frequency" to recurringRule.frequency.unit
                // ))
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add recurring transaction"
                )
            }
        }
    }
    
    fun saveBudget(budget: BudgetDTO) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val result = budgetRepository.createBudget(budget)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to save budget"
                    )
                }
                
                // Analytics hook
                // Analytics.track("budget_saved", mapOf(
                //     "overall_limit_minor" to budget.overallLimitMinor,
                //     "alert_threshold_pct" to budget.alertThresholdPct
                // ))
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save budget"
                )
            }
        }
    }
    
    fun checkBudgetAlert(currentSpent: Long, budgetLimit: Long, threshold: Int) {
        val percentage = (currentSpent.toDouble() / budgetLimit.toDouble()) * 100
        
        if (percentage >= threshold) {
            // Analytics hook
            // Analytics.track("budget_alert_shown", mapOf(
            //     "spent_percentage" to percentage,
            //     "threshold" to threshold
            // ))
        }
    }
    
    fun clearState() {
        _uiState.value = ExpenseUiState()
    }
}

data class ExpenseUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)