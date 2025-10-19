#!/bin/sh

echo "=========================================================="
echo "    MeliECommerce - Development Mode (H2 Database)"
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

echo "Starting application in DEVELOPMENT mode"
echo ""
echo "Configuration:"
echo "  Database: H2 (in-memory)"
echo "  Port: 8080"
echo "  Profile: dev"
echo ""
echo "---------------------------------------------------"
echo "Access the application at:"
echo "  API: http://localhost:8080"
echo "---------------------------------------------------"
echo ""

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"