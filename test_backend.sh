#!/bin/bash

# SmartMeal Backend Test Script
# Tests all critical components for signup flow

echo "🎯 SmartMeal Backend Test Script"
echo "=================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Check if Apache is running
echo "📋 Test 1: Checking Apache..."
if ps aux | grep -v grep | grep httpd > /dev/null; then
    echo -e "${GREEN}✅ Apache is running${NC}"
else
    echo -e "${RED}❌ Apache is NOT running${NC}"
    echo "   → Open XAMPP and start Apache"
fi
echo ""

# Test 2: Check if MySQL is running
echo "📋 Test 2: Checking MySQL..."
if ps aux | grep -v grep | grep mysql > /dev/null; then
    echo -e "${GREEN}✅ MySQL is running${NC}"
else
    echo -e "${RED}❌ MySQL is NOT running${NC}"
    echo "   → Open XAMPP and start MySQL"
fi
echo ""

# Test 3: Find htdocs directory
echo "📋 Test 3: Finding XAMPP htdocs..."
HTDOCS=$(find /Applications -name "htdocs" -type d 2>/dev/null | head -1)
if [ -z "$HTDOCS" ]; then
    echo -e "${YELLOW}⚠️  Could not find htdocs directory${NC}"
    echo "   → Please locate your XAMPP htdocs manually"
    HTDOCS="/Applications/XAMPP/htdocs"
    echo "   → Using default: $HTDOCS"
else
    echo -e "${GREEN}✅ Found htdocs: $HTDOCS${NC}"
fi
echo ""

# Test 4: Check if backend files exist
echo "📋 Test 4: Checking backend files..."
if [ -f "$HTDOCS/smartmeal/backend/api/users.php" ]; then
    echo -e "${GREEN}✅ Backend files found${NC}"
else
    echo -e "${RED}❌ Backend files NOT found${NC}"
    echo "   → Run this command to copy files:"
    echo "   mkdir -p $HTDOCS/smartmeal/backend"
    echo "   cp -r /Users/mac/StudioProjects/SmartMeal/backend/* $HTDOCS/smartmeal/backend/"
fi
echo ""

# Test 5: Test backend API endpoint (port 80)
echo "📋 Test 5: Testing backend API (port 80)..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/smartmeal/backend/api/users.php 2>/dev/null)
if [ "$RESPONSE" = "200" ]; then
    echo -e "${GREEN}✅ Backend API accessible at http://localhost/smartmeal/backend/api/users.php${NC}"
    curl -s http://localhost/smartmeal/backend/api/users.php | python3 -m json.tool 2>/dev/null || echo ""
elif [ "$RESPONSE" = "404" ]; then
    echo -e "${RED}❌ Backend API returns 404${NC}"
    echo "   → Check if files are in correct location"
else
    echo -e "${RED}❌ Cannot reach backend API (HTTP $RESPONSE)${NC}"
    echo "   → Check if Apache is running and files are copied"
fi
echo ""

# Test 6: Test backend API endpoint (port 8080)
echo "📋 Test 6: Testing backend API (port 8080)..."
RESPONSE8080=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/smartmeal/backend/api/users.php 2>/dev/null)
if [ "$RESPONSE8080" = "200" ]; then
    echo -e "${GREEN}✅ Backend API also accessible at http://localhost:8080/smartmeal/backend/api/users.php${NC}"
else
    echo -e "${YELLOW}⚠️  Port 8080 not accessible (this is OK if port 80 works)${NC}"
fi
echo ""

# Test 7: Check ADB devices
echo "📋 Test 7: Checking Android devices..."
if command -v adb &> /dev/null; then
    DEVICES=$(adb devices | grep -v "List of devices" | grep "device" | wc -l)
    if [ $DEVICES -gt 0 ]; then
        echo -e "${GREEN}✅ Android device/emulator connected${NC}"
        adb devices
    else
        echo -e "${YELLOW}⚠️  No Android device/emulator connected${NC}"
        echo "   → Start your emulator or connect a device"
    fi
else
    echo -e "${YELLOW}⚠️  ADB not found in PATH${NC}"
fi
echo ""

# Test 8: Check ADB reverse
echo "📋 Test 8: Checking ADB reverse..."
if command -v adb &> /dev/null && [ $DEVICES -gt 0 ]; then
    ADB_REVERSE=$(adb reverse --list 2>/dev/null | grep "tcp:8080" | wc -l)
    if [ $ADB_REVERSE -gt 0 ]; then
        echo -e "${GREEN}✅ ADB reverse is set up${NC}"
        adb reverse --list
    else
        echo -e "${YELLOW}⚠️  ADB reverse NOT set up${NC}"
        echo "   → Run this command:"
        echo "   adb reverse tcp:8080 tcp:80"
    fi
else
    echo -e "${YELLOW}⚠️  Cannot check ADB reverse (no device connected)${NC}"
fi
echo ""

# Test 9: Check MySQL database
echo "📋 Test 9: Checking MySQL database..."
if command -v mysql &> /dev/null || [ -f "/Applications/XAMPP/bin/mysql" ]; then
    MYSQL_CMD="mysql"
    if [ -f "/Applications/XAMPP/bin/mysql" ]; then
        MYSQL_CMD="/Applications/XAMPP/bin/mysql"
    fi

    DB_EXISTS=$($MYSQL_CMD -u root -e "SHOW DATABASES LIKE 'smartmeal_db';" 2>/dev/null | grep smartmeal_db | wc -l)
    if [ $DB_EXISTS -gt 0 ]; then
        echo -e "${GREEN}✅ Database 'smartmeal_db' exists${NC}"

        # Check if password_hash column exists
        PASS_COL=$($MYSQL_CMD -u root smartmeal_db -e "DESCRIBE users;" 2>/dev/null | grep password_hash | wc -l)
        if [ $PASS_COL -gt 0 ]; then
            echo -e "${GREEN}✅ Users table has 'password_hash' column${NC}"
        else
            echo -e "${RED}❌ Users table missing 'password_hash' column${NC}"
            echo "   → Run this SQL in phpMyAdmin:"
            echo "   ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) AFTER email;"
        fi
    else
        echo -e "${RED}❌ Database 'smartmeal_db' does NOT exist${NC}"
        echo "   → Create database in phpMyAdmin: http://localhost/phpmyadmin"
        echo "   → Import schema from: /Users/mac/StudioProjects/SmartMeal/backend/database/schema.sql"
    fi
else
    echo -e "${YELLOW}⚠️  MySQL command not available${NC}"
    echo "   → Check database manually at: http://localhost/phpmyadmin"
fi
echo ""

# Summary
echo "=================================="
echo "📊 SUMMARY"
echo "=================================="
echo ""
echo "If all tests passed, you're ready to:"
echo "1. Run: adb reverse tcp:8080 tcp:80"
echo "2. Build app: ./gradlew clean assembleDebug"
echo "3. Install: ./gradlew installDebug"
echo "4. Test signup flow in the app"
echo ""
echo "If any tests failed, follow the suggestions above."
echo ""
echo "🔗 Quick Links:"
echo "   - phpMyAdmin: http://localhost/phpmyadmin"
echo "   - Backend API: http://localhost/smartmeal/backend/api/users.php"
echo "   - Setup Guide: /Users/mac/StudioProjects/SmartMeal/COMPLETE_SETUP_GUIDE.md"
echo ""

