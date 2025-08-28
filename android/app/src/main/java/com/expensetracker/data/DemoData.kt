package com.expensetracker.data

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Data Models
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val isExpense: Boolean,
    val category: String,
    val date: Date,
    val familyMember: String
)

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String,
    val color: String,
    val isExpense: Boolean
)

data class SavingsGoal(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val currentAmount: Double,
    val targetAmount: Double
) {
    val progress: Float get() = (currentAmount / targetAmount).toFloat()
    val progressPercent: String get() = "${(progress * 100).toInt()}%"
}

data class FamilyMember(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String,
    val avatar: String
)

data class BudgetCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val limit: Double,
    val spent: Double
) {
    val progress: Float get() = (spent / limit).toFloat()
}

data class RecurringExpense(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: Double,
    val category: String,
    val frequency: String,
    val nextDate: Date
)

// Demo Data Object
object DemoData {
    const val totalIncome: Double = 12500.00
    const val totalExpenses: Double = 8200.00
    
    val balance: Double get() = totalIncome - totalExpenses
    
    val transactions = listOf(
        Transaction(
            title = "Salary",
            amount = 5000.00,
            isExpense = false,
            category = "Income",
            date = Date(),
            familyMember = "John"
        ),
        Transaction(
            title = "Grocery Shopping",
            amount = 156.78,
            isExpense = true,
            category = "Groceries",
            date = Date(),
            familyMember = "Sarah"
        ),
        Transaction(
            title = "Gas Station",
            amount = 45.50,
            isExpense = true,
            category = "Transportation",
            date = Date(),
            familyMember = "John"
        ),
        Transaction(
            title = "Restaurant",
            amount = 89.32,
            isExpense = true,
            category = "Dining",
            date = Date(),
            familyMember = "Sarah"
        ),
        Transaction(
            title = "Freelance Work",
            amount = 800.00,
            isExpense = false,
            category = "Income",
            date = Date(),
            familyMember = "John"
        )
    )
    
    val categories = listOf(
        Category("Groceries", "cart", "accentSuccess", true),
        Category("Transportation", "car", "accentWarning", true),
        Category("Dining", "fork.knife", "accentInfo", true),
        Category("Entertainment", "tv", "primary", true),
        Category("Utilities", "house", "expense", true),
        Category("Salary", "dollarsign.circle", "income", false),
        Category("Freelance", "briefcase", "income", false)
    )
    
    val savingsGoals = listOf(
        SavingsGoal("Emergency Fund", 2500.00, 5000.00),
        SavingsGoal("Vacation", 1200.00, 3000.00),
        SavingsGoal("New Car", 4500.00, 6000.00),
        SavingsGoal("Home Renovation", 900.00, 3000.00)
    )
    
    val familyMembers = listOf(
        FamilyMember("John Smith", "Admin", "person.circle.fill"),
        FamilyMember("Sarah Smith", "Member", "person.circle.fill"),
        FamilyMember("Emma Smith", "Member", "person.circle.fill")
    )
    
    val budgetCategories = listOf(
        BudgetCategory("Groceries", 500.00, 325.00),
        BudgetCategory("Transportation", 300.00, 245.00),
        BudgetCategory("Dining", 200.00, 89.32),
        BudgetCategory("Entertainment", 150.00, 67.50)
    )
    
    val recurringExpenses = listOf(
        RecurringExpense(
            "Netflix",
            15.99,
            "Entertainment",
            "Monthly",
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 5) }.time
        ),
        RecurringExpense(
            "Gym Membership",
            29.99,
            "Health",
            "Monthly",
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 12) }.time
        ),
        RecurringExpense(
            "Phone Bill",
            89.00,
            "Utilities",
            "Monthly",
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 8) }.time
        )
    )
}

// Extension functions for formatting
fun Double.formatAsCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(this)
}

fun Double.formatAsPercent(): String {
    return "${(this * 100).toInt()}%"
}

fun Date.formatAsMonthDay(): String {
    val formatter = SimpleDateFormat("MM/dd", Locale.US)
    return formatter.format(this)
}

fun Date.formatAsShort(): String {
    val formatter = SimpleDateFormat("M/d/yyyy", Locale.US)
    return formatter.format(this)
}

fun Date.formatAsTime(): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.US)
    return formatter.format(this)
}