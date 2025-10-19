# Scripts Documentation

## Overview

This document provides comprehensive documentation for the main deployment and setup scripts included in the MeliECommerce project. These scripts automate the installation, building, and execution of the application in different environments.

## Quick Start

```bash
# Make scripts executable
chmod +x scripts/*.sh

# Option 1: Install and run immediately (Development mode)
./scripts/install_and_run.sh

# Option 2: Run development mode
./scripts/run_development.sh

# Option 3: Run production mode
./scripts/run_production.sh
```

## System Requirements

- **Operating System**: Linux, macOS, or Windows (Git Bash)
- **Java**: 17 or higher
- **Maven**: Will be installed automatically if not present
- **Git**: For cloning the repository

## Available Scripts

### 1. install_and_run.sh

**Purpose**: Complete installation and execution in one command

**What it does**:
1. Checks for Maven installation (installs if missing)
2. Verifies Java is installed
3. Navigates to project directory
4. Cleans previous builds
5. Installs dependencies
6. Builds the project
7. Starts the application in DEVELOPMENT mode

**Usage**:
```bash
./scripts/install_and_run.sh
```

**Output**:
- Displays installation progress
- Shows access URLs
- Starts Spring Boot development server

**When to use**:
- Fresh installation of the project
- First-time setup
- Quick development start

**Time required**: 5-10 minutes (depending on internet speed)

**Sample Output**:
```
==========================================================
         MeliECommerce - Installation & Run Script
                    Development Mode (H2)
==========================================================

Requirements verification completed

Project directory: /home/user/meliecommerce

Cleaning previous project...
Installing dependencies...
Dependencies installed successfully

Building project...
Project built successfully

Starting application in DEVELOPMENT mode (H2)...

---------------------------------------------------
Access the application at:
  API: http://localhost:8080
---------------------------------------------------
```

---

### 2. run_development.sh

**Purpose**: Start the application in DEVELOPMENT mode

**What it does**:
1. Checks for Maven installation (installs if missing)
2. Verifies project is built (builds if necessary)
3. Starts Spring Boot with development profile

**Configuration**:
- Database: H2 (in-memory, embedded)
- Port: 8080
- Profile: dev
- H2 Console: Enabled
- DDL Mode: create-drop (fresh schema on each restart)
- Hot Reload: Enabled (requires IDE support)

**Usage**:
```bash
./scripts/run_development.sh
```

**Access Points**:
- Application API: `http://localhost:8080`

**When to use**:
- Local development
- Testing new features
- Debugging issues
- Daily development work

**Sample Output**:
```
==========================================================
    MeliECommerce - Development Mode (H2 Database)
==========================================================

Project directory: /home/user/meliecommerce

Starting application in DEVELOPMENT mode

Configuration:
  Database: H2 (in-memory)
  Port: 8080
  Profile: dev

---------------------------------------------------
Access the application at:
  API: http://localhost:8080
---------------------------------------------------
```

**Development Tips**:
- Data is stored in-memory and lost on restart
- Use for rapid development and testing
- H2 Console helps visualize database state
- No external dependencies required

---

### 3. run_production.sh

**Purpose**: Start the application in PRODUCTION mode

**What it does**:
1. Checks for Maven installation (installs if missing)
2. Verifies project is built (builds if necessary)
3. Verifies PostgreSQL configuration
4. Requests user confirmation
5. Starts Spring Boot with production profile

**Configuration**:
- Database: PostgreSQL
- Port: 8080
- Profile: prod
- H2 Console: Disabled
- DDL Mode: validate (no automatic schema changes)
- Optimizations: Production-ready settings

**Prerequisites**:
Before running this script, ensure:

1. PostgreSQL is installed and running:
   ```bash
   sudo systemctl start postgresql
   ```

2. Database and user are created:
   ```bash
   sudo -u postgres psql
   
   CREATE DATABASE ecommerce;
   CREATE USER melie_user WITH PASSWORD 'your_secure_password';
   GRANT ALL PRIVILEGES ON DATABASE ecommerce TO melie_user;
   \q
   ```

