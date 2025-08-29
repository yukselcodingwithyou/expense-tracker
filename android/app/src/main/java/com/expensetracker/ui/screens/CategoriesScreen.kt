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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.components.*
import com.expensetracker.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
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
                            "Add Category",
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
            // Section: Expense Categories
            item {
                Text(
                    text = "Expense Categories",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(DemoData.categories.filter { it.isExpense }) { category ->
                CategoryItem(
                    category = category,
                    onEdit = { /* Handle edit */ },
                    onArchive = { /* Handle archive */ }
                )
            }
            
            // Section: Income Categories
            item {
                Text(
                    text = "Income Categories",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(DemoData.categories.filter { !it.isExpense }) { category ->
                CategoryItem(
                    category = category,
                    onEdit = { /* Handle edit */ },
                    onArchive = { /* Handle archive */ }
                )
            }
        }
    }
    
    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, isExpense, icon ->
                // Handle save category
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryItem(
    category: Category,
    onEdit: () -> Unit,
    onArchive: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    PastelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.icon),
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = if (category.isExpense) "Expense" else "Income",
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
                        text = { Text("Archive") },
                        onClick = {
                            showMenu = false
                            onArchive()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Archive, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (String, Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var selectedIcon by remember { mutableStateOf("cart") }
    var showIconPicker by remember { mutableStateOf(false) }
    
    val availableIcons = listOf(
        "cart", "car", "fork.knife", "tv", "house", "heart", "briefcase", 
        "airplane", "phone", "book", "gamecontroller", "gift"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Type selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterChip(
                        selected = isExpense,
                        onClick = { isExpense = true },
                        label = { Text("Expense") }
                    )
                    
                    FilterChip(
                        selected = !isExpense,
                        onClick = { isExpense = false },
                        label = { Text("Income") }
                    )
                }
                
                // Icon selector
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Icon",
                        style = MaterialTheme.typography.labelLarge
                    )
                    
                    OutlinedButton(
                        onClick = { showIconPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(selectedIcon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Icon")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, isExpense, selectedIcon) },
                enabled = name.isNotBlank()
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
    
    if (showIconPicker) {
        AlertDialog(
            onDismissRequest = { showIconPicker = false },
            title = { Text("Select Icon") },
            text = {
                LazyColumn {
                    items(availableIcons.chunked(4)) { iconRow ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            iconRow.forEach { icon ->
                                IconButton(
                                    onClick = {
                                        selectedIcon = icon
                                        showIconPicker = false
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (selectedIcon == icon) PrimaryBlue.copy(alpha = 0.2f) 
                                            else MaterialTheme.colorScheme.surface,
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(icon),
                                        contentDescription = null,
                                        tint = if (selectedIcon == icon) PrimaryBlue 
                                               else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconPicker = false }) {
                    Text("Done")
                }
            }
        )
    }
}

private fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "cart" -> Icons.Default.ShoppingCart
        "car" -> Icons.Default.DirectionsCar
        "fork.knife" -> Icons.Default.Restaurant
        "tv" -> Icons.Default.Tv
        "house" -> Icons.Default.Home
        "heart" -> Icons.Default.Favorite
        "briefcase" -> Icons.Default.Work
        "airplane" -> Icons.Default.Flight
        "phone" -> Icons.Default.Phone
        "book" -> Icons.Default.Book
        "gamecontroller" -> Icons.Default.SportsEsports
        "gift" -> Icons.Default.Card_Giftcard
        else -> Icons.Default.Category
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    ExpenseTrackerTheme {
        CategoriesScreen()
    }
}