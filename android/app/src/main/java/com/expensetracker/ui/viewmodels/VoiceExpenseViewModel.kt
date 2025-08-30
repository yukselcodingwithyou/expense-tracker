package com.expensetracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.model.*
import com.expensetracker.data.repository.VoiceExpenseRepository
import com.expensetracker.dto.ledger.LedgerEntryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceExpenseViewModel @Inject constructor(
    private val voiceExpenseRepository: VoiceExpenseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(VoiceExpenseUiState())
    val uiState: StateFlow<VoiceExpenseUiState> = _uiState.asStateFlow()
    
    private val _voiceExpenses = MutableStateFlow<List<VoiceExpenseData>>(emptyList())
    val voiceExpenses: StateFlow<List<VoiceExpenseData>> = _voiceExpenses.asStateFlow()
    
    fun processVoiceExpense(spokenText: String, preferredCurrency: String = "USD") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            
            val request = VoiceExpenseRequest(spokenText, preferredCurrency)
            voiceExpenseRepository.processVoiceExpense(request)
                .onSuccess { voiceExpenseData ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        currentVoiceExpense = voiceExpenseData,
                        showExpenseDetails = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = error.message ?: "Failed to process voice expense"
                    )
                }
        }
    }
    
    fun createExpenseFromVoice(request: CreateExpenseFromVoiceRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingExpense = true, error = null)
            
            voiceExpenseRepository.createExpenseFromVoice(request)
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
    
    fun loadUserVoiceExpenses() {
        viewModelScope.launch {
            voiceExpenseRepository.getUserVoiceExpenses()
                .onSuccess { voiceExpenseList ->
                    _voiceExpenses.value = voiceExpenseList
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to load voice expenses"
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearExpenseDetails() {
        _uiState.value = _uiState.value.copy(
            showExpenseDetails = false,
            currentVoiceExpense = null,
            expenseCreated = false,
            createdExpense = null
        )
    }
    
    fun updateParseResult(updatedResult: ExpenseParseResult) {
        val currentExpense = _uiState.value.currentVoiceExpense ?: return
        _uiState.value = _uiState.value.copy(
            currentVoiceExpense = currentExpense.copy(parseResult = updatedResult)
        )
    }
}

data class VoiceExpenseUiState(
    val isProcessing: Boolean = false,
    val isCreatingExpense: Boolean = false,
    val currentVoiceExpense: VoiceExpenseData? = null,
    val showExpenseDetails: Boolean = false,
    val expenseCreated: Boolean = false,
    val createdExpense: LedgerEntryResponse? = null,
    val error: String? = null
)