# Eksik Bileşenler için Implementasyon Promptları

Bu dokümanda Expense Tracker projesindeki **gerçek eksikliklerin** tamamlanması için detaylı implementasyon promptları yer almaktadır.

---

## 🔥 Prompt 1: Token Blacklisting Sistemi (Backend)

### Görev
Redis kullanarak logout işleminde token blacklisting implementasyonu.

### Durum
- **Dosya**: `backend/src/main/java/com/expensetracker/controller/AuthController.java:45`
- **Sorun**: Kullanıcı logout yaptıktan sonra token hala geçerli kalıyor
- **Risk Seviyesi**: Yüksek (Güvenlik açığı)

### Implementasyon Adımları

1. **TokenBlacklistService Genişletmesi**:
```java
@Service
public class TokenBlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void blacklistToken(String token, Duration expirationTime) {
        redisTemplate.opsForValue().set("blacklisted:" + token, true, expirationTime);
    }
    
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklisted:" + token));
    }
}
```

2. **AuthController.logout() Metodunu Güncelle**:
```java
@PostMapping("/logout")
public ResponseEntity<Map<String, String>> logout(
        HttpServletRequest request,
        @AuthenticationPrincipal UserPrincipal user) {
    
    String token = jwtTokenProvider.getTokenFromRequest(request);
    if (token != null) {
        Duration expirationTime = jwtTokenProvider.getExpirationTime(token);
        tokenBlacklistService.blacklistToken(token, expirationTime);
    }
    
    return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
}
```

3. **JWT Filter'a Blacklist Kontrolü Ekle**:
```java
// JwtAuthenticationFilter.java içinde
if (tokenBlacklistService.isTokenBlacklisted(token)) {
    throw new InvalidTokenException("Token has been blacklisted");
}
```

### Test Senaryoları
```java
@Test
void logout_shouldBlacklistToken() {
    // Test implementasyonu
}

@Test 
void blacklistedToken_shouldBeRejected() {
    // Test implementasyonu
}
```

**Tahmini Süre**: 3-4 saat

---

## 🔧 Prompt 2: Aile Geçiş Sistemi (Backend)

### Görev
UserService'da eksik olan aile seçimi ve geçiş mantığının implementasyonu.

### Durum
- **Dosya**: `backend/src/main/java/com/expensetracker/service/UserService.java:21`
- **Sorun**: Kullanıcılar birden fazla aileye üye olduğunda hangi ailenin aktif olduğu belirsiz

### Implementasyon Adımları

1. **User Domain Modelini Güncelle**:
```java
public class User {
    // Mevcut alanlar...
    private String currentFamilyId; // Aktif aile
    private List<String> familyIds; // Üye olunan tüm aileler
}
```

2. **UserService Metodlarını İmplement Et**:
```java
@Service
public class UserService {
    
    public String getCurrentUserFamilyId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String currentFamilyId = user.getCurrentFamilyId();
        
        // Eğer current family set edilmemişse, ilk aileyi varsayılan yap
        if (currentFamilyId == null && !user.getFamilyIds().isEmpty()) {
            currentFamilyId = user.getFamilyIds().get(0);
            user.setCurrentFamilyId(currentFamilyId);
            userRepository.save(user);
        }
        
        return currentFamilyId;
    }
    
    public void switchFamily(String userId, String familyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getFamilyIds().contains(familyId)) {
            throw new RuntimeException("User is not a member of this family");
        }
        
        user.setCurrentFamilyId(familyId);
        userRepository.save(user);
    }
    
    public List<Family> getUserFamilies(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return familyRepository.findAllById(user.getFamilyIds());
    }
}
```

3. **Yeni Controller Endpoint'leri**:
```java
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    
    @GetMapping("/families")
    public ResponseEntity<List<Family>> getUserFamilies(@AuthenticationPrincipal UserPrincipal user) {
        List<Family> families = userService.getUserFamilies(user.getId());
        return ResponseEntity.ok(families);
    }
    
    @PostMapping("/switch-family/{familyId}")
    public ResponseEntity<Map<String, String>> switchFamily(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String familyId) {
        
        userService.switchFamily(user.getId(), familyId);
        return ResponseEntity.ok(Map.of("message", "Family switched successfully"));
    }
}
```

**Tahmini Süre**: 5-6 saat

---

## 📧 Prompt 3: Email Bildirim Sistemi

