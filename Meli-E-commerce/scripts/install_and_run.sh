#!/bin/sh

# SCRIPT 1: install_and_run.sh
# Installs Maven if needed, installs dependencies and runs the application in development mode
# Only works on ubuntu environment or git bash (In the future will be added others scripts)

#!/bin/sh

echo "=========================================================="
echo "         MeliECommerce - Installation & Run Script"
echo "                    Development Mode (H2)"
echo "=========================================================="
echo ""

install_maven() {
    echo "Maven is not installed. Installing Maven..."
    
    sudo apt-get update
    sudo apt-get install -y maven
    
    if [ $? -ne 0 ]; then
        echo "Error installing Maven. Please install it manually."
        exit 1
    fi
    
    echo "Maven installed successfully"
}

if ! command -v mvn >/dev/null 2>&1; then
    install_maven
fi

if ! command -v java >/dev/null 2>&1; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

echo "Requirements verification completed"
echo ""

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR" || exit 1

echo "Project directory: $PROJECT_DIR"
echo ""

echo "Cleaning previous project..."
mvn clean >/dev/null 2>&1

echo "Installing dependencies..."
mvn install -DskipTests

if [ $? -ne 0 ]; then
    echo "Error during dependencies installation"
    exit 1
fi

echo "Dependencies installed successfully"
echo ""

echo "Building project..."
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo "Error during project build"
    exit 1
fi

echo "Project built successfully"
echo ""

echo "Starting application in DEVELOPMENT mode (H2)..."
echo ""
echo "---------------------------------------------------"
echo "Access the application at:"
echo "  API: http://localhost:8080"
echo "---------------------------------------------------"
echo ""

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
