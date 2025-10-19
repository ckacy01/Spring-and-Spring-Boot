## 🏗️ Architecture

### Layered Architecture Pattern

```
┌─────────────────────────────────────┐
│        REST Controllers             │  (API Layer)
├─────────────────────────────────────┤
│         Services                    │  (Business Logic)
├─────────────────────────────────────┤
│       Repositories                  │  (Data Access)
├─────────────────────────────────────┤
│        Entities / DTOs              │  (Domain Model)
├─────────────────────────────────────┤
│       H2/PostgreSQL Database        │  (Persistence)
└─────────────────────────────────────┘
```

### Key Design Patterns

**DTO Pattern**: Separates API contracts from internal domain models
**Repository Pattern**: Abstracts database access logic
**Service Layer**: Centralizes business logic and transactions
**Exception Handling**: Global exception handler for consistent error responses
**Soft Delete**: Logical deletion maintains referential integrity

### Technology Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.3.0+ |
| Language | Java 17+ |
| ORM | Hibernate |
| Build Tool | Maven |
| Logging | Lombok SLF4J |
| DB (Dev) | H2 |
| DB (Prod) | PostgreSQL |
| Testing | JUnit 5, Postman |
| Documentation | Swagger, JavaDoc |

## 🔒 Security Considerations

- **Soft Deletes**: Data is never permanently removed, enabling audit trails
- **Environment Variables**: Sensitive configuration externalized for production
- **Input Validation**: Request validation at controller layer
- **Exception Handling**: Prevents information leakage in error responses
- **CORS Configuration**: Secure cross-origin request handling
