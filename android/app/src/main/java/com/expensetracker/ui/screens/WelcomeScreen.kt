package com.expensetracker.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.components.*
import kotlinx.coroutines.launch

data class WelcomeFeature(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    onCompleteWelcome: () -> Unit = {}
) {
    val features = listOf(
        WelcomeFeature(
            title = "Track Your Expenses",
            description = "Easily track all your family's income and expenses in one place. Categorize transactions and see where your money goes.",
            icon = Icons.Default.Receipt
        ),
        WelcomeFeature(
            title = "Set Budgets & Goals",
            description = "Create monthly budgets for different categories and set savings goals. Get alerts when you're approaching your limits.",
            icon = Icons.Default.TrendingUp
        ),
        WelcomeFeature(
            title = "Manage Recurring Transactions",
            description = "Set up recurring income and expenses like salary, rent, and subscriptions. Never miss a payment again.",
            icon = Icons.Default.Autorenew
        ),
        WelcomeFeature(
            title = "Family Collaboration",
            description = "Invite family members to contribute to your shared budget. Everyone can add transactions and view reports.",
            icon = Icons.Default.Group
        ),
        WelcomeFeature(
            title = "Detailed Reports",
            description = "Get insights with comprehensive reports and visualizations. Export data to PDF or CSV for your records.",
            icon = Icons.Default.Analytics
        )
    )
    
    val pagerState = rememberPagerState(pageCount = { features.size })
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        containerColor = SurfaceBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCompleteWelcome
                ) {
                    Text(
                        "Skip",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                WelcomeFeaturePage(features[page])
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(features.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) PrimaryBlue 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    SecondaryButton(
                        text = "Previous",
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.width(100.dp))
                }
                
                if (pagerState.currentPage < features.size - 1) {
                    PrimaryButton(
                        text = "Next",
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    )
                } else {
                    PrimaryButton(
                        text = "Get Started",
                        onClick = onCompleteWelcome
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeFeaturePage(feature: WelcomeFeature) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Feature icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = PrimaryBlue
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Feature title
        Text(
            text = feature.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Feature description
        Text(
            text = feature.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ExpenseTrackerTheme {
        WelcomeScreen()
    }
}