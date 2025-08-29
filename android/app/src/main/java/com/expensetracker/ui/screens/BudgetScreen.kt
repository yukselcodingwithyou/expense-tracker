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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.components.*
import com.expensetracker.data.*
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Demo budget data
    val currentBudget = remember {
        BudgetCategory("Overall Budget", 3000.0, 2420.0)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Settings") },
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
                            "Edit Budget",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current month budget overview
            item {
                BudgetOverviewCard(currentBudget)
            }
            
            // Budget alert threshold exceeded
            if (currentBudget.progress > 0.8f) {
                item {
                    BudgetAlertCard(currentBudget)
                }
            }
            
            // Category budgets
            item {
                Text(
                    text = "Category Budgets",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(DemoData.budgetCategories) { budgetCategory ->
                CategoryBudgetItem(
                    budgetCategory = budgetCategory,
                    onEdit = { /* Handle edit category budget */ }
                )
            }
        }
    }
    
    if (showAddDialog) {
        BudgetSettingsDialog(
            onDismiss = { showAddDialog = false },
            onSave = { overallLimit, threshold, includeRecurring ->
                // Handle save budget settings
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun BudgetOverviewCard(budget: BudgetCategory) {
    PastelCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Budget",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                val progressColor = when {
                    budget.progress >= 1.0f -> ExpenseRed
                    budget.progress >= 0.8f -> AccentWarning
                    else -> AccentSuccess
                }
                
                Text(
                    text = "${(budget.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = progressColor
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = budget.spent.formatAsCurrency(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Budget",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = budget.limit.formatAsCurrency(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            LinearProgressIndicator(
                progress = budget.progress.coerceAtMost(1.0f),
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    budget.progress >= 1.0f -> ExpenseRed
                    budget.progress >= 0.8f -> AccentWarning
                    else -> AccentSuccess
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Text(
                text = "Remaining: ${(budget.limit - budget.spent).formatAsCurrency()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun BudgetAlertCard(budget: BudgetCategory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AccentWarning.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AccentWarning,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Budget Alert",
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentWarning
                )
                
                Text(
                    text = "You've exceeded 80% of your monthly budget",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun CategoryBudgetItem(
    budgetCategory: BudgetCategory,
    onEdit: () -> Unit
) {
    PastelCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                    )
                    
                    Text(
                        text = budgetCategory.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit budget",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = budgetCategory.spent.formatAsCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = budgetCategory.limit.formatAsCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            LinearProgressIndicator(
                progress = budgetCategory.progress.coerceAtMost(1.0f),
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    budgetCategory.progress >= 1.0f -> ExpenseRed
                    budgetCategory.progress >= 0.8f -> AccentWarning
                    else -> AccentSuccess
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Text(
                text = "${(budgetCategory.progress * 100).toInt()}% used",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetSettingsDialog(
    onDismiss: () -> Unit,
    onSave: (Double, Int, Boolean) -> Unit
) {
    var overallLimit by remember { mutableStateOf("3000") }
    var alertThreshold by remember { mutableStateOf("80") }
    var includeRecurring by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Budget Settings") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = overallLimit,
                    onValueChange = { overallLimit = it },
                    label = { Text("Monthly Budget Limit") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = alertThreshold,
                    onValueChange = { alertThreshold = it },
                    label = { Text("Alert Threshold (%)") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Include Recurring Expenses",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Switch(
                        checked = includeRecurring,
                        onCheckedChange = { includeRecurring = it }
                    )
                }
                
                Text(
                    text = "When enabled, recurring expenses will be counted towards your budget limits",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    val limitValue = overallLimit.toDoubleOrNull() ?: 0.0
                    val thresholdValue = alertThreshold.toIntOrNull() ?: 80
                    onSave(limitValue, thresholdValue, includeRecurring)
                },
                enabled = overallLimit.isNotBlank() && alertThreshold.isNotBlank()
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
fun BudgetScreenPreview() {
    ExpenseTrackerTheme {
        BudgetScreen()
    }
}