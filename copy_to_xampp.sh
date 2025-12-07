#!/bin/bash

# Copy backend files to XAMPP htdocs

echo "🔧 Copying Backend Files to XAMPP"
echo "==================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Find XAMPP htdocs
HTDOCS=""
POSSIBLE_LOCATIONS=(
    "/Applications/XAMPP/htdocs"
    "/Applications/XAMPP/xamppfiles/htdocs"
    "/opt/lampp/htdocs"
    "~/Applications/XAMPP/htdocs"
)

echo "Looking for XAMPP htdocs..."
for location in "${POSSIBLE_LOCATIONS[@]}"; do
    if [ -d "$location" ]; then
        HTDOCS="$location"
        echo -e "${GREEN}✅ Found htdocs at: $HTDOCS${NC}"
        break
    fi
done

if [ -z "$HTDOCS" ]; then
    echo -e "${RED}❌ Could not find XAMPP htdocs directory${NC}"
    echo ""
    echo "Please find it manually and run:"
    echo "sudo cp -r /Users/mac/StudioProjects/SmartMeal/backend /path/to/htdocs/smartmeal/"
    exit 1
fi

echo ""
echo "Copying files..."

# Create smartmeal directory
sudo mkdir -p "$HTDOCS/smartmeal/backend" 2>/dev/null

# Copy backend files
sudo cp -r /Users/mac/StudioProjects/SmartMeal/backend/* "$HTDOCS/smartmeal/backend/"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Files copied successfully!${NC}"
    echo ""
    echo "Files are now at: $HTDOCS/smartmeal/backend/"
    echo ""
    echo "Test the API:"
    echo "http://192.168.1.4/smartmeal/backend/api/users.php"
else
    echo -e "${RED}❌ Failed to copy files${NC}"
    echo "You may need to run with sudo or adjust permissions"
fi

echo ""
echo "Verifying files..."
if [ -f "$HTDOCS/smartmeal/backend/api/users.php" ]; then
    echo -e "${GREEN}✅ users.php exists${NC}"
else
    echo -e "${RED}❌ users.php not found${NC}"
fi

if [ -f "$HTDOCS/smartmeal/backend/config/database.php" ]; then
    echo -e "${GREEN}✅ database.php exists${NC}"
else
    echo -e "${RED}❌ database.php not found${NC}"
fi

echo ""
echo "Done!"

