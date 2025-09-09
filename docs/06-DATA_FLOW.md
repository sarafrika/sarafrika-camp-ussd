# Data Flow Diagrams

## Overview

This document provides comprehensive data flow diagrams showing how information moves through the Camp Sarafrika USSD system, from user interaction to data persistence and external service integration.

## High-Level System Data Flow

```mermaid
graph TB
    subgraph "User Layer"
        User[Mobile User]
        Phone[Mobile Phone]
    end
    
    subgraph "Network Layer"
        Telco[Mobile Network<br/>Safaricom/Airtel]
        AT[Africa's Talking<br/>USSD Gateway]
    end
    
    subgraph "Application Layer"
        Controller[USSD Controller]
        MenuService[Menu Service]
        CampService[Camp Service]
        LocationService[Location Service]
        RegistrationService[Registration Service]
        SmsService[SMS Service]
        TrackingService[Tracking Service]
    end
    
    subgraph "Data Layer"
        Redis[(Redis<br/>Session Cache)]
        PostgreSQL[(PostgreSQL<br/>Database)]
    end
    
    subgraph "External Services"
        SmsGateway[SMS Gateway<br/>Africa's Talking]
        Payment[M-Pesa<br/>Payment Gateway]
    end
    
    User -->|Dials *123#| Phone
    Phone -->|USSD Request| Telco
    Telco -->|HTTP POST| AT
    AT -->|Webhook| Controller
    
    Controller -->|Session Management| Redis
    Controller -->|Process Input| MenuService
    
    MenuService -->|Get Camps| CampService
    MenuService -->|Get Locations| LocationService
    MenuService -->|Create Registration| RegistrationService
    MenuService -->|Send SMS| SmsService
    MenuService -->|Track Interaction| TrackingService
    
    CampService -->|Query| PostgreSQL
    LocationService -->|Query| PostgreSQL
    RegistrationService -->|Persist| PostgreSQL
    TrackingService -->|Log| PostgreSQL
    
    SmsService -->|Send Message| SmsGateway
    RegistrationService -->|Process Payment| Payment
    
    Controller -->|USSD Response| AT
    AT -->|Display| Telco
    Telco -->|Show Menu| Phone
    Phone -->|Visual Display| User
    
    SmsGateway -->|SMS| Phone
```

## USSD Request-Response Flow

```mermaid
sequenceDiagram
    participant U as User
    participant T as Telco
    participant AT as Africa's Talking
    participant C as USSD Controller
    participant R as Redis
    participant M as Menu Service
    participant D as Database
    
    U->>T: Dial *123#
    T->>AT: USSD Session Start
    AT->>C: POST /ussd (sessionId, phoneNumber, text="")
    
    C->>R: Get Session
    R-->>C: Session Not Found
    C->>M: Process Input (new session)
    M->>M: Create Welcome Menu
    M-->>C: CON Welcome to Camp Sarafrika!...
    
    C->>R: Save Session
    C-->>AT: USSD Response
    AT-->>T: Display Menu
    T-->>U: Show Options
    
    U->>T: Press 1
    T->>AT: User Input: 1
    AT->>C: POST /ussd (sessionId, phoneNumber, text="1")
    
    C->>R: Get Session
    R-->>C: Session Data
    C->>M: Process Input (state: main_menu, input: "1")
    M->>D: Get Categories
    D-->>M: Category List
    M-->>C: CON Select a camp category...
    
    C->>R: Update Session
    C-->>AT: USSD Response
    AT-->>T: Display Categories
    T-->>U: Show Categories
```

## Registration Data Flow

```mermaid
graph TD
    subgraph "Registration Process"
        A[User Input Collection] --> B[Data Validation]
        B --> C[Session Storage]
        C --> D[Registration Creation]
        D --> E[SMS Notification]
        E --> F[Payment Integration]
        F --> G[Confirmation]
    end
    
    subgraph "Data Validation Steps"
        B1[Name Length Check]
        B2[Age Range Validation]
        B3[Phone Number Format]
        B4[Camp Availability]
        B5[Location Fee Lookup]
    end
    
    subgraph "Database Operations"
        D1[Insert Registration]
        D2[Link to Camp]
        D3[Generate Reference Code]
        D4[Update Status]
    end
    
    subgraph "External Integrations"
        E1[Participant SMS]
        E2[Guardian SMS]
        F1[M-Pesa STK Push]
        F2[Payment Verification]
    end
    
    B --> B1
    B --> B2
    B --> B3
    B --> B4
    B --> B5
    
    D --> D1
    D --> D2
    D --> D3
    D --> D4
    
    E --> E1
    E --> E2
    
    F --> F1
    F --> F2
```

## Session Management Flow

```mermaid
graph LR
    subgraph "Session Lifecycle"
        A[Session Create] --> B[State Transitions]
        B --> C[Data Accumulation]
        C --> D[Session Cleanup]
    end
    
    subgraph "Redis Operations"
        R1[SET session:id]
        R2[GET session:id]
        R3[UPDATE session:id]
        R4[EXPIRE session:id]
    end
    
    subgraph "Session Data Structure"
        S1[Session ID]
        S2[Phone Number]
        S3[Current State]
        S4[State History]
        S5[User Data]
        S6[Menu Context]
    end
    
    A --> R1
    B --> R2
    B --> R3
    C --> R3
    D --> R4
    
    R1 --> S1
    R1 --> S2
    R3 --> S3
    R3 --> S4
    R3 --> S5
    R3 --> S6
```

## Database Query Flow

