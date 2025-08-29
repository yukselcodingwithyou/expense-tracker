package com.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.components.*
import com.expensetracker.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    onNavigateBack: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring Transactions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showAddDialog = true }) {
                        Text(
                            "Add Recurring",
                            color = PrimaryBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceBackground
                )
            )
        },
        containerColor = SurfaceBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Active recurring transactions
            item {
                Text(
                    text = "Active Recurring",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(DemoData.recurringExpenses) { recurring ->
                RecurringItem(
                    recurring = recurring,
                    onTogglePause = { /* Handle pause/resume */ },
                    onEdit = { /* Handle edit */ },
                    onDelete = { /* Handle delete */ }
                )
            }
            
            // Add some demo income recurring items
            items(listOf(
                RecurringExpense(
                    "Salary",
                    5000.0,
                    "Salary",
                    "Monthly",
                    java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 15) }.time
                ),
                RecurringExpense(
                    "Investment Returns",
                    250.0,
                    "Investment",
                    "Monthly",
                    java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 20) }.time
                )
            )) { recurring ->
                RecurringItem(
                    recurring = recurring,
                    isIncome = true,
                    onTogglePause = { /* Handle pause/resume */ },
                    onEdit = { /* Handle edit */ },
                    onDelete = { /* Handle delete */ }
                )
            }
        }
    }
    
    if (showAddDialog) {
        AddRecurringDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, amount, categoryId, memberId, frequency, isExpense ->
                // Handle save recurring
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurringItem(
    recurring: RecurringExpense,
    isIncome: Boolean = false,
    onTogglePause: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    
    PastelCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPaused) MaterialTheme.colorScheme.outline 
                            else if (isIncome) IncomeGreen else ExpenseRed
                        )
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = recurring.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "${recurring.frequency} • ${recurring.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${if (isIncome) "+" else "-"}${recurring.amount.formatAsCurrency()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isIncome) IncomeGreen else ExpenseRed
                    )
                    
                    Text(
                        text = "Next: ${recurring.nextDate.formatAsMonthDay()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isPaused) "Resume" else "Pause") },
                            onClick = {
                                showMenu = false
                                isPaused = !isPaused
                                onTogglePause()
                            },
                            leadingIcon = {
                                Icon(
                                    if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = null
                                )
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }
            
            if (isPaused) {
                Surface(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Text(
                            text = "Paused",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecurringDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Groceries") }
    var selectedMember by remember { mutableStateOf("John") }
    var selectedFrequency by remember { mutableStateOf("Monthly") }
    var isExpense by remember { mutableStateOf(true) }
    
    val frequencies = listOf("Weekly", "Monthly", "Quarterly", "Yearly")
    val categories = if (isExpense) {
        DemoData.categories.filter { it.isExpense }.map { it.name }
    } else {
        DemoData.categories.filter { !it.isExpense }.map { it.name }
    }
    val members = DemoData.familyMembers.map { it.name }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Recurring Transaction") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Type selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterChip(
                        selected = isExpense,
                        onClick = { 
                            isExpense = true
                            selectedCategory = "Groceries"
                        },
                        label = { Text("Expense") }
                    )
                    
                    FilterChip(
                        selected = !isExpense,
                        onClick = { 
                            isExpense = false
                            selectedCategory = "Salary"
                        },
                        label = { Text("Income") }
                    )
                }
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Category dropdown
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
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
                
                // Frequency dropdown
                var frequencyExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = frequencyExpanded,
                    onExpandedChange = { frequencyExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedFrequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = frequencyExpanded,
                        onDismissRequest = { frequencyExpanded = false }
                    ) {
                        frequencies.forEach { frequency ->
                            DropdownMenuItem(
                                text = { Text(frequency) },
                                onClick = {
                                    selectedFrequency = frequency
                                    frequencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    onSave(name, amountValue, selectedCategory, selectedMember, selectedFrequency, isExpense)
                },
                enabled = name.isNotBlank() && amount.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RecurringScreenPreview() {
    ExpenseTrackerTheme {
        RecurringScreen()
    }
}