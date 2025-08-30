import SwiftUI

struct LoginView: View {
    @EnvironmentObject var authManager: AuthManager
    @State private var email = ""
    @State private var password = ""
    
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
                    if authManager.isLoading {
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
                .disabled(authManager.isLoading || email.isEmpty || password.isEmpty)
                
                Button("Don't have an account? Sign up") {
                    signup()
                }
                .disabled(authManager.isLoading)
            }
            
            Spacer()
        }
        .padding()
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

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .environmentObject(AuthManager())
    }
}