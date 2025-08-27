import SwiftUI

@main
struct ExpenseTrackerApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(AuthManager())
        }
    }
}