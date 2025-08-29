import SwiftUI

struct WelcomeFeature {
    let title: String
    let description: String
    let iconName: String
}

struct WelcomeScreen: View {
    @State private var currentPage = 0
    let onCompleteWelcome: () -> Void
    
    private let features = [
        WelcomeFeature(
            title: "Track Your Expenses",
            description: "Easily track all your family's income and expenses in one place. Categorize transactions and see where your money goes.",
            iconName: "receipt"
        ),
        WelcomeFeature(
            title: "Set Budgets & Goals",
            description: "Create monthly budgets for different categories and set savings goals. Get alerts when you're approaching your limits.",
            iconName: "chart.line.uptrend.xyaxis"
        ),
        WelcomeFeature(
            title: "Manage Recurring Transactions",
            description: "Set up recurring income and expenses like salary, rent, and subscriptions. Never miss a payment again.",
            iconName: "arrow.clockwise"
        ),
        WelcomeFeature(
            title: "Family Collaboration",
            description: "Invite family members to contribute to your shared budget. Everyone can add transactions and view reports.",
            iconName: "person.3"
        ),
        WelcomeFeature(
            title: "Detailed Reports",
            description: "Get insights with comprehensive reports and visualizations. Export data to PDF or CSV for your records.",
            iconName: "chart.bar"
        )
    ]
    
    var body: some View {
        VStack(spacing: 0) {
            // Skip button
            HStack {
                Spacer()
                
                Button("Skip") {
                    onCompleteWelcome()
                }
                .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                .padding()
            }
            
            // Content
            TabView(selection: $currentPage) {
                ForEach(features.indices, id: \.self) { index in
                    WelcomeFeaturePage(feature: features[index])
                        .tag(index)
                }
            }
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            .animation(.easeInOut, value: currentPage)
            
            // Page indicators
            HStack(spacing: Theme.Spacing.xs) {
                ForEach(features.indices, id: \.self) { index in
                    Circle()
                        .fill(index == currentPage ? Theme.Colors.primary : Theme.Colors.onSurface.opacity(0.3))
                        .frame(width: 8, height: 8)
                        .animation(.easeInOut, value: currentPage)
                }
            }
            .padding(.vertical, Theme.Spacing.lg)
            
            // Navigation buttons
            HStack {
                if currentPage > 0 {
                    SecondaryButton(title: "Previous") {
                        withAnimation {
                            currentPage -= 1
                        }
                    }
                } else {
                    Spacer()
                        .frame(width: 100)
                }
                
                Spacer()
                
                if currentPage < features.count - 1 {
                    PrimaryButton(title: "Next") {
                        withAnimation {
                            currentPage += 1
                        }
                    }
                } else {
                    PrimaryButton(title: "Get Started") {
                        onCompleteWelcome()
                    }
                }
            }
            .padding(.horizontal, Theme.Spacing.lg)
            .padding(.bottom, Theme.Spacing.lg)
        }
        .background(Theme.Colors.surface)
    }
}

struct WelcomeFeaturePage: View {
    let feature: WelcomeFeature
    
    var body: some View {
        VStack(spacing: Theme.Spacing.xl) {
            Spacer()
            
            // Feature icon
            ZStack {
                Circle()
                    .fill(Theme.Colors.primary.opacity(0.1))
                    .frame(width: 120, height: 120)
                
                Image(systemName: feature.iconName)
                    .font(.system(size: 60))
                    .foregroundColor(Theme.Colors.primary)
            }
            
            VStack(spacing: Theme.Spacing.lg) {
                // Feature title
                Text(feature.title)
                    .font(Theme.Typography.titleXL)
                    .foregroundColor(Theme.Colors.onSurface)
                    .multilineTextAlignment(.center)
                
                // Feature description
                Text(feature.description)
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    .multilineTextAlignment(.center)
                    .lineSpacing(4)
                    .padding(.horizontal, Theme.Spacing.lg)
            }
            
            Spacer()
        }
        .padding(Theme.Spacing.lg)
    }
}

#Preview {
    WelcomeScreen {
        // Handle completion
    }
}