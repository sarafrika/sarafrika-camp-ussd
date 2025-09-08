# Camp Sarafrika USSD - Development Guide

## Getting Started

### Prerequisites
- Java 21 or higher
- Docker and Docker Compose
- PostgreSQL (for local development)
- Redis (for session management)

### Project Structure
```
sarafrika-camp-ussd/
├── src/
│   ├── main/
│   │   ├── java/apps/sarafrika/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # JPA entities
│   │   │   └── service/             # Business logic services
│   │   └── resources/
│   │       ├── application.yml      # Configuration
│   │       └── db/migration/        # Flyway SQL scripts
│   └── test/                        # Test files
├── docs/                            # Documentation
├── build.gradle                     # Build configuration
└── README.md
```

## Development Setup

### 1. Clone and Build
```bash
git clone <repository-url>
cd sarafrika-camp-ussd
./gradlew build
```

### 2. Database Setup
```bash
# Start PostgreSQL (if using Docker)
docker run --name postgres-dev -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=sarafrika_camp -p 5432:5432 -d postgres:15

# Start Redis
docker run --name redis-dev -p 6379:6379 -d redis:7-alpine
```

### 3. Environment Configuration
Create `.env` file:
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sarafrika_camp
DB_USERNAME=postgres
DB_PASSWORD=postgres

REDIS_HOST=localhost
REDIS_PORT=6379

AFRICAS_TALKING_API_KEY=your-sandbox-key
AFRICAS_TALKING_USERNAME=sandbox
AFRICAS_TALKING_ENVIRONMENT=sandbox

USSD_SERVICE_CODE=*123#
```

### 4. Run Application
```bash
./gradlew quarkusDev
```

Application will start on `http://localhost:8080`

## Development Workflow

### Database Migrations
All database changes must use Flyway migrations:

1. **Create Migration File**
   ```bash
   # Format: V{timestamp}__{description}.sql
   touch src/main/resources/db/migration/V202509081600__add_new_table.sql
   ```

2. **Write Migration**
   ```sql
   -- V202509081600__add_new_table.sql
   CREATE TABLE new_table (
       id BIGSERIAL PRIMARY KEY,
       uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       -- ... other base entity fields
       name VARCHAR(255) NOT NULL
   );
   ```

3. **Test Migration**
   ```bash
   ./gradlew quarkusDev
   # Migration runs automatically on startup
   ```

### Adding New Entities

1. **Extend BaseEntity**
   ```java
   @Entity
   @Table(name = "your_table")
   public class YourEntity extends BaseEntity {
       @Column(name = "name")
       public String name;
       
       // Add Panache finder methods
       public static PanacheQuery<YourEntity> findByName(String name) {
           return find("name = ?1 and isDeleted = false", name);
       }
   }
   ```

2. **Create Service**
   ```java
   @ApplicationScoped
   public class YourEntityService {
       @Transactional
       public YourEntity create(YourEntity entity) {
           entity.persist();
           return entity;
       }
   }
   ```

### USSD Menu Development

1. **Add New State**
   - Update `UssdMenuService.processUssdInput()` switch statement
   - Add handler method: `handleYourStateInput()`
   - Add display method: `showYourState()`

2. **State Handler Pattern**
   ```java
   private String handleYourStateInput(UserSession session, String input) {
       // Validate input
       if (!isValid(input)) {
           return "CON Invalid input. Please try again.\n\n" + showYourState();
       }
       
       // Save to session
       session.putData("yourData", input);
       
       // Navigate to next state
       session.pushState("next_state");
       return showNextState(session);
   }
   ```

3. **Menu Display Pattern**
   ```java
   private String showYourState() {
       return """
               CON Your Menu Title:
               
               1. Option One
               2. Option Two
               
               0. Back""";
   }
   ```

### Testing USSD Flows

1. **Manual Testing with curl**
   ```bash
   # Initial request
   curl -X POST http://localhost:8080/ussd \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "sessionId=test123&phoneNumber=%2B254712345678&networkCode=63902&serviceCode=*123#&text="
   
   # Follow-up request
   curl -X POST http://localhost:8080/ussd \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "sessionId=test123&phoneNumber=%2B254712345678&networkCode=63902&serviceCode=*123#&text=1"
   ```

2. **Session Debugging**
   ```bash
   # Check Redis session
   docker exec -it redis-dev redis-cli
   > KEYS ussd:session:*
   > GET ussd:session:test123
   ```

## Code Standards

### Entity Guidelines
- Always extend `BaseEntity`
- Use `@Column(name = "snake_case")` for all fields
- Implement Panache finder methods for common queries
- Add soft delete support via `isDeleted = false` in queries

### Service Guidelines
- Use `@ApplicationScoped` for services
- Add `@Transactional` for database operations
- Handle exceptions gracefully
- Log important operations

### DTO Guidelines
- Use `@JsonProperty("snake_case")` for JSON fields
- Provide no-args constructor
- Use public fields for simplicity
- Add validation where appropriate

### Logging
```java
private static final Logger LOG = Logger.getLogger(YourClass.class);

LOG.infof("Operation completed for user %s", phoneNumber);
LOG.errorf(exception, "Failed to process request for session %s", sessionId);
```

## Debugging

### Common Issues

1. **Database Connection**
   - Check PostgreSQL is running
   - Verify connection string in application.yml
   - Check migration logs

2. **Redis Session Issues**
   - Verify Redis is running and accessible
   - Check session key format: `ussd:session:{sessionId}`
   - Monitor session TTL (5 minutes)

3. **USSD Flow Issues**
   - Check session state history
   - Verify input validation
   - Review state transitions

### Development Tools

1. **Swagger UI**: `http://localhost:8080/swagger-ui`
2. **Health Check**: `http://localhost:8080/ussd/health`
3. **OpenAPI Spec**: `http://localhost:8080/openapi`

## Performance Considerations

### Database Optimization
- Use appropriate indexes (already configured)
- Implement pagination for large datasets
- Use database connection pooling (Quarkus default)

### Redis Optimization
- Monitor session sizes
- Implement session cleanup if needed
- Use connection pooling for high load

### Response Time Targets
- USSD responses: < 2 seconds
- Database queries: < 500ms
- Redis operations: < 100ms

## Deployment

### Local Development
```bash
./gradlew quarkusDev
```

### Production Build
```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar
```

### Native Build (GraalVM)
```bash
./gradlew buildNative
./build/sarafrika-camp-ussd-1.0.0-runner
```

---
**Last Updated:** September 8, 2025