# Camp Sarafrika USSD Service - Project Plan

## Project Overview
**Version:** 1.3  
**Date:** September 8, 2025  
**Status:** In Development

The Camp Sarafrika USSD Service is a robust, high-performance application that allows users to register and pay for various camps through USSD interface. Built with Quarkus, containerized with Docker, and integrates with Africa's Talking for USSD functionality.

## Architecture & Technology Stack

### Core Technologies
- **Backend Framework:** Quarkus (Java 21)
- **Build Tool:** Gradle
- **Database:** PostgreSQL with Flyway migrations
- **Session Management:** Redis
- **Containerization:** Docker & Docker Compose
- **API Integration:** Africa's Talking (USSD, SMS, Payments)
- **Documentation:** OpenAPI/Swagger

### Key Features
- Stateless webhook architecture
- Redis-based session management with 5-minute TTL
- Pagination support (3 items per page)
- Soft delete pattern with audit fields
- Reference code generation (CS-XXXXXXXX format)
- Conditional logic for minors (guardian phone required)

## Implementation Status

### ✅ COMPLETED PHASES

#### Phase 1: Foundation & Core Setup
- [x] **Project Structure**
  - Quarkus project initialized with Gradle
  - Java 21 configured
  - Package structure: `apps.sarafrika`
  
- [x] **Dependencies Configuration**
  - Hibernate ORM Panache for database operations
  - PostgreSQL JDBC driver
  - Flyway for database migrations
  - Redis client for session management
  - REST client for external API calls
  - OpenAPI for documentation
  - YAML configuration support

#### Phase 2: Database Layer
- [x] **Base Entity Pattern**
  - Abstract `BaseEntity` with audit fields:
    - `id` (Primary Key), `uuid`, `created_date`, `created_by`
    - `updated_date`, `updated_by`, `is_deleted`, `deleted_by`, `deleted_date`
  - Soft delete functionality implemented

- [x] **Database Schema**
  - Flyway migration scripts with timestamp versioning (`V202509081541__`)
  - `camps` table with JSONB activities field
  - `registrations` table with business constraints
  - Proper indexing for performance
  - Sample data insertion

#### Phase 3: Entity Models & Services
- [x] **Entity Classes**
  - `Camp` entity with category-based queries
  - `Registration` entity with business logic methods
  - Panache repository pattern implementation

- [x] **Service Layer**
  - `CampService` for camp operations
  - `RegistrationService` with reference code generation
  - Transactional support

#### Phase 4: Session Management
- [x] **Redis Integration**
  - `SessionService` with JSON serialization
  - `UserSession` DTO with snake_case JSON properties
  - State management with history stack
  - Pagination support for dynamic menus

#### Phase 5: USSD Implementation
- [x] **Controller Layer**
  - `UssdController` handling Africa's Talking webhooks
  - Form parameter processing
  - Error handling and logging

- [x] **Menu System**
  - `UssdMenuService` with complete flow implementation
  - Main menu (no back option as requested)
  - Category selection with dynamic loading
  - Camp selection with pagination (3 per page + "More >>")
  - Complete registration flow:
    - Full name input
    - Age input
    - Conditional guardian phone (minors only)
    - Participant phone input
    - Registration confirmation
  - My Bookings functionality
  - Help section
  - Proper back navigation throughout

#### Phase 6: Business Logic
- [x] **Registration Flow**
  - Data validation at each step
  - Kenyan phone number validation
  - Age validation (5-25 years)
  - Guardian phone requirement for minors
  - Registration summary display
  - Reference code generation and storage

### 🚧 IN PROGRESS

#### Phase 7: External Integrations
- [x] Basic project structure for Africa's Talking
- [ ] SMS API integration for confirmations
- [ ] Payment API integration (M-Pesa STK Push)
- [ ] Payment callback handling

### 📋 PENDING PHASES

#### Phase 8: Containerization & DevOps
- [ ] **Docker Configuration**
  - Application Dockerfile with native compilation
  - docker-compose.yml for development environment
  - PostgreSQL and Redis services
  - Environment variable configuration

#### Phase 9: Testing & Quality Assurance
- [ ] **Unit Testing**
  - Service layer tests
  - Entity tests
  - Session management tests
  
- [ ] **Integration Testing**
  - USSD flow end-to-end tests
  - Database integration tests with TestContainers
  - Redis session tests
  
- [ ] **Load Testing**
  - Concurrent session handling (minimum 100 users)
  - Performance benchmarks (sub-2-second response time)

#### Phase 10: Production Readiness
- [ ] **Monitoring & Observability**
  - Health checks implementation
  - Metrics collection
  - Logging standardization
  
- [ ] **Security Hardening**
  - Input sanitization review
  - Credential management audit
  - Security headers configuration

## Configuration

### Application Configuration (`application.yml`)
```yaml
quarkus:
  datasource:
    db-kind: postgresql
    # Environment-based configuration
  redis:
    hosts: redis://localhost:6379
  
africas-talking:
  api-key: ${AFRICAS_TALKING_API_KEY}
  username: ${AFRICAS_TALKING_USERNAME}
  
ussd:
  service-code: ${USSD_SERVICE_CODE:*123#}
  session-timeout: 300 # 5 minutes
```

## USSD Flow Architecture

### State Management
- **Session Storage:** Redis with 5-minute TTL
- **State History:** Stack-based navigation
- **Back Navigation:** Supported at all levels except main menu
- **Data Persistence:** Key-value storage in session

### Menu Structure
```
Main Menu (*123#)
├── 1. Register for a Camp
│   ├── Select Category (Dynamic from DB)
│   ├── Select Camp (Paginated, 3 per page)
│   ├── Enter Full Name
│   ├── Enter Age
│   ├── Enter Guardian Phone (if age < 18)
│   ├── Enter Participant Phone
│   └── Confirm Registration
├── 2. My Bookings
├── 3. Help
└── 4. Exit
```

## Database Design

### Core Tables
- **camps**: Camp information with JSONB activities
- **registrations**: User registrations with audit trail

### Key Constraints
- Unique reference codes
- Guardian phone required for minors
- Proper foreign key relationships

## Performance Requirements
- **Response Time:** < 2 seconds for USSD responses
- **Concurrency:** Minimum 100 concurrent sessions
- **Availability:** 99.9% uptime target
- **Session Timeout:** 5 minutes with automatic cleanup

## Security Considerations
- Input validation at all levels
- SQL injection prevention via Panache
- Environment-based credential management
- Audit trail for all operations

## Next Steps
1. Complete Africa's Talking payment integration
2. Implement SMS confirmation system
3. Create Docker configuration
4. Set up comprehensive testing
5. Performance optimization and load testing
6. Production deployment preparation

---
**Last Updated:** September 8, 2025  
**Project Lead:** Development Team  
**Status:** 70% Complete