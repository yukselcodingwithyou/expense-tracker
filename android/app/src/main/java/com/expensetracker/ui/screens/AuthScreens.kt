package com.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.ui.theme.*
import com.expensetracker.ui.components.*

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {}
) {
    var currentPage by remember { mutableStateOf(0) }
    
    val pages = listOf(
        OnboardingPage(
            title = "Track Your Expenses",
            subtitle = "Keep track of your family's income and expenses with our easy-to-use interface",
            icon = Icons.Default.PieChart
        ),
        OnboardingPage(
            title = "Set Savings Goals",
            subtitle = "Create and monitor your savings goals to achieve your financial dreams",
            icon = Icons.Default.TrendingUp
        ),
        OnboardingPage(
            title = "Family Financial Management",
            subtitle = "Manage your family's finances together with multiple user accounts and permissions",
            icon = Icons.Default.Group
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Illustration placeholder
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    PrimaryBlue.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = pages[currentPage].icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(120.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Title and subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = pages[currentPage].title,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = pages[currentPage].subtitle,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Page indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (index == currentPage) PrimaryBlue else CardBorder,
                            CircleShape
                        )
                )
            }
        }
        
        // Navigation buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentPage < pages.size - 1) {
                PrimaryButton(
                    text = "Next",
                    onClick = { currentPage++ },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                PrimaryButton(
                    text = "Get Started",
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            if (currentPage > 0) {
                TextButton(
                    onClick = { currentPage-- },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Previous",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun AuthSelectionScreen(
    onEmailAuth: () -> Unit = {},
    onGoogleAuth: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // App logo
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Title and subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Family Finance Tracker",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Manage your family's finances together",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Auth buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Google Sign In
            SecondaryButton(
                text = "Continue with Google",
                onClick = onGoogleAuth,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Email Sign In
            PrimaryButton(
                text = "Continue with Email",
                onClick = onEmailAuth,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Terms and Privacy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "By continuing, you agree to our",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(onClick = { /* Terms */ }) {
                        Text(
                            "Terms of Service",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryBlue
                        )
                    }
                    
                    Text(
                        "and",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    TextButton(onClick = { /* Privacy */ }) {
                        Text(
                            "Privacy Policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedLoginScreen(
    onNavigateToDashboard: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Logo
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Title and subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isSignUp) "Create Account" else "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = if (isSignUp) "Join Family Finance Tracker" else "Sign in to your account",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Form fields
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Enter your password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = CardBorder
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            PrimaryButton(
                text = if (isLoading) "" else if (isSignUp) "Create Account" else "Sign In",
                onClick = {
                    isLoading = true
                    // TODO: Implement actual auth
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(1000)
                        onNavigateToDashboard()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            TextButton(
                onClick = { isSignUp = !isSignUp },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ExpenseTrackerTheme {
        WelcomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun AuthSelectionScreenPreview() {
    ExpenseTrackerTheme {
        AuthSelectionScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun EnhancedLoginScreenPreview() {
    ExpenseTrackerTheme {
        EnhancedLoginScreen()
    }
}