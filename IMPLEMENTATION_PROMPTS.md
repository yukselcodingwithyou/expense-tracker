# Implementation Prompts for Missing Components

This document provides detailed implementation prompts for the critical missing components in the Expense Tracker project. Use these prompts to guide development of the missing functionality.

---

## Prompt 1: Budget Management System (Backend)

### Task
Implement a comprehensive budget management system for the expense tracker backend.

### Requirements
1. **Domain Model**: Create a `Budget` entity that supports:
   - Budget periods (monthly, quarterly, yearly, custom)
   - Overall budget limits and category-specific limits
   - Alert thresholds and notifications
   - Family-scoped budgets

2. **Repository Layer**: Implement `BudgetRepository` with MongoDB integration

3. **Service Layer**: Create `BudgetService` with the following capabilities:
   - Create/update/delete budgets
   - Calculate spending against budgets
   - Generate budget alerts when thresholds are exceeded
   - Track budget performance over time

4. **Controller Layer**: Implement `BudgetController` with REST endpoints:
   - `POST /api/v1/budgets` - Create budget
   - `GET /api/v1/budgets` - List budgets for family
   - `GET /api/v1/budgets/{id}` - Get budget details
   - `PUT /api/v1/budgets/{id}` - Update budget
   - `DELETE /api/v1/budgets/{id}` - Delete budget
   - `GET /api/v1/budgets/{id}/status` - Get current budget status

5. **DTOs**: Create request/response DTOs following existing patterns

### Technical Specifications
- Use existing `BudgetDTO` as a starting point
- Integrate with existing `LedgerEntry` system for spending calculations
- Follow existing security patterns with `@AuthenticationPrincipal UserPrincipal`
- Include proper validation annotations
- Add Swagger/OpenAPI documentation
- Write unit tests following existing test patterns

### Implementation Notes
- Consider budget periods and how they relate to spending calculations
- Implement proper family-scoped access control
- Handle currency conversion if needed
- Consider performance for budget status calculations

---

## Prompt 2: Recurring Transactions System (Backend)

### Task
Implement a recurring transactions system that automatically creates transactions based on user-defined rules.

### Requirements
1. **Domain Model**: Create `RecurringRule` entity with:
   - Recurrence patterns (daily, weekly, monthly, yearly)
   - Start/end dates
   - Transaction template (amount, category, description)
   - Next execution date
   - Active/inactive status

2. **Repository Layer**: Implement `RecurringRuleRepository`

3. **Service Layer**: Create `RecurringService` with:
   - CRUD operations for recurring rules
   - Scheduled job to process pending recurring transactions
   - Transaction creation based on rules
   - Rule execution tracking

4. **Controller Layer**: Implement `RecurringController` with endpoints:
   - `POST /api/v1/recurring` - Create recurring rule
   - `GET /api/v1/recurring` - List user's recurring rules
   - `GET /api/v1/recurring/{id}` - Get recurring rule details
   - `PUT /api/v1/recurring/{id}` - Update recurring rule
   - `DELETE /api/v1/recurring/{id}` - Delete recurring rule
   - `POST /api/v1/recurring/{id}/execute` - Manually execute rule

5. **Scheduler**: Implement Spring `@Scheduled` task for automatic execution

### Technical Specifications
- Use existing `RecurringRuleDTO` as reference
- Integrate with `LedgerService` for transaction creation
- Implement proper error handling for failed executions
- Add execution history tracking
- Consider timezone handling for scheduling
- Include retry logic for failed executions

---

## Prompt 3: Reports and Analytics System (Backend)

### Task
Implement comprehensive reporting and analytics system for financial data analysis.

### Requirements
1. **Service Layer**: Create `ReportService` with capabilities:
   - Generate spending summaries by category/time period
   - Calculate trends and insights
   - Export data in multiple formats (JSON, CSV, PDF)
   - Budget vs actual spending analysis
   - Custom date range reports

2. **Controller Layer**: Implement `ReportController` with endpoints:
   - `GET /api/v1/reports/summary` - Get spending summary
   - `GET /api/v1/reports/trends` - Get spending trends
   - `GET /api/v1/reports/categories` - Category breakdown
   - `GET /api/v1/reports/budget-analysis` - Budget performance
   - `GET /api/v1/reports/export` - Export data

3. **DTOs**: Create response DTOs for various report types:
   - `ReportSummaryResponse`
   - `SpendingTrendResponse`
   - `CategoryBreakdownResponse`
   - `BudgetAnalysisResponse`

### Technical Specifications
- Use MongoDB aggregation pipelines for efficient data processing
- Implement caching for frequently requested reports
- Support multiple date range formats
- Include proper error handling for large datasets
- Add pagination for large reports
- Consider performance optimization for complex queries

---

## Prompt 4: Android App Data Layer Implementation

### Task
Implement the complete data layer for the Android app, replacing mock implementations with real API integration.

### Requirements
1. **Network Layer**:
   - Set up Retrofit with OkHttp for API calls
   - Implement proper error handling and retry logic
   - Add authentication interceptor for token management
   - Configure timeout and connection settings

2. **Repository Pattern**:
   - `AuthRepository` - Authentication operations
   - `CategoryRepository` - Category management
   - `LedgerRepository` - Transaction operations
   - `BudgetRepository` - Budget management
   - `FamilyRepository` - Family operations

3. **Data Models**:
   - Create Kotlin data classes matching backend DTOs
   - Implement proper JSON serialization
   - Add validation and error handling

