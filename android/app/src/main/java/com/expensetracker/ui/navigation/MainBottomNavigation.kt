package com.expensetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.dashboard.DashboardScreen
import com.expensetracker.ui.screens.*

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("dashboard", "Dashboard", Icons.Default.Home)
    object Expenses : BottomNavItem("expenses", "Expenses", Icons.Default.Remove)
    object Income : BottomNavItem("income", "Income", Icons.Default.Add)
    object Reports : BottomNavItem("reports", "Reports", Icons.Default.BarChart)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Expenses,
        BottomNavItem.Income,
        BottomNavItem.Reports,
        BottomNavItem.Settings
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                contentColor = PrimaryBlue
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            ) 
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        containerColor = SurfaceBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAddExpense = {
                        navController.navigate(BottomNavItem.Expenses.route)
                    }
                )
            }
            composable(BottomNavItem.Expenses.route) {
                AddExpenseScreen()
            }
            composable(BottomNavItem.Income.route) {
                AddIncomeScreen()
            }
            composable(BottomNavItem.Reports.route) {
                ReportsScreen()
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun AddIncomeScreen() {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Salary") }
    var selectedMember by remember { mutableStateOf("John") }
    var notes by remember { mutableStateOf("") }
    
    val categories = DemoData.categories.filter { !it.isExpense }.map { it.name }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Income") },
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
                    text = "Add Income",
                    onClick = { /* Handle add action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        },
        containerColor = SurfaceBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Field
            MoneyTextField(
                label = "Amount",
                value = amount,
                onValueChange = { amount = it }
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
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
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
            
            // Date Picker (simplified)
            FormTextField(
                label = "Date",
                value = "Today",
                onValueChange = {},
                placeholder = "Select date"
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
                    onExpandedChange = { memberExpanded = !memberExpanded }
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
                        DemoData.familyMembers.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member.name) },
                                onClick = {
                                    selectedMember = member.name
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
                placeholder = "Add notes..."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainBottomNavigationPreview() {
    ExpenseTrackerTheme {
        MainBottomNavigation()
    }
}