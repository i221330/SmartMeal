#!/bin/bash

# SmartMeal Backend Diagnosis - Fix 500 Error

echo "🔍 SmartMeal Backend Diagnosis"
echo "==============================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Test 1: Check if Apache is accessible
echo "📋 Test 1: Checking if backend is accessible..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://192.168.1.4/smartmeal/backend/api/users.php 2>/dev/null)
if [ "$RESPONSE" = "200" ]; then
    echo -e "${GREEN}✅ Backend is accessible (HTTP 200)${NC}"
    curl -s http://192.168.1.4/smartmeal/backend/api/users.php | python3 -m json.tool 2>/dev/null || echo ""
elif [ "$RESPONSE" = "500" ]; then
    echo -e "${RED}❌ Backend returns 500 error (PHP/Database issue)${NC}"
    echo "This means the file exists but PHP code has an error"
elif [ "$RESPONSE" = "404" ]; then
    echo -e "${RED}❌ Backend not found (404)${NC}"
    echo "Backend files not copied to XAMPP htdocs"
else
    echo -e "${RED}❌ Cannot reach backend (HTTP $RESPONSE)${NC}"
fi
echo ""

# Test 2: Check MySQL connection from PHP
echo "📋 Test 2: Creating test script to check MySQL connection..."
TEST_PHP='<?php
error_reporting(E_ALL);
ini_set("display_errors", 1);
header("Content-Type: application/json");

try {
    $db = new PDO("mysql:host=localhost;dbname=smartmeal_db", "root", "");
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Check if users table exists
    $stmt = $db->query("SHOW TABLES LIKE \"users\"");
    $tableExists = $stmt->rowCount() > 0;

    // Check if password_hash column exists
    $hasPasswordHash = false;
    if ($tableExists) {
        $stmt = $db->query("DESCRIBE users");
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
        foreach ($columns as $col) {
            if ($col["Field"] === "password_hash") {
                $hasPasswordHash = true;
                break;
            }
        }
    }

    echo json_encode([
        "database_connected" => true,
        "users_table_exists" => $tableExists,
        "password_hash_exists" => $hasPasswordHash,
        "message" => "Database connection successful"
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "database_connected" => false,
        "error" => $e->getMessage()
    ]);
}
?>'

# Try to write test file (might need sudo)
echo "$TEST_PHP" > /tmp/test_db.php 2>/dev/null

echo "Test PHP file created at: /tmp/test_db.php"
echo "To test database connection, copy this file to XAMPP htdocs:"
echo ""
echo "sudo cp /tmp/test_db.php /Applications/XAMPP/htdocs/smartmeal/"
echo "Then access: http://192.168.1.4/smartmeal/test_db.php"
echo ""

# Test 3: Check if MySQL is running
echo "📋 Test 3: Checking if MySQL process is running..."
if ps aux | grep -v grep | grep mysql > /dev/null; then
    echo -e "${GREEN}✅ MySQL process is running${NC}"
else
    echo -e "${RED}❌ MySQL is NOT running${NC}"
    echo "Solution: Open XAMPP Control Panel and start MySQL"
fi
echo ""

# Test 4: Try to access phpMyAdmin
echo "📋 Test 4: Checking phpMyAdmin..."
PMA_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/phpmyadmin 2>/dev/null)
if [ "$PMA_RESPONSE" = "200" ] || [ "$PMA_RESPONSE" = "302" ]; then
    echo -e "${GREEN}✅ phpMyAdmin is accessible${NC}"
    echo "Open: http://localhost/phpmyadmin"
else
    echo -e "${YELLOW}⚠️  phpMyAdmin might not be accessible (HTTP $PMA_RESPONSE)${NC}"
fi
echo ""

# Summary
echo "==============================="
echo "📊 DIAGNOSIS SUMMARY"
echo "==============================="
echo ""
echo "Most likely issues:"
echo ""
echo "1. Database 'smartmeal_db' doesn't exist"
echo "   → Solution: Create in phpMyAdmin"
echo ""
echo "2. Table 'users' doesn't exist"
echo "   → Solution: Import schema.sql"
echo ""
echo "3. Column 'password_hash' missing"
echo "   → Solution: ALTER TABLE users ADD COLUMN password_hash VARCHAR(255);"
echo ""
echo "4. Backend files not in htdocs"
echo "   → Solution: Copy backend files to XAMPP"
echo ""
echo "📝 NEXT STEPS:"
echo ""
echo "1. Open phpMyAdmin: http://localhost/phpmyadmin"
echo "2. Check if 'smartmeal_db' database exists"
echo "3. If not, create it and import:"
echo "   /Users/mac/StudioProjects/SmartMeal/backend/database/schema.sql"
echo "4. Test again: http://192.168.1.4/smartmeal/backend/api/users.php"
echo ""