4. **Local Storage**:
   - Set up Room database for offline capabilities
   - Implement data synchronization logic
   - Cache frequently accessed data

5. **Dependency Injection**:
   - Complete Hilt setup with proper modules
   - Provide singletons for repositories and services
   - Configure different implementations for testing

### Technical Specifications
- Use existing UI components and screens
- Replace all mock data and TODO implementations
- Implement proper error states in ViewModels
- Add loading states and user feedback
- Follow existing code patterns and structure
- Include comprehensive error handling

### Implementation Notes
- Remove all `kotlinx.coroutines.delay()` simulation calls
- Replace mock data with real API responses
- Implement proper navigation based on authentication state
- Add offline-first capabilities where appropriate

---

## Prompt 5: iOS App Data Layer Implementation

### Task
Implement the complete data layer for the iOS app, replacing mock implementations with real API integration.

### Requirements
1. **API Service Layer**:
   - Complete `APIService` implementation with all endpoints
   - Implement secure token storage using Keychain
   - Add automatic token refresh logic
   - Implement proper error handling

2. **Data Models**:
   - Create Swift structs matching backend DTOs
   - Implement proper JSON codable conformance
   - Add validation and error handling

3. **Repository Pattern**:
   - Create repository protocols and implementations
   - `AuthRepository`, `CategoryRepository`, `LedgerRepository`, etc.
   - Implement data caching and synchronization

4. **Local Storage**:
   - Set up Core Data or SwiftData for persistence
   - Implement offline capabilities
   - Add data migration support

5. **Authentication Manager**:
   - Complete `AuthManager` with real authentication
   - Implement proper session management
   - Add logout and token refresh handling

### Technical Specifications
- Remove all `Task.sleep()` simulation calls
- Implement real authentication flow
- Add proper error states in ViewModels
- Follow existing SwiftUI patterns
- Include comprehensive error handling
- Add proper navigation flow management

---

## Prompt 6: Notification System (Backend)

### Task
Implement a comprehensive notification system for budget alerts, reminders, and user notifications.

### Requirements
1. **Domain Model**: Create `Notification` entity with:
   - Notification types (budget alerts, reminders, system notifications)
   - Target user and family
   - Delivery channels (in-app, email, push)
   - Status tracking (sent, delivered, read)

2. **Service Layer**: Create `NotificationService` with:
   - Create and send notifications
   - Email integration for notifications
   - Budget alert generation
   - Notification history and management

3. **Controller Layer**: Implement `NotificationController` for:
   - Getting user notifications
   - Marking notifications as read
   - Notification preferences management

4. **Integration**: Connect with:
   - Budget system for automatic alerts
   - Email service for notifications
   - WebSocket for real-time notifications

### Technical Specifications
- Implement email integration using Spring Mail
- Add template engine for notification formatting
- Include retry logic for failed deliveries
- Add user preferences for notification types
- Consider push notification integration for mobile apps

---

## Prompt 7: File Upload and Attachment System (Backend)

### Task
Implement file upload functionality for receipt attachments using MinIO object storage.

### Requirements
1. **Service Layer**: Create `FileUploadService` with:
   - File upload to MinIO
   - File validation (type, size, virus scanning)
   - File metadata management
   - Secure file access URLs

2. **Controller Layer**: Implement `FileController` with:
   - File upload endpoints
   - File download endpoints
   - File metadata endpoints

3. **Integration**: Connect with:
   - Existing MinIO container
   - LedgerEntry for attachments
   - Security for access control

### Technical Specifications
- Support common image formats (JPG, PNG, PDF)
- Implement file size limits and validation
- Add virus scanning if possible
- Generate secure, time-limited download URLs
- Include proper error handling for upload failures

---

## Prompt 8: CI/CD Pipeline Setup

### Task
Set up a complete CI/CD pipeline using GitHub Actions for automated testing, building, and deployment.

### Requirements
1. **Backend Pipeline**:
   - Run tests on multiple Java versions
   - Build Docker images
   - Security scanning
   - Deploy to staging/production

2. **Android Pipeline**:
   - Run unit and UI tests
   - Build APK/AAB files
   - Code quality checks
   - Play Store deployment (optional)

3. **iOS Pipeline**:
   - Run tests on simulators
   - Build for App Store
   - Code signing and certificates
   - TestFlight deployment (optional)

4. **Infrastructure**:
   - Database migrations
   - Environment-specific deployments
   - Rollback capabilities

### Technical Specifications
- Use GitHub Actions workflows
- Implement proper secret management
- Add quality gates and approvals
- Include performance testing
- Set up monitoring and alerting

---

## Usage Instructions

1. **Choose Priority**: Start with Prompt 1 (Budget System) as it's a core feature
2. **Use Incrementally**: Implement one prompt at a time
3. **Test Thoroughly**: Each implementation should include comprehensive tests
4. **Update Documentation**: Update API documentation and user guides
5. **Review Integration**: Ensure new components integrate properly with existing code

## Implementation Order Recommendation

1. **Budget Management System** (Backend) - Core business feature
2. **Android Data Layer** - Essential for mobile app functionality  
3. **iOS Data Layer** - Complete mobile app implementation
4. **Recurring Transactions** - Important automation feature
5. **Reports and Analytics** - User value and insights
6. **Notification System** - User engagement
7. **File Upload System** - Enhanced functionality
8. **CI/CD Pipeline** - Production readiness

Each prompt is designed to be self-contained but may reference integration points with other components. Follow the existing code patterns and architecture when implementing these features.