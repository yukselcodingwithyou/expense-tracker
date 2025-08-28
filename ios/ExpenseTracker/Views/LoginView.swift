import SwiftUI

struct LoginView: View {
    @EnvironmentObject var authManager: AuthManager
    @State private var email = ""
    @State private var password = ""
    @State private var isLoading = false
    
    var body: some View {
        VStack(spacing: 20) {
            Spacer()
            
            Text("Expense Tracker")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Spacer()
            
            VStack(spacing: 16) {
                TextField("Email", text: $email)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                
                SecureField("Password", text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                
                Button(action: login) {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    } else {
                        Text("Login")
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)
                .disabled(isLoading || email.isEmpty || password.isEmpty)
                
                Button("Don't have an account? Sign up") {
                    signup()
                }
                .disabled(isLoading)
            }
            
            Spacer()
        }
        .padding()
    }
    
    private func login() {
        isLoading = true
        
        // TODO: Implement actual login
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            authManager.isAuthenticated = true
            isLoading = false
        }
    }
    
    private func signup() {
        isLoading = true
        
        // TODO: Implement actual signup
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            authManager.isAuthenticated = true
            isLoading = false
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .environmentObject(AuthManager())
    }
}