import SwiftUI

@main
struct ExpenseTrackerApp: App {
    // Initialize persistence controller as shared instance
    let persistenceController = PersistenceController.shared
    
    var body: some Scene {
        WindowGroup {
            RootNavigationView()
                .environment(\.managedObjectContext, persistenceController.context)
        }
    }
}