import SwiftUI

struct WelcomeView: View {
    @State private var currentPage = 0
    private let pages = [
        OnboardingPage(
            title: "Track Your Expenses",
            subtitle: "Keep track of your family's income and expenses with our easy-to-use interface",
            imageName: "chart.pie"
        ),
        OnboardingPage(
            title: "Set Savings Goals",
            subtitle: "Create and monitor your savings goals to achieve your financial dreams",
            imageName: "target"
        ),
        OnboardingPage(
            title: "Family Financial Management",
            subtitle: "Manage your family's finances together with multiple user accounts and permissions",
            imageName: "person.3"
        )
    ]
    
    var body: some View {
        VStack(spacing: Theme.Spacing.xxxl) {
            Spacer()
            
            // Page indicator placeholder image
            Image(systemName: pages[currentPage].imageName)
                .font(.system(size: 120))
                .foregroundColor(Theme.Colors.primary)
                .frame(height: 200)
            
            VStack(spacing: Theme.Spacing.lg) {
                Text(pages[currentPage].title)
                    .font(Theme.Typography.titleXL)
                    .foregroundColor(Theme.Colors.onSurface)
                    .multilineTextAlignment(.center)
                
                Text(pages[currentPage].subtitle)
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, Theme.Spacing.xl)
            }
            
            Spacer()
            
            VStack(spacing: Theme.Spacing.lg) {
                // Page indicators
                HStack(spacing: Theme.Spacing.sm) {
                    ForEach(0..<pages.count, id: \.self) { index in
                        Circle()
                            .fill(index == currentPage ? Theme.Colors.primary : Theme.Colors.cardBorder)
                            .frame(width: 8, height: 8)
                    }
                }
                
                // Navigation buttons
                if currentPage < pages.count - 1 {
                    PrimaryButton(title: "Next") {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            currentPage += 1
                        }
                    }
                } else {
                    NavigationLink(destination: AuthSelectionView()) {
                        Text("Get Started")
                            .font(Theme.Typography.label)
                            .foregroundColor(Theme.Colors.onPrimary)
                            .padding(.horizontal, Theme.Spacing.lg)
                            .padding(.vertical, Theme.Spacing.md)
                            .background(
                                RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                    .fill(Theme.Colors.primary)
                            )
                    }
                    .buttonStyle(PlainButtonStyle())
                }
                
                if currentPage > 0 {
                    Button("Previous") {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            currentPage -= 1
                        }
                    }
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                }
            }
        }
        .padding(Theme.Spacing.xl)
        .background(Theme.Colors.surface)
        .navigationBarHidden(true)
    }
}

struct OnboardingPage {
    let title: String
    let subtitle: String
    let imageName: String
}

struct AuthSelectionView: View {
    var body: some View {
        VStack(spacing: Theme.Spacing.xxxl) {
            Spacer()
            
            // App logo/illustration placeholder
            Image(systemName: "dollarsign.circle.fill")
                .font(.system(size: 120))
                .foregroundColor(Theme.Colors.primary)
            
            VStack(spacing: Theme.Spacing.lg) {
                Text("Family Finance Tracker")
                    .font(Theme.Typography.titleXL)
                    .foregroundColor(Theme.Colors.onSurface)
                    .multilineTextAlignment(.center)
                
                Text("Manage your family's finances together")
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    .multilineTextAlignment(.center)
            }
            
            Spacer()
            
            VStack(spacing: Theme.Spacing.lg) {
                // Google Sign In Button
                Button(action: {
                    // Handle Google sign in
                }) {
                    HStack(spacing: Theme.Spacing.md) {
                        Image(systemName: "globe")
                            .foregroundColor(Theme.Colors.onSurface)
                        
                        Text("Continue with Google")
                            .font(Theme.Typography.label)
                            .foregroundColor(Theme.Colors.onSurface)
                    }
                    .padding(.horizontal, Theme.Spacing.lg)
                    .padding(.vertical, Theme.Spacing.md)
                    .frame(maxWidth: .infinity)
                    .background(
                        RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                            .fill(Theme.Colors.card)
                            .overlay(
                                RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                    .stroke(Theme.Colors.cardBorder, lineWidth: 1)
                            )
                    )
                }
                
                // Email Sign In/Up
                NavigationLink(destination: LoginView()) {
                    HStack(spacing: Theme.Spacing.md) {
                        Image(systemName: "envelope")
                            .foregroundColor(Theme.Colors.onPrimary)
                        
                        Text("Continue with Email")
                            .font(Theme.Typography.label)
                            .foregroundColor(Theme.Colors.onPrimary)
                    }
                    .padding(.horizontal, Theme.Spacing.lg)
                    .padding(.vertical, Theme.Spacing.md)
                    .frame(maxWidth: .infinity)
                    .background(
                        RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                            .fill(Theme.Colors.primary)
                    )
                }
                .buttonStyle(PlainButtonStyle())
                
                // Terms and Privacy
                VStack(spacing: Theme.Spacing.xs) {
                    Text("By continuing, you agree to our")
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                    
                    HStack(spacing: 4) {
                        Button("Terms of Service") {
                            // Handle terms
                        }
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.primary)
                        
                        Text("and")
                            .font(Theme.Typography.caption)
                            .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
                        
                        Button("Privacy Policy") {
                            // Handle privacy
                        }
                        .font(Theme.Typography.caption)
                        .foregroundColor(Theme.Colors.primary)
                    }
                }
            }
        }
        .padding(Theme.Spacing.xl)
        .background(Theme.Colors.surface)
        .navigationBarHidden(true)
    }
}