3. Configuration file is updated (`src/main/resources/application-prod.yaml`):
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/melie_commerce
       username: melie_user
       password: your_secure_password
       driver-class-name: org.postgresql.Driver
     jpa:
       hibernate:
         ddl-auto: validate
       database-platform: org.hibernate.dialect.PostgreSQLDialect
   ```

**Usage**:
```bash
./scripts/run_production.sh
```

**Process**:
1. Script displays configuration requirements
2. Prompts for verification (press Enter to continue)
3. If verification fails, script exits
4. Application starts if all checks pass

**Access Points**:
- Application API: `http://localhost:8080`
- PostgreSQL: `localhost:5432/melie_commerce`

**When to use**:
- Production deployments
- Performance testing
- Staging environment testing
- Preparing for live release

**Sample Output**:
```
==========================================================
   MeliECommerce - Production Mode (PostgreSQL Database)
==========================================================

VERIFICATION REQUIRED

Make sure PostgreSQL is running and the configuration
in 'src/main/resources/application-prod.yaml' is correct:

  spring:
    datasource:
      url: jdbc:postgresql://localhost:5432/melie_commerce
      username: your_user
      password: your_password

Press Enter to continue...

Starting application in PRODUCTION mode

Configuration:
  Database: PostgreSQL
  Port: 8080
  Profile: prod

---------------------------------------------------
Access the application at:
  API: http://localhost:8080
---------------------------------------------------
```

**Production Considerations**:
- Data is persistent in PostgreSQL
- Requires database setup beforehand
- Configuration must match PostgreSQL server
- No schema modifications on startup (validate mode)
- Best for stable, production environments

---

## Maven Installation

All scripts automatically detect and install Maven if it's not present on your system.

### Manual Installation (if needed)

**On Ubuntu/Debian/Linux Mint**:
```bash
sudo apt-get update
sudo apt-get install -y maven
```

**On macOS**:
```bash
brew install maven
```

**Verify installation**:
```bash
mvn --version
```

---

## Environment Profiles

The application uses Spring Boot profiles to manage different configurations:

| Profile | Database | Purpose | DDL Mode |
|---------|----------|---------|----------|
| dev | H2 | Development | create-drop |
| prod | PostgreSQL | Production | validate |

**Configuration files location**: `src/main/resources/`
- `application.yaml` - Default configuration
- `application-dev.yaml` - Development profile
- `application-prod.yaml` - Production profile

---

## Troubleshooting

### Maven installation fails

**Error**: "Error installing Maven"

**Solution**:
1. Update package manager:
   ```bash
   sudo apt-get update
   ```

2. Install manually:
   ```bash
   sudo apt-get install -y maven
   ```

3. Verify installation:
   ```bash
   mvn --version
   ```

### Port 8080 already in use

**Error**: "Address already in use: 8080"

**Solution**:
1. Find process using port 8080:
   ```bash
   lsof -i :8080
   ```

2. Kill the process:
   ```bash
   kill -9 <PID>
   ```

3. Or change port in `application.yaml`:
   ```yaml
   server:
     port: 8081
   ```

### PostgreSQL connection fails

**Error**: "FATAL: role does not exist"

**Solution**:
1. Check PostgreSQL is running:
   ```bash
   sudo systemctl status postgresql
   ```

2. Verify user exists:
   ```bash
   sudo -u postgres psql -l
   ```

3. Check configuration in `application-prod.yaml` matches your setup

### Permission denied when running script

**Error**: "Permission denied"

**Solution**:
Make scripts executable:
```bash
chmod +x scripts/*.sh
```

---

## Building JAR for Deployment

```bash
mvn clean package -DskipTests
java -jar target/meliecommerce-1.0.0.jar --spring.profiles.active=prod
```

---

## Related Documentation

For more information, see:
- [README.md]() - Project overview and features
- [DATABASE.md](DATABASE.md) - Database schema and design
- [API.md](API.md) - API endpoints documentation