### Görev
Bütçe uyarıları ve sistem bildirimleri için email sistemi.

### Implementasyon Adımları

1. **Notification Domain Model**:
```java
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String familyId;
    private NotificationType type;
    private String title;
    private String message;
    private Map<String, Object> data;
    private boolean isRead = false;
    private boolean emailSent = false;
    private Instant createdAt;
    
    public enum NotificationType {
        BUDGET_ALERT, BUDGET_EXCEEDED, WEEKLY_SUMMARY, MONTHLY_REPORT
    }
}
```

2. **EmailService**:
```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    public void sendBudgetAlert(User user, Budget budget, double usagePercentage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setFrom(fromEmail);
        message.setSubject("Bütçe Uyarısı - " + budget.getName());
        message.setText(String.format(
            "Merhaba %s,\n\n" +
            "%s bütçenizin %%%d'ını kullandınız.\n" +
            "Detaylar için uygulamayı kontrol edin.",
            user.getEmail(), budget.getName(), (int)usagePercentage
        ));
        
        mailSender.send(message);
    }
}
```

3. **NotificationService**:
```java
@Service
public class NotificationService {
    
    public void createBudgetAlert(String userId, String budgetId, double usagePercentage) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(Notification.NotificationType.BUDGET_ALERT);
        notification.setTitle("Bütçe Uyarısı");
        notification.setMessage("Bütçenizin %" + (int)usagePercentage + "'ını kullandınız");
        // ... diğer alanlar
        
        notificationRepository.save(notification);
        
        // Email gönder
        User user = userService.findById(userId);
        Budget budget = budgetService.findById(budgetId);
        emailService.sendBudgetAlert(user, budget, usagePercentage);
    }
}
```

4. **BudgetService'a Bildirim Entegrasyonu**:
```java
// BudgetService.getBudgetSpendingStatus metodunda
if (usagePercentage >= budget.getAlertThresholdPct()) {
    notificationService.createBudgetAlert(userId, budgetId, usagePercentage);
}
```

**Tahmini Süre**: 8-10 saat

---

## 📎 Prompt 4: Dosya Yükleme Sistemi (MinIO)

### Görev
Fiş ve fatura ekleme için MinIO entegrasyonu.

### Implementasyon Adımları

1. **Attachment Domain Model**:
```java
@Document(collection = "attachments")
public class Attachment {
    @Id
    private String id;
    private String ledgerEntryId;
    private String filename;
    private String originalFilename;
    private String contentType;
    private long size;
    private String minioObjectName;
    private Instant uploadedAt;
}
```

2. **FileUploadService**:
```java
@Service
public class FileUploadService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Value("${minio.bucket.attachments}")
    private String attachmentsBucket;
    
    public Attachment uploadFile(MultipartFile file, String ledgerEntryId) {
        try {
            String objectName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(attachmentsBucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            Attachment attachment = new Attachment();
            attachment.setLedgerEntryId(ledgerEntryId);
            attachment.setFilename(objectName);
            attachment.setOriginalFilename(file.getOriginalFilename());
            // ... diğer alanlar
            
            return attachmentRepository.save(attachment);
            
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
    
    public String getFileUrl(String attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(attachmentsBucket)
                    .object(attachment.getFilename())
                    .expiry(60 * 60 * 24) // 24 saat
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate file URL", e);
        }
    }
}
```

3. **FileController**:
```java
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    
    @PostMapping("/upload/{ledgerEntryId}")
    public ResponseEntity<Attachment> uploadFile(
            @PathVariable String ledgerEntryId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal user) {
        
        // Yetki kontrolü
        LedgerEntry entry = ledgerService.getEntryById(user.getId(), ledgerEntryId);
        
        Attachment attachment = fileUploadService.uploadFile(file, ledgerEntryId);
        return ResponseEntity.ok(attachment);
    }
    
    @GetMapping("/{attachmentId}/url")
    public ResponseEntity<Map<String, String>> getFileUrl(
            @PathVariable String attachmentId,
            @AuthenticationPrincipal UserPrincipal user) {
        
        String url = fileUploadService.getFileUrl(attachmentId);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
```

**Tahmini Süre**: 6-8 saat

---

## 📱 Prompt 5: Android Room Database (Offline Storage)

### Görev
Android uygulamasında offline depolama için Room database implementasyonu.

### Implementasyon Adımları