struct EnhancedLoginView: View {
    @EnvironmentObject var authManager: AuthManager
    @State private var email = ""
    @State private var password = ""
    @State private var isSignUp = false
    
    var body: some View {
        VStack(spacing: Theme.Spacing.xl) {
            Spacer()
            
            // Logo
            Image(systemName: "dollarsign.circle.fill")
                .font(.system(size: 80))
                .foregroundColor(Theme.Colors.primary)
            
            VStack(spacing: Theme.Spacing.lg) {
                Text(isSignUp ? "Create Account" : "Welcome Back")
                    .font(Theme.Typography.titleL)
                    .foregroundColor(Theme.Colors.onSurface)
                
                Text(isSignUp ? "Join Family Finance Tracker" : "Sign in to your account")
                    .font(Theme.Typography.body)
                    .foregroundColor(Theme.Colors.onSurface.opacity(0.7))
            }
            
            VStack(spacing: Theme.Spacing.lg) {
                FormTextField(
                    label: "Email",
                    text: $email,
                    placeholder: "Enter your email"
                )
                .keyboardType(.emailAddress)
                .autocapitalization(.none)
                
                VStack(alignment: .leading, spacing: Theme.Spacing.xs) {
                    Text("Password")
                        .font(Theme.Typography.label)
                        .foregroundColor(Theme.Colors.onSurface)
                    
                    SecureField("Enter your password", text: $password)
                        .font(Theme.Typography.body)
                        .padding(Theme.Spacing.md)
                        .background(
                            RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                .fill(Theme.Colors.card)
                                .overlay(
                                    RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                                        .stroke(Theme.Colors.cardBorder, lineWidth: 1)
                                )
                        )
                }
                
                Button(action: isSignUp ? signup : login) {
                    if authManager.isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: Theme.Colors.onPrimary))
                    } else {
                        Text(isSignUp ? "Create Account" : "Sign In")
                    }
                }
                .font(Theme.Typography.label)
                .foregroundColor(Theme.Colors.onPrimary)
                .padding(.horizontal, Theme.Spacing.lg)
                .padding(.vertical, Theme.Spacing.md)
                .frame(maxWidth: .infinity)
                .background(
                    RoundedRectangle(cornerRadius: Theme.Shapes.buttonCornerRadius)
                        .fill(Theme.Colors.primary)
                        .opacity(authManager.isLoading || email.isEmpty || password.isEmpty ? 0.6 : 1.0)
                )
                .disabled(authManager.isLoading || email.isEmpty || password.isEmpty)
                
                Button(isSignUp ? "Already have an account? Sign In" : "Don't have an account? Sign Up") {
                    isSignUp.toggle()
                }
                .font(Theme.Typography.body)
                .foregroundColor(Theme.Colors.primary)
            }
            
            Spacer()
        }
        .padding(Theme.Spacing.xl)
        .background(Theme.Colors.surface)
        .navigationBarHidden(true)
    }
    
    private func login() {
        Task {
            await authManager.login(email: email, password: password)
        }
    }
    
    private func signup() {
        Task {
            await authManager.signup(email: email, password: password)
        }
    }
}

#Preview {
    WelcomeView()
}

#Preview {
    AuthSelectionView()
}

#Preview {
    EnhancedLoginView()
        .environmentObject(AuthManager())
}