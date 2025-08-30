package com.expensetracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.model.*
import com.expensetracker.data.repository.ReceiptRepository
import com.expensetracker.dto.ledger.LedgerEntryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()
    
    private val _receipts = MutableStateFlow<List<ReceiptData>>(emptyList())
    val receipts: StateFlow<List<ReceiptData>> = _receipts.asStateFlow()
    
    fun processReceiptImage(imageFile: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            
            receiptRepository.processReceiptImage(imageFile)
                .onSuccess { receiptData ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        currentReceipt = receiptData,
                        showReceiptDetails = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = error.message ?: "Failed to process receipt"
                    )
                }
        }
    }
    
    fun createExpenseFromReceipt(request: CreateExpenseFromReceiptRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingExpense = true, error = null)
            
            receiptRepository.createExpenseFromReceipt(request)
                .onSuccess { ledgerEntry ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingExpense = false,
                        expenseCreated = true,
                        createdExpense = ledgerEntry
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingExpense = false,
                        error = error.message ?: "Failed to create expense"
                    )
                }
        }
    }
    
    fun loadUserReceipts() {
        viewModelScope.launch {
            receiptRepository.getUserReceipts()
                .onSuccess { receiptList ->
                    _receipts.value = receiptList
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to load receipts"
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearReceiptDetails() {
        _uiState.value = _uiState.value.copy(
            showReceiptDetails = false,
            currentReceipt = null,
            expenseCreated = false,
            createdExpense = null
        )
    }
}

data class ReceiptUiState(
    val isProcessing: Boolean = false,
    val isCreatingExpense: Boolean = false,
    val currentReceipt: ReceiptData? = null,
    val showReceiptDetails: Boolean = false,
    val expenseCreated: Boolean = false,
    val createdExpense: LedgerEntryResponse? = null,
    val error: String? = null
)