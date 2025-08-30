import SwiftUI

struct SmartExpenseEntryView: View {
    @StateObject private var receiptManager = ReceiptManager()
    @StateObject private var voiceManager = VoiceExpenseManager()
    
    @State private var showingReceiptCamera = false
    @State private var showingVoiceEntry = false
    @State private var capturedReceiptImage: UIImage?
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                // Header
                VStack(spacing: 8) {
                    Text("Smart Expense Entry")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    
                    Text("Add expenses using your camera or voice")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
                .padding(.top)
                
                Spacer()
                
                // Feature cards
                VStack(spacing: 20) {
                    // Receipt OCR Card
                    ExpenseFeatureCard(
                        icon: "camera.fill",
                        title: "Scan Receipt",
                        description: "Take a photo of your receipt and we'll extract the expense details automatically",
                        buttonText: "Capture Receipt",
                        isLoading: receiptManager.isProcessing,
                        action: {
                            showingReceiptCamera = true
                        }
                    )
                    
                    // Voice Entry Card
                    ExpenseFeatureCard(
                        icon: "mic.fill",
                        title: "Voice Entry",
                        description: "Say your expense out loud and we'll parse the amount, category, and details",
                        buttonText: "Start Recording",
                        isLoading: voiceManager.isProcessing,
                        action: {
                            showingVoiceEntry = true
                        }
                    )
                }
                
                Spacer()
                
                // Recent processed items (if any)
                if !receiptManager.receipts.isEmpty || !voiceManager.voiceExpenses.isEmpty {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Recent Activity")
                            .font(.headline)
                            .padding(.horizontal)
                        
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 12) {
                                ForEach(receiptManager.receipts.prefix(3)) { receipt in
                                    RecentReceiptCard(receipt: receipt)
                                }
                                
                                ForEach(voiceManager.voiceExpenses.prefix(3)) { voiceExpense in
                                    RecentVoiceExpenseCard(voiceExpense: voiceExpense)
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                }
            }
            .padding()
            .navigationBarHidden(true)
            .onAppear {
                receiptManager.loadUserReceipts()
                voiceManager.loadUserVoiceExpenses()
            }
        }
        .sheet(isPresented: $showingReceiptCamera) {
            ReceiptCameraView(capturedImage: $capturedReceiptImage)
                .onDisappear {
                    if let image = capturedReceiptImage {
                        receiptManager.processReceiptImage(image)
                        capturedReceiptImage = nil
                    }
                }
        }
        .sheet(isPresented: $showingVoiceEntry) {
            VoiceExpenseEntryView { spokenText in
                showingVoiceEntry = false
                voiceManager.processVoiceExpense(spokenText: spokenText)
            }
        }
        .sheet(isPresented: $receiptManager.showReceiptDetails) {
            if let receipt = receiptManager.currentReceipt {
                ReceiptDetailsView(receipt: receipt, receiptManager: receiptManager)
            }
        }
        .sheet(isPresented: $voiceManager.showExpenseDetails) {
            if let voiceExpense = voiceManager.currentVoiceExpense {
                VoiceExpenseDetailsView(voiceExpense: voiceExpense, voiceManager: voiceManager)
            }
        }
        .alert("Success!", isPresented: $receiptManager.expenseCreated) {
            Button("OK") {
                receiptManager.clearReceiptDetails()
            }
        } message: {
            Text("Expense created successfully from receipt!")
        }
        .alert("Success!", isPresented: $voiceManager.expenseCreated) {
            Button("OK") {
                voiceManager.clearExpenseDetails()
            }
        } message: {
            Text("Expense created successfully from voice input!")
        }
        .alert("Error", isPresented: .constant(receiptManager.error != nil || voiceManager.error != nil)) {
            Button("OK") {
                receiptManager.clearError()
                voiceManager.clearError()
            }
        } message: {
            Text(receiptManager.error ?? voiceManager.error ?? "Unknown error")
        }
    }
}

struct ExpenseFeatureCard: View {
    let icon: String
    let title: String
    let description: String
    let buttonText: String
    let isLoading: Bool
    let action: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            HStack {
                Image(systemName: icon)
                    .font(.system(size: 32))
                    .foregroundColor(.blue)
                    .frame(width: 50, height: 50)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.headline)
                        .fontWeight(.semibold)
                    
                    Text(description)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .fixedSize(horizontal: false, vertical: true)
                }
                
                Spacer()
            }
            
            Button(action: action) {
                HStack {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .scaleEffect(0.8)
                    } else {
                        Image(systemName: icon)
                    }
                    
                    Text(isLoading ? "Processing..." : buttonText)
                        .fontWeight(.semibold)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 44)
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .disabled(isLoading)
        }
        .padding()
        .background(Color.secondary.opacity(0.1))
        .cornerRadius(12)
    }
}

struct RecentReceiptCard: View {
    let receipt: ReceiptData
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(receipt.storeName ?? "Unknown Store")
                .font(.headline)
                .lineLimit(1)
            
            Text("$\(receipt.totalAmount, specifier: "%.2f")")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(.green)
            
            Text("Receipt • \(Int(receipt.confidence * 100))% confident")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(width: 140)
        .padding(12)
        .background(Color.secondary.opacity(0.1))
        .cornerRadius(8)
    }
}

struct RecentVoiceExpenseCard: View {
    let voiceExpense: VoiceExpenseData
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(voiceExpense.parseResult.description)
                .font(.headline)
                .lineLimit(1)
            
            if let amountMinor = voiceExpense.parseResult.amountMinor {
                Text("$\(Double(amountMinor) / 100.0, specifier: "%.2f")")
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(.green)
            }
            
            Text("Voice • \(Int(voiceExpense.overallConfidence * 100))% confident")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(width: 140)
        .padding(12)
        .background(Color.secondary.opacity(0.1))
        .cornerRadius(8)
    }
}

// Placeholder detail views - these would be fully implemented in a real app
struct ReceiptDetailsView: View {
    let receipt: ReceiptData
    let receiptManager: ReceiptManager
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Receipt Details")
                    .font(.title)
                Text("Store: \(receipt.storeName ?? "Unknown")")
                Text("Amount: $\(receipt.totalAmount, specifier: "%.2f")")
                
                Spacer()
                
                Button("Create Expense") {
                    // Implementation would create expense from receipt
                    receiptManager.clearReceiptDetails()
                }
                .buttonStyle(.borderedProminent)
            }
            .padding()
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Close") {
                        receiptManager.clearReceiptDetails()
                    }
                }
            }
        }
    }
}

struct VoiceExpenseDetailsView: View {
    let voiceExpense: VoiceExpenseData
    let voiceManager: VoiceExpenseManager
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Voice Expense Details")
                    .font(.title)
                Text("Spoken: \"\(voiceExpense.originalText)\"")
                    .italic()
                Text("Description: \(voiceExpense.parseResult.description)")
                if let amountMinor = voiceExpense.parseResult.amountMinor {
                    Text("Amount: $\(Double(amountMinor) / 100.0, specifier: "%.2f")")
                }
                
                Spacer()
                
                Button("Create Expense") {
                    // Implementation would create expense from voice data
                    voiceManager.clearExpenseDetails()
                }
                .buttonStyle(.borderedProminent)
            }
            .padding()
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Close") {
                        voiceManager.clearExpenseDetails()
                    }
                }
            }
        }
    }
}

#Preview {
    SmartExpenseEntryView()
}