1. **Entity'ler**:
```kotlin
@Entity(tableName = "ledger_entries")
data class LedgerEntryEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val memberId: String,
    val type: String,
    val amountMinor: Long,
    val currency: String,
    val categoryId: String,
    val occurredAt: Long,
    val notes: String?,
    val isOnline: Boolean = false,
    val needsSync: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: String
)
```

2. **DAO'lar**:
```kotlin
@Dao
interface LedgerEntryDao {
    @Query("SELECT * FROM ledger_entries WHERE familyId = :familyId ORDER BY occurredAt DESC")
    fun getEntries(familyId: String): Flow<List<LedgerEntryEntity>>
    
    @Query("SELECT * FROM ledger_entries WHERE needsSync = 1")
    suspend fun getEntriesNeedingSync(): List<LedgerEntryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LedgerEntryEntity)
    
    @Update
    suspend fun updateEntry(entry: LedgerEntryEntity)
}
```

3. **Database**:
```kotlin
@Database(
    entities = [LedgerEntryEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun ledgerEntryDao(): LedgerEntryDao
    abstract fun categoryDao(): CategoryDao
}
```

4. **Repository'de Offline-First Logic**:
```kotlin
@Singleton
class LedgerRepository @Inject constructor(
    private val apiService: LedgerApiService,
    private val database: ExpenseTrackerDatabase,
    private val networkConnectivity: NetworkConnectivity
) {
    
    fun getEntries(familyId: String): Flow<List<LedgerEntry>> = 
        database.ledgerEntryDao().getEntries(familyId)
            .map { entities -> entities.map { it.toDomainModel() } }
    
    suspend fun createEntry(request: CreateLedgerEntryRequest): Result<LedgerEntry> {
        return try {
            if (networkConnectivity.isConnected()) {
                // Online: API'ye gönder
                val response = apiService.createEntry(request)
                if (response.isSuccessful) {
                    val entry = response.body()!!
                    // Local'a kaydet
                    database.ledgerEntryDao().insertEntry(entry.toEntity())
                    Result.success(entry)
                } else {
                    // API hatası, offline kaydet
                    saveOfflineEntry(request)
                }
            } else {
                // Offline: Local'a kaydet
                saveOfflineEntry(request)
            }
        } catch (e: Exception) {
            saveOfflineEntry(request)
        }
    }
    
    private suspend fun saveOfflineEntry(request: CreateLedgerEntryRequest): Result<LedgerEntry> {
        val entry = request.toEntity().copy(
            id = UUID.randomUUID().toString(),
            isOnline = false,
            needsSync = true
        )
        database.ledgerEntryDao().insertEntry(entry)
        return Result.success(entry.toDomainModel())
    }
    
    suspend fun syncPendingEntries() {
        val pendingEntries = database.ledgerEntryDao().getEntriesNeedingSync()
        
        pendingEntries.forEach { entry ->
            try {
                val request = entry.toCreateRequest()
                val response = apiService.createEntry(request)
                
                if (response.isSuccessful) {
                    val onlineEntry = response.body()!!
                    database.ledgerEntryDao().updateEntry(
                        entry.copy(
                            id = onlineEntry.id,
                            isOnline = true,
                            needsSync = false
                        )
                    )
                }
            } catch (e: Exception) {
                // Sync hatası, tekrar denenecek
            }
        }
    }
}
```

**Tahmini Süre**: 8-10 saat

---

## 🍎 Prompt 6: iOS API Integration Tamamlama

### Görev
iOS uygulamasında eksik API endpoint'lerinin implementasyonu.

### Implementasyon Adımları

1. **Complete APIService**:
```swift
extension APIService {
    
    func getBudgets() async throws -> [Budget] {
        return try await performRequest(
            endpoint: "/budgets",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func createBudget(_ request: CreateBudgetRequest) async throws -> Budget {
        return try await performRequest(
            endpoint: "/budgets",
            method: "POST",
            body: request,
            requiresAuth: true
        )
    }
    
    func getRecurringRules() async throws -> [RecurringRule] {
        return try await performRequest(
            endpoint: "/recurring",
            method: "GET",
            requiresAuth: true
        )
    }
    
    func generateReport(startDate: Date, endDate: Date) async throws -> ReportSummary {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        
        let queryItems = [
            URLQueryItem(name: "startDate", value: formatter.string(from: startDate)),
            URLQueryItem(name: "endDate", value: formatter.string(from: endDate))
        ]
        
        return try await performRequest(
            endpoint: "/reports/summary",
            method: "GET",
            queryItems: queryItems,
            requiresAuth: true
        )
    }
}
```

