package com.expensetracker.ui.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import com.expensetracker.data.*

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModelTest {
    
    private lateinit var viewModel: ExpenseViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ExpenseViewModel()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `addExpense should set loading state initially`() = runTest {
        // Given
        val ledgerCreate = LedgerCreateDTO(
            type = "EXPENSE",
            amountMinor = 2500L,
            currency = "USD",
            categoryId = "groceries",
            memberId = "john",
            occurredAt = "2025-01-20T14:00:00Z",
            notes = "Test expense"
        )
        
        // When
        viewModel.addExpense(ledgerCreate)
        
        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `addExpense should set success state after completion`() = runTest {
        // Given
        val ledgerCreate = LedgerCreateDTO(
            type = "EXPENSE",
            amountMinor = 2500L,
            currency = "USD",
            categoryId = "groceries",
            memberId = "john",
            occurredAt = "2025-01-20T14:00:00Z",
            notes = "Test expense"
        )
        
        // When
        viewModel.addExpense(ledgerCreate)
        advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `addRecurring should handle valid recurring rule`() = runTest {
        // Given
        val recurringRule = RecurringRuleDTO(
            id = null,
            familyId = "fam1",
            name = "Monthly Rent",
            type = "EXPENSE",
            amountMinor = 150000L,
            currency = "USD",
            categoryId = "rent",
            memberId = "ethan",
            frequency = FrequencyDTO(
                unit = "MONTHLY",
                interval = 1,
                byMonthDay = listOf(1)
            ),
            startDate = "2025-01-01",
            endDate = null,
            timezone = "UTC",
            nextRunAt = null,
            isPaused = false
        )
        
        // When
        viewModel.addRecurring(recurringRule)
        advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `saveBudget should handle valid budget`() = runTest {
        // Given
        val budget = BudgetDTO(
            id = null,
            name = "Monthly Budget",
            period = PeriodDTO(
                type = "MONTH",
                start = "2025-02-01",
                end = "2025-02-28"
            ),
            overallLimitMinor = 300000L,
            includeRecurring = true,
            alertThresholdPct = 80,
            perCategory = listOf(
                CategoryBudgetDTO(
                    categoryId = "groceries",
                    limitMinor = 50000L
                )
            )
        )
        
        // When
        viewModel.saveBudget(budget)
        advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `checkBudgetAlert should handle threshold exceeded`() {
        // Given
        val currentSpent = 2400L // $24.00
        val budgetLimit = 3000L  // $30.00
        val threshold = 80
        
        // When
        viewModel.checkBudgetAlert(currentSpent, budgetLimit, threshold)
        
        // Then - would trigger analytics in real implementation
        // This test verifies the logic runs without error
        // In a real app, we'd verify analytics was called
    }
    
    @Test
    fun `clearState should reset ui state`() = runTest {
        // Given - set some state first
        val ledgerCreate = LedgerCreateDTO(
            type = "EXPENSE",
            amountMinor = 2500L,
            currency = "USD",
            categoryId = "groceries",
            memberId = "john",
            occurredAt = "2025-01-20T14:00:00Z",
            notes = "Test expense"
        )
        viewModel.addExpense(ledgerCreate)
        advanceUntilIdle()
        
        // Verify state was set
        assertTrue(viewModel.uiState.value.isSuccess)
        
        // When
        viewModel.clearState()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.error)
    }
}