```mermaid
graph TB
    subgraph "Query Layer"
        Q1[Category Query]
        Q2[Camp Query]
        Q3[Location Query]
        Q4[Activity Query]
        Q5[Registration Query]
    end
    
    subgraph "Service Layer"
        CS[Camp Service]
        LS[Location Service]
        AS[Activity Service]
        RS[Registration Service]
    end
    
    subgraph "Database Tables"
        T1[(camps)]
        T2[(locations)]
        T3[(activities)]
        T4[(registrations)]
        T5[(camp_locations)]
    end
    
    CS --> Q1
    CS --> Q2
    LS --> Q3
    AS --> Q4
    RS --> Q5
    
    Q1 --> T1
    Q2 --> T1
    Q2 --> T5
    Q2 --> T2
    Q3 --> T2
    Q4 --> T3
    Q5 --> T4
    
    T5 --> T1
    T5 --> T2
```

## SMS Notification Flow

```mermaid
sequenceDiagram
    participant R as Registration Service
    participant S as SMS Service
    participant AT as Africa's Talking
    participant P as Participant
    participant G as Guardian
    participant DB as Database
    
    R->>S: Send Registration Confirmation
    S->>S: Format Participant Message
    S->>AT: Send SMS (Participant)
    S->>DB: Log SMS Notification
    AT-->>P: SMS Delivery
    
    alt Guardian has different number
        S->>S: Format Guardian Message
        S->>AT: Send SMS (Guardian)
        S->>DB: Log SMS Notification
        AT-->>G: SMS Delivery
    end
    
    AT->>S: Delivery Status Webhook
    S->>DB: Update Delivery Status
```

## Payment Integration Flow

```mermaid
graph TD
    A[Registration Confirmed] --> B[Generate Payment Request]
    B --> C[M-Pesa STK Push]
    C --> D{Payment Response}
    
    D -->|Success| E[Update Registration Status]
    D -->|Failed| F[Payment Failed]
    D -->|Timeout| G[Payment Timeout]
    
    E --> H[Send Payment Confirmation SMS]
    F --> I[Send Payment Failure SMS]
    G --> J[Send Timeout Notice SMS]
    
    H --> K[Registration Complete]
    I --> L[Registration Pending]
    J --> L
```

## Error Handling Flow

```mermaid
graph TD
    A[User Input] --> B{Input Validation}
    B -->|Valid| C[Process Request]
    B -->|Invalid| D[Format Error Message]
    
    C --> E{System Processing}
    E -->|Success| F[Generate Response]
    E -->|Error| G[Log Error]
    
    G --> H{Error Type}
    H -->|Recoverable| I[Retry Logic]
    H -->|Fatal| J[Graceful Degradation]
    
    I --> C
    J --> K[Error Response]
    D --> L[Validation Error Response]
    F --> M[Success Response]
    K --> M
    L --> M
    
    M --> N[Send to User]
```

## Analytics and Tracking Flow

```mermaid
graph LR
    subgraph "Data Collection Points"
        A[USSD Interaction]
        B[Registration Event]
        C[SMS Event]
        D[Payment Event]
        E[Error Event]
    end
    
    subgraph "Tracking Service"
        T[Tracking Service]
    end
    
    subgraph "Storage"
        S1[(User Interactions)]
        S2[(Performance Metrics)]
        S3[(Error Logs)]
        S4[(Business Metrics)]
    end
    
    A --> T
    B --> T
    C --> T
    D --> T
    E --> T
    
    T --> S1
    T --> S2
    T --> S3
    T --> S4
```

## Cache Strategy Flow

```mermaid
graph TD
    A[Request Comes In] --> B{Check Redis Cache}
    B -->|Hit| C[Return Cached Data]
    B -->|Miss| D[Query Database]
    
    D --> E[Process Results]
    E --> F[Cache Results in Redis]
    F --> G[Return Data]
    
    C --> H[Update Last Access]
    G --> I[Set TTL]
    
    subgraph "Cache Keys"
        K1[session:{sessionId}]
        K2[camps:{category}]
        K3[locations:all]
        K4[activities:{campId}]
    end
    
    subgraph "TTL Settings"
        T1[Sessions: 5 minutes]
        T2[Static Data: 1 hour]
        T3[Dynamic Data: 15 minutes]
    end
```

## Backup and Recovery Flow

```mermaid
graph TD
    subgraph "Backup Strategy"
        A[Continuous WAL Backup]
        B[Daily Full Backup]
        C[Hourly Incremental]
    end
    
    subgraph "Recovery Scenarios"
        D[Point-in-Time Recovery]
        E[Complete System Restore]
        F[Partial Data Recovery]
    end
    
    subgraph "Monitoring"
        G[Health Checks]
        H[Alert System]
        I[Performance Metrics]
    end
    
    A --> D
    B --> E
    C --> F
    
    G --> H
    H --> I
```

## Monitoring and Observability Flow

```mermaid
graph TB
    subgraph "Application Metrics"
        M1[Response Times]
        M2[Error Rates]
        M3[Session Counts]
        M4[Registration Rates]
    end
    
    subgraph "Infrastructure Metrics"
        I1[CPU Usage]
        I2[Memory Usage]
        I3[Disk I/O]
        I4[Network Traffic]
    end
    
    subgraph "Business Metrics"
        B1[Daily Registrations]
        B2[Popular Camps]
        B3[User Journey Analytics]
        B4[Revenue Tracking]
    end
    
    subgraph "Alerting"
        A1[Threshold Alerts]
        A2[Anomaly Detection]
        A3[Health Check Failures]
    end
    
    M1 --> A1
    M2 --> A1
    I1 --> A1
    I2 --> A1
    
    M3 --> A2
    B1 --> A2
    
    all --> A3
```

---

**Next**: [API Reference](./05-API_REFERENCE.md)