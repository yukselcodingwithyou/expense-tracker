package com.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.components.*
import com.expensetracker.data.*
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAddExpenseScreen(
    onNavigateBack: () -> Unit = {},
    onExpenseAdded: (LedgerCreateDTO) -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Groceries") }
    var selectedMember by remember { mutableStateOf("John") }
    var notes by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    val categories = DemoData.categories.filter { it.isExpense == isExpense }.map { it.name }
    val members = DemoData.familyMembers.map { it.name }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (isExpense) "Add Expense" else "Add Income") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceBackground
                )
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryButton(
                    text = "Add ${if (isExpense) "Expense" else "Income"}",
                    onClick = { 
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        val amountMinor = amountValue.toAmountMinor()
                        
                        val ledgerCreate = LedgerCreateDTO(
                            type = if (isExpense) "EXPENSE" else "INCOME",
                            amountMinor = amountMinor,
                            currency = "USD",
                            categoryId = selectedCategory.lowercase(),
                            memberId = selectedMember.lowercase(),
                            occurredAt = Instant.now().toString(),
                            notes = notes.ifBlank { null }
                        )
                        
                        onExpenseAdded(ledgerCreate)
                        showSuccessMessage = true
                        
                        // Reset form
                        amount = ""
                        notes = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = amount.isNotBlank() && amount.toDoubleOrNull() != null
                )
            }
        },
        containerColor = SurfaceBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Toggle
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterChip(
                    selected = isExpense,
                    onClick = { 
                        isExpense = true
                        selectedCategory = "Groceries"
                    },
                    label = { Text("Expense") },
                    modifier = Modifier.weight(1f)
                )
                
                FilterChip(
                    selected = !isExpense,
                    onClick = { 
                        isExpense = false
                        selectedCategory = "Salary"
                    },
                    label = { Text("Income") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Amount Field
            FormTextField(
                label = "Amount",
                value = amount,
                onValueChange = { amount = it },
                placeholder = "0.00",
                prefix = "$",
                keyboardType = KeyboardType.Decimal
            )
            
            // Category Selector
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                var categoryExpanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = CardBorder
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Date Field (simplified - shows today)
            FormTextField(
                label = "Date",
                value = "Today",
                onValueChange = {},
                placeholder = "Select date",
                enabled = false
            )
            
            // Family Member Selector
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Family Member",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                var memberExpanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = memberExpanded,
                    onExpandedChange = { memberExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMember,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = CardBorder
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = memberExpanded,
                        onDismissRequest = { memberExpanded = false }
                    ) {
                        members.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member) },
                                onClick = {
                                    selectedMember = member
                                    memberExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Notes Field
            FormTextField(
                label = "Notes (Optional)",
                value = notes,
                onValueChange = { notes = it },
                placeholder = "Add notes...",
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for FAB
        }
    }
    
    // Success message
    if (showSuccessMessage) {
        LaunchedEffect(showSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            showSuccessMessage = false
            onNavigateBack()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnhancedAddExpenseScreenPreview() {
    ExpenseTrackerTheme {
        EnhancedAddExpenseScreen()
    }
}