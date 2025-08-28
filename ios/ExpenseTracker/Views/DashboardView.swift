import SwiftUI

struct DashboardView: View {
    @State private var balance: Double = 2450.75
    @State private var income: Double = 3200.00
    @State private var expenses: Double = 749.25
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    BalanceCard(balance: balance, income: income, expenses: expenses)
                    
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Recent Transactions")
                            .font(.headline)
                            .fontWeight(.bold)
                        
                        ForEach(0..<5) { index in
                            TransactionRow(
                                title: "Sample Transaction \(index)",
                                amount: 25.50,
                                isExpense: index % 2 == 0,
                                category: "Groceries"
                            )
                        }
                    }
                    .padding(.horizontal)
                }
            }
            .navigationTitle("Dashboard")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        // TODO: Navigate to add transaction
                    }) {
                        Image(systemName: "plus")
                    }
                }
            }
        }
    }
}

struct BalanceCard: View {
    let balance: Double
    let income: Double
    let expenses: Double
    
    var body: some View {
        VStack(spacing: 16) {
            Text("Current Balance")
                .font(.title2)
                .foregroundColor(.secondary)
            
            Text("$\(balance, specifier: "%.2f")")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)
            
            HStack(spacing: 40) {
                BalanceItem(
                    title: "Income",
                    amount: income,
                    color: .green
                )
                
                BalanceItem(
                    title: "Expenses",
                    amount: expenses,
                    color: .red
                )
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}

struct BalanceItem: View {
    let title: String
    let amount: Double
    let color: Color
    
    var body: some View {
        VStack {
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
            
            Text("$\(amount, specifier: "%.2f")")
                .font(.title3)
                .fontWeight(.semibold)
                .foregroundColor(color)
        }
    }
}

struct TransactionRow: View {
    let title: String
    let amount: Double
    let isExpense: Bool
    let category: String
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                
                Text(category)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Text("\(isExpense ? "-" : "+")$\(amount, specifier: "%.2f")")
                .font(.headline)
                .fontWeight(.semibold)
                .foregroundColor(isExpense ? .red : .green)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(8)
    }
}

struct DashboardView_Previews: PreviewProvider {
    static var previews: some View {
        DashboardView()
    }
}