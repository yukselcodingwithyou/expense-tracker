# Expense Tracker

A comprehensive expense tracking application with backend API, Android app, and future iOS support.

## Features

### Backend (Spring Boot)
- 🔐 **Authentication**: JWT-based auth with Argon2 password hashing
- 👥 **Multi-tenant**: Family-based expense management
- 💰 **Financial Tracking**: Income/expense categorization with MongoDB storage
- 📊 **Reporting**: Summary, detailed, and visualization reports
- 🔄 **Recurring Transactions**: Automated recurring expense/income entries
- 💾 **Export**: CSV and PDF export capabilities
- 🛡️ **Security**: Rate limiting, CORS configuration, input validation
- 📈 **Observability**: Actuator health checks, Prometheus metrics
- 🧪 **Testing**: Comprehensive unit and integration tests

### Android App (Jetpack Compose)
- 🎨 **Modern UI**: Material Design 3 with Jetpack Compose
- 🏗️ **Clean Architecture**: MVVM with Hilt dependency injection
- 🔄 **Offline Support**: Room database with network sync
- 📱 **Responsive Design**: Adaptive UI for different screen sizes
- 🔐 **Secure Storage**: Encrypted DataStore for token management

### Infrastructure
- 🐳 **Docker**: Complete containerization with docker-compose
- 🗄️ **Databases**: MongoDB for data, Redis for caching/sessions
- 📦 **Build Tools**: Maven (backend), Gradle (Android)
- 🔧 **Development**: Hot reload, comprehensive Make targets

## Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Android Studio (for Android app)
- Make (optional, for convenience commands)

### 1. Start Backend Services

```bash
# Clone and navigate to the project
git clone <repository-url>
cd expense-tracker

# Start all services with docker-compose
make up
# or manually: docker-compose up -d

# The backend will be available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# MongoDB: localhost:27017
# Redis: localhost:6379
# MinIO: localhost:9000 (console: localhost:9001)
```

### 2. Test Backend API

```bash
# Check health
curl http://localhost:8080/actuator/health

# Sign up a new user
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### 3. Run Android App

```bash
# Open Android Studio and import the android/ directory
# Or use command line:
cd android
./gradlew assembleDebug

# Install on device/emulator
./gradlew installDebug
```

## Project Structure

```
expense-tracker/
├── backend/                 # Spring Boot API
│   ├── src/main/java/com/expensetracker/
│   │   ├── controller/      # REST endpoints
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Data access
│   │   ├── domain/          # Entity models
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── security/        # JWT & authentication
│   │   └── config/          # Spring configuration
│   ├── src/test/            # Unit & integration tests
│   ├── Dockerfile
│   └── pom.xml
├── android/                 # Android Jetpack Compose app
│   ├── app/src/main/java/com/expensetracker/
│   │   ├── ui/              # Compose screens & themes
│   │   ├── data/            # Repositories & data sources
│   │   ├── domain/          # Models & use cases
│   │   └── di/              # Hilt modules
│   └── app/build.gradle
├── docker-compose.yml       # Full stack services
├── Makefile                 # Development commands
└── README.md
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/signup` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/logout` - User logout
- `POST /api/v1/auth/token/refresh` - Refresh JWT token

### Families
- `POST /api/v1/families` - Create family
- `GET /api/v1/families/{id}` - Get family details

### Categories
- `GET /api/v1/categories` - List categories
- `POST /api/v1/categories` - Create category
- `PATCH /api/v1/categories/{id}` - Update category
- `DELETE /api/v1/categories/{id}` - Delete category

### Ledger
- `GET /api/v1/ledger` - List transactions (paginated)
- `POST /api/v1/ledger` - Create transaction
- `PATCH /api/v1/ledger/{id}` - Update transaction
- `DELETE /api/v1/ledger/{id}` - Delete transaction
- `GET /api/v1/ledger/recent` - Get recent transactions

## Development Commands

```bash
# Build everything
make build

# Run tests
make test

# Start services
make up

# Stop services
make down

# View logs
make logs

# Quick development cycle
make dev

# Backend only
make backend-build
make backend-test
make backend-run

# Smoke test
make smoke-test
```

## Configuration

### Backend Environment Variables
- `SPRING_DATA_MONGODB_URI` - MongoDB connection string
- `SPRING_DATA_REDIS_HOST` - Redis host
- `APP_JWT_SECRET` - JWT signing secret
- `GOOGLE_CLIENT_ID` - Google OAuth client ID

### Android Configuration
- Backend URL configured in `BuildConfig.BASE_URL`
- Debug: `http://10.0.2.2:8080/api/v1/` (Android emulator)
- Release: Update to production URL

## Database Schema

### MongoDB Collections
- **users**: User accounts with email/password and family memberships
- **families**: Family groups with currency settings
- **categories**: Income/expense categories with icons and colors
- **ledger**: Transaction entries with amounts, dates, and metadata
- **recurringRules**: Automated recurring transaction rules
- **budgets**: Budget limits and tracking
- **goals**: Savings goals and progress
- **notifications**: System notifications
- **exports**: Generated report files

## Security Features
- **Password Hashing**: Argon2id for secure password storage
- **JWT Tokens**: Short-lived access tokens + refresh tokens
- **Rate Limiting**: Protect auth endpoints and exports
- **Input Validation**: Bean validation on all inputs
- **CORS**: Configured for mobile app schemes
- **Soft Deletes**: Preserve data integrity

## Testing
- **Backend**: JUnit 5, Mockito, RestAssured, Testcontainers
- **Android**: JUnit, MockK, Compose UI tests
- **Coverage**: Target ≥80% for business logic

## Future Enhancements
- 📱 iOS app with SwiftUI
- 📧 Email notifications
- 📊 Advanced analytics and insights
- 🌍 Multi-language support
- 🔄 Real-time synchronization
- 💳 Bank account integration
- 📸 Receipt photo capture and OCR

## Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes with tests
4. Run `make test` to ensure all tests pass
5. Submit a pull request

## License
MIT License - see LICENSE file for details