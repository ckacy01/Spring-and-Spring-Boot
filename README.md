# MeliECommerce

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Status](https://img.shields.io/badge/Status-Active%20Development-blue.svg)](https://github.com/yourusername/meliecommerce)

A robust e-commerce REST API built with Spring Boot, featuring multi-environment support, comprehensive documentation, and production-ready architecture.

##  Overview: The Challenge and Solution

### The Problem

**MELI**, a leading e-commerce company, faced critical technical failures that severely impacted its order management system. The situation included:

- **Misconfigured production environment**: Satellites in production were improperly configured, causing operational disruptions
- **Database failures**: A database node became unresponsive, leading to system unavailability
- **System instability**: These issues resulted in significant financial losses and widespread customer complaints
- **Urgent need**: The company required an immediate and effective solution to restore operations

### The Solution

A talented recent graduate, was brought in to address these challenges. His approach focused on:

1. **System Restructuring**: Complete reconfiguration of the order management system with proper architecture
2. **Multi-Environment Setup**: Implementation of Spring profiles to manage development, testing, and production environments separately
3. **Database Resilience**: Proper database configuration and connection management across different environments
4. **API Documentation**: Integration of Swagger for comprehensive service documentation and visual testing
5. **Code Quality**: Implementation of JavaDoc for complete code documentation
6. **Soft Delete Logic**: Design of data preservation mechanisms to maintain referential integrity and audit trails

### Results Achieved

- Resolved all technical issues affecting order management
- Implemented robust, production-ready architecture
- Established comprehensive documentation (API, Database, Code)
- Created preventive mechanisms to avoid future failures
- Significantly improved system reliability and customer experience
- Laid foundation for scalable, maintainable e-commerce platform

This project represents the successful transformation of a problematic system into a solid, well-documented, and maintainable e-commerce platform.

---

## Table of Contents

- [Features](#features)
- [Release Notes](#release-notes)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Usage](#usage)
- [Documentation](#documentation)
- [Testing](#testing)
- [License](#license)

## Features

### Core Functionality
- **User Management**: Full CRUD operations for user profiles
- **Product Catalog**: Comprehensive product management system
- **Order Processing**: Complete order lifecycle management with order details tracking
- **Soft Delete Logic**: Implements logical deletion to maintain referential integrity
- **Product Snapshots**: Automatically captures product information at order time to preserve historical data

### Technical Features
- **Multi-Environment Support**: Seamless switching between development, testing, and production environments
- **Database Flexibility**: H2 for development/testing, PostgreSQL for production
- **API Documentation**: Integrated Swagger UI for interactive API exploration
- **Code Documentation**: Complete JavaDoc coverage for all classes
- **Comprehensive Testing**: JUnit 5 and Postman test suites
- **Layered Architecture**: Clean separation of concerns with controller, service, repository, and entity layers
- **Exception Handling**: Centralized global exception handling with custom exceptions
- **ORM Support**: Hibernate-based object-relational mapping
- **Logging**: Lombok-powered efficient logging throughout the application

## Release Notes

### Version 1.1.0 - Environment Profiles (October 21, 2025)
> Actual version
#### New Features
- Environment profiles added: Introduced separate profiles for development, testing (using H2), and production (using PostgreSQL) to improve configuration management and deployment flexibility.
- Environment variable management: Added support for .env files to securely manage and isolate environment-specific variables.

#### Technical Stack
- Spring Boot 3.5.2+
- Java 17+
- Hibernate ORM
- Lombok
- H2 Database (dev/test)
- PostgreSQL (production)
- JUnit 5
- Swagger/Springdoc

## Requirements

### System Requirements
- **Java**: 17 or higher
- **Maven**: 3.6.0 or higher
- **Git**: Any recent version

### Runtime Requirements
- **Development/Testing**: H2 Database (embedded)
- **Production**: PostgreSQL 12+

### Optional Tools
- **Postman**: For API endpoint testing
- **Git Bash**: For running deployment scripts (Windows users)
- **Docker**: For containerized deployment (optional)

## Installation

### Option 1: Automated Installation & Run

```bash
cd scripts
./install_and_run.sh
```

This script will:
- Clone/setup the repository
- Install dependencies
- Build the project
- Run the application in development mode (H2 database)

### Option 2: Installation Only

```bash
cd scripts
./only_install.sh
```

This prepares the project without starting the application.

### Option 3: Manual Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/meliecommerce.git
   cd meliecommerce
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Build the project**
   ```bash
   mvn clean package
   ```

## Quick Start

### Development Environment (H2 Database)

```bash
cd scripts
./run_development.sh
```

Or manually:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

The application will start at `http://localhost:8080`


### Production Environment (PostgreSQL)

```bash
cd scripts
./run_production.sh
```

Before running, ensure PostgreSQL is configured in `application-prod.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/melie_commerce
    username: your_postgres_user
    password: your_postgres_password
```


This runs the application with the testing profile using H2 database.

## Project Structure

```
meliecommerce/
├── docs/
│   ├── swagger/                 # API documentation exports
│   ├── javadoc/                 # Generated JavaDoc
│   ├── postman/                 # Postman collection for testing
│   └── more-info/               # Additional documentation
│
├── scripts/                      # Deployment and setup scripts
│   ├── install_and_run.sh       # Complete setup and run
│   ├── run_development.sh       # Run with dev profile
│   └── run_production.sh        # Run with prod profile
│
├── src/main/
│   ├── java/org/technoready/meliecommerce/
│   │   ├── config/              # Spring configuration classes
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── entity/              # JPA entities
│   │   ├── exception/           # Custom exceptions and handlers
│   │   ├── repository/          # Spring Data repositories
│   │   ├── service/             # Business logic services
│   │   ├── util/                # Utility classes
│   │   └── MeliEcommerceApplication.java
│   │
│   └── resources/
│       ├── application.yaml             # Default configuration
│       ├── application-dev.yaml         # Development profile
│       ├── application-prod.yaml        # Production profile
│       └── application-test.yaml        # Testing profile
│
├── src/test/
│   └── java/org/technoready/meliecommerce/
│       └── MeliEcommerceApplicationTests.java
│
├── pom.xml                      # Maven configuration
├── README.md                    # This file
└── .gitignore
```

## Configuration

### Environment Profiles

The application supports four Spring profiles:

#### Default (`application.yaml`)
Base configuration shared across all environments.

#### Development (`application-dev.yaml`)
```yaml
spring:
  profiles:
    active: dev
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
```
- **Database**: H2 (in-memory)
- **DDL**: Create-drop (resets on restart)
- **H2 Console**: Enabled at `http://localhost:8080/h2-console`

#### Testing (`application-test.yaml`)
```yaml
spring:
  profiles:
    active: test
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
```
- **Database**: H2 (in-memory)
- **DDL**: Create-drop
- **Purpose**: Automated testing with clean state

#### Production (`application-prod.yaml`)
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/melie_commerce
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
```
- **Database**: PostgreSQL
- **DDL**: Validate (no automatic schema changes)
- **Credentials**: Use environment variables for security

## Usage

### Example API Requests

#### Create User
```bash
curl -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jorge",
    "lastName": "Avila",
    "email": "jorge@example.com"
  }'
```

#### Get All Products
```bash
curl http://localhost:8080/api/products?activeOnly=true
```

#### Create Order
```bash
curl -X POST http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '[
    {"productId": 1, "quantity": 2},
    {"productId": 3, "quantity": 1}
  ]'
```

#### Update Order
```bash
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '[
    {"productId": 2, "quantity": 3}
  ]'
```

#### Soft Delete User
```bash
curl -X DELETE http://localhost:8080/api/user/1
```

For complete API documentation, see [API.md](Meli-E-commerce/docs/more-info/API.md)

## Documentation

This project includes comprehensive documentation:

### API Documentation
**File**: [API.md](Meli-E-commerce/docs/more-info/API.md)
- Detailed endpoint descriptions
- Request/response examples
- Query parameters explained
- Error codes and handling

### Database Documentation
**File**: [DATABASE.md](Meli-E-commerce/docs/more-info/DATABASE.md)
- Schema design explanation
- Table structures and relationships
- Soft delete logic implementation
- Product snapshot mechanism
- Entity diagrams

### Scripts Documentation
**File**: [SCRIPTS.md](Meli-E-commerce/docs/more-info/SCRIPTS.md)


### Code Documentation
**Location**: `docs/javadoc/`
- Complete JavaDoc for all classes
- Method signatures and parameters
- Exception documentation
- Usage examples where applicable

Generate or update JavaDoc:
```bash
mvn javadoc:javadoc
```

## Testing

### API Testing with Postman

1. Open Postman
2. Import the collection from `docs/postman/`
3. Use the development ( In the future will be the testing ) environment profile
4. Execute test scenarios

Test coverage areas:
- User CRUD operations
- Product management
- Order creation and updates
- Soft delete functionality
- Error handling scenarios
- Edge cases and validations

##  License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact & Support

- **Project Lead**: Jorge Armando Avila Carrillo
- **Issues**: [GitHub Issues](https://github.com/ckacy01/Spring-and-Spring-Boot/issues)
- **Discussions**: [GitHub Discussions](https://github.com/ckacy01/Spring-and-Spring-Boot/discussions)

## Roadmap

- [ ] Add JUNIT 5 testing
- [ ] Add Testing environment
- [ ] Integrate Swagger/OpenAPI into project
- [ ] Configure Swagger UI for interactive testing
- [ ] Develop tests for different scenarios ( success, edge cases. failure)

## Changelog

**v1.0.0** - Initial release with core features (October 19, 2025)

---

For more detailed information, please refer to:
- [Database Documentation](Meli-E-commerce/docs/more-info/DATABASE.md)
- [Architecture Documentation](Meli-E-commerce/docs/more-info/ARCHITECTURE.md)
- [API Endpoints](Meli-E-commerce/docs/more-info/API.md)
- [ Java Doc ](Meli-E-commerce/docs/javadoc)
- [Scripts Documentation & Ussage](Meli-E-commerce/docs/more-info/SCRIPTS.md)

