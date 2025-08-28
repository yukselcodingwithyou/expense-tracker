package com.expensetracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.components.*
import com.expensetracker.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedDateRange by remember { mutableStateOf("This Month") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedMember by remember { mutableStateOf("All Members") }
    
    val tabs = listOf("Summary", "Detailed", "Visualizations")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Reports",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
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
            // Filters
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterRow(
                        label = "Date Range",
                        value = selectedDateRange,
                        onClick = { /* Handle date range selection */ }
                    )
                    
                    FilterRow(
                        label = "Category",
                        value = selectedCategory,
                        onClick = { /* Handle category selection */ }
                    )
                    
                    FilterRow(
                        label = "Family Member",
                        value = selectedMember,
                        onClick = { /* Handle member selection */ }
                    )
                }
            }
            
            // Segmented Control
            item {
                SegmentedTabs(
                    items = tabs,
                    selectedIndex = selectedTab,
                    onSelectionChanged = { selectedTab = it }
                )
            }
            
            // Content based on selected tab
            item {
                when (selectedTab) {
                    0 -> SummaryContent()
                    1 -> DetailedContent()
                    2 -> VisualizationsContent()
                }
            }
            
            // Export buttons
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Export PDF",
                        onClick = { /* Handle PDF export */ },
                        modifier = Modifier.weight(1f)
                    )
                    
                    SecondaryButton(
                        text = "Export CSV",
                        onClick = { /* Handle CSV export */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary tiles
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatTile(
                title = "Total Income",
                amount = DemoData.totalIncome.formatAsCurrency(),
                modifier = Modifier.weight(1f),
                color = IncomeGreen
            )
            
            StatTile(
                title = "Total Expenses",
                amount = DemoData.totalExpenses.formatAsCurrency(),
                modifier = Modifier.weight(1f),
                color = ExpenseRed
            )
        }
        
        StatTile(
            title = "Net Income",
            amount = DemoData.balance.formatAsCurrency(),
            color = AccentSuccess
        )
        
        // Category breakdown
        PastelCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Category Breakdown",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoData.categories.filter { it.isExpense }.take(5).forEach { category ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${(200..800).random()}".formatAsCurrency(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                Text(
                                    text = "${(15..35).random()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailedContent() {
    PastelCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Transaction Details",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DemoData.transactions.forEach { transaction ->
                    ListRow(
                        leadingIcon = Icons.Default.CreditCard,
                        title = transaction.title,
                        subtitle = "${transaction.category} • ${transaction.familyMember} • ${transaction.date.formatAsShort()}",
                        rightValue = "${if (transaction.isExpense) "-" else "+"}${transaction.amount.formatAsCurrency()}",
                        showChevron = false
                    )
                }
            }
        }
    }
}

@Composable
private fun VisualizationsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Monthly Trends Chart
        PastelCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Monthly Trends",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Simple bar chart placeholder
                SimpleBarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                
                Text(
                    text = "Income vs Expenses (Last 6 Months)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        
        // Pie Chart
        PastelCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Spending by Category",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Simple pie chart placeholder
                SimplePieChart(
                    modifier = Modifier.size(150.dp)
                )
                
                Text(
                    text = "Expense Distribution",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SimpleBarChart(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val barWidth = size.width / 8
        val maxHeight = size.height * 0.8f
        
        repeat(6) { index ->
            val height = (40..120).random() / 120f * maxHeight
            val x = (index + 1) * barWidth
            
            drawRect(
                color = PrimaryBlue,
                topLeft = Offset(x, size.height - height),
                size = Size(barWidth * 0.6f, height)
            )
        }
    }
}

@Composable
private fun SimplePieChart(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.clip(CircleShape)
    ) {
        val colors = listOf(PrimaryBlue, AccentSuccess, AccentWarning, AccentInfo)
        val total = 360f
        var startAngle = 0f
        
        colors.forEachIndexed { index, color ->
            val sweepAngle = total / colors.size
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )
            startAngle += sweepAngle
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    ExpenseTrackerTheme {
        ReportsScreen()
    }
}