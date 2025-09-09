# Camp Sarafrika USSD Documentation

## Table of Contents

1. [System Overview](./01-SYSTEM_OVERVIEW.md)
2. [Database Schema & Relationships](./02-DATABASE_SCHEMA.md)
3. [Data Population Guide](./03-DATA_POPULATION.md)
4. [USSD Flow Documentation](./04-USSD_FLOW.md)
5. [API Reference](./05-API_REFERENCE.md)
6. [Data Flow Diagrams](./06-DATA_FLOW.md)
7. [Deployment Guide](./07-DEPLOYMENT.md)
8. [Testing Guide](./08-TESTING.md)

## Quick Start

Camp Sarafrika USSD is a youth camp registration system built on Quarkus framework, providing USSD-based mobile registration for Kenyan users.

### Key Features
- **USSD-based registration** for camps via mobile phones
- **Location-based pricing** with normalized database structure
- **SMS notifications** via Africa's Talking API
- **Session management** with Redis
- **Real-time tracking** and analytics
- **Multi-step registration** flow with validation

### Technology Stack
- **Backend**: Java 21, Quarkus Framework
- **Database**: PostgreSQL with Flyway migrations
- **Cache**: Redis for session management
- **SMS**: Africa's Talking SDK
- **Deployment**: Docker & Docker Compose

## Documentation Overview

This documentation provides comprehensive guidance on:

- **Understanding the system architecture** and data flows
- **Setting up and populating** the database with camps, locations, and activities
- **Testing the USSD flow** from dial to registration completion
- **Monitoring and maintaining** the system in production
- **Extending functionality** with new features

Each section builds upon the previous, providing both technical depth and practical examples for implementation.

---

**Next**: [System Overview](./01-SYSTEM_OVERVIEW.md)