2. **Repository Pattern**:
```swift
protocol BudgetRepositoryProtocol {
    func getBudgets() async throws -> [Budget]
    func createBudget(_ budget: CreateBudgetRequest) async throws -> Budget
}

class BudgetRepository: BudgetRepositoryProtocol {
    private let apiService = APIService()
    
    func getBudgets() async throws -> [Budget] {
        return try await apiService.getBudgets()
    }
    
    func createBudget(_ budget: CreateBudgetRequest) async throws -> Budget {
        return try await apiService.createBudget(budget)
    }
}
```

3. **ViewModels**:
```swift
@MainActor
class BudgetViewModel: ObservableObject {
    @Published var budgets: [Budget] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    private let repository: BudgetRepositoryProtocol
    
    init(repository: BudgetRepositoryProtocol = BudgetRepository()) {
        self.repository = repository
    }
    
    func loadBudgets() async {
        isLoading = true
        errorMessage = nil
        
        do {
            budgets = try await repository.getBudgets()
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }
}
```

**Tahmini Süre**: 10-12 saat

---

## 🔄 Prompt 7: CI/CD Pipeline Kurulumu

### Görev
GitHub Actions ile otomatik test, build ve deployment pipeline'ı.

### Implementasyon Adımları

1. **Backend Pipeline** (`.github/workflows/backend.yml`):
```yaml
name: Backend CI/CD

on:
  push:
    branches: [ main, develop ]
    paths: [ 'backend/**' ]
  pull_request:
    branches: [ main ]
    paths: [ 'backend/**' ]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      mongodb:
        image: mongo:7
        ports:
          - 27017:27017
      redis:
        image: redis:7
        ports:
          - 6379:6379
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run tests
      run: |
        cd backend
        mvn clean test
    
    - name: Build JAR
      run: |
        cd backend
        mvn clean package -DskipTests
    
    - name: Build Docker image
      if: github.ref == 'refs/heads/main'
      run: |
        cd backend
        docker build -t expense-tracker-backend:${{ github.sha }} .
    
    - name: Security scan
      uses: securecodewarrior/github-action-add-sarif@v1
      with:
        sarif-file: 'security-scan-results.sarif'
```

2. **Android Pipeline** (`.github/workflows/android.yml`):
```yaml
name: Android CI

on:
  push:
    branches: [ main, develop ]
    paths: [ 'android/**' ]
  pull_request:
    branches: [ main ]
    paths: [ 'android/**' ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Gradle dependencies
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
    
    - name: Grant execute permission for gradlew
      run: chmod +x android/gradlew
    
    - name: Run unit tests
      run: |
        cd android
        ./gradlew testDebugUnitTest
    
    - name: Run lint
      run: |
        cd android
        ./gradlew lintDebug
    
    - name: Build APK
      run: |
        cd android
        ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: debug-apk
        path: android/app/build/outputs/apk/debug/*.apk
```

3. **Deployment Pipeline**:
```yaml
name: Deploy to Staging

on:
  push:
    branches: [ develop ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: staging
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Deploy to staging
      run: |
        echo "Deploying to staging environment"
        # Staging deployment scripts
```

**Tahmini Süre**: 10-12 saat

---

## 📊 Özet ve Öncelik Sırası

### Kritik Öncelik (Hemen Yapılmalı)
1. **Token Blacklisting** - 3-4 saat - Güvenlik açığı
2. **Aile Geçiş Sistemi** - 5-6 saat - Temel fonksiyonalite

### Yüksek Öncelik (1-2 Hafta)
3. **Email Bildirim Sistemi** - 8-10 saat - Kullanıcı deneyimi
4. **Dosya Yükleme** - 6-8 saat - Önemli özellik
5. **Android Offline Storage** - 8-10 saat - Kullanıcı deneyimi

### Orta Öncelik (2-4 Hafta)
6. **iOS API Integration** - 10-12 saat - Platform completeness
7. **CI/CD Pipeline** - 10-12 saat - DevOps maturity

**Toplam Tahmini Süre**: 50-62 saat

**Her prompt bağımsız implementasyona uygun şekilde tasarlanmıştır. Mevcut kod yapısını takip ederek geliştirilmelidir.**