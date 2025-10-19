#!/bin/sh

echo "=========================================================="
echo "   MeliECommerce - Production Mode (PostgreSQL Database)"
echo "=========================================================="
echo ""

if ! command -v mvn >/dev/null 2>&1; then
    echo "Maven is not installed. Installing Maven..."

    sudo apt-get update
    sudo apt-get install -y maven

    if [ $? -ne 0 ]; then
        echo "Error installing Maven. Please install it manually."
        exit 1
    fi

    echo "Maven installed successfully"
    echo ""
fi

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR" || exit 1

echo "Project directory: $PROJECT_DIR"
echo ""

if [ ! -d "target" ]; then
    echo "Project is not built. Building now..."
    echo ""
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "Error during project build"
        exit 1
    fi
    echo ""
fi

echo "VERIFICATION REQUIRED"
echo ""
echo "Make sure PostgreSQL is running and the configuration"
echo "in 'src/main/resources/application-prod.yaml' is correct:"
echo ""
echo "  spring:"
echo "    datasource:"
echo "      url: jdbc:postgresql://localhost:5432/ecommerce"
echo "      username: postgres (default config)"
echo "      password: postgres (default config)"
echo ""
echo "Press Enter to continue..."
read -r dummy

echo ""
echo "Starting application in PRODUCTION mode"
echo ""
echo "Configuration:"
echo "  Database: PostgreSQL"
echo "  Port: 8080"
echo "  Profile: prod"
echo ""
echo "---------------------------------------------------"
echo "Access the application at:"
echo "  API: http://localhost:8080"
echo "---------------------------------------------------"
echo ""

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"