#!/bin/bash

# Test Profile Picture Backend Integration
echo "═══════════════════════════════════════════════════════"
echo "Testing Profile Picture Backend Integration"
echo "═══════════════════════════════════════════════════════"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost/smartmeal/api"
TEST_USER_ID="test_user_123"
TEST_EMAIL="test@smartmeal.com"
TEST_PROFILE_URL="/data/data/com.example.smartmeal/files/profile_test_user_123.jpg"

echo "Configuration:"
echo "  Base URL: $BASE_URL"
echo "  Test User ID: $TEST_USER_ID"
echo ""

# Test 1: Check if backend is accessible
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Test 1: Backend Accessibility"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/users.php")
if [ "$response" == "200" ]; then
    echo -e "${GREEN}✓${NC} Backend is accessible (HTTP $response)"
else
    echo -e "${RED}✗${NC} Backend is NOT accessible (HTTP $response)"
    echo "Please make sure XAMPP is running and check the URL"
    exit 1
fi
echo ""

# Test 2: Get API info
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Test 2: API Info"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
response=$(curl -s "$BASE_URL/users.php")
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

# Test 3: Test profile update endpoint
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Test 3: Profile Update Endpoint"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Sending PUT request with profile_image_url..."
response=$(curl -s -X PUT "$BASE_URL/users.php?action=profile" \
    -H "Content-Type: application/json" \
    -d "{
        \"user_id\": \"$TEST_USER_ID\",
        \"profile_image_url\": \"$TEST_PROFILE_URL\"
    }")
echo "Response:"
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"

# Check if it was successful
if echo "$response" | grep -q "successfully"; then
    echo -e "${GREEN}✓${NC} Profile update endpoint works!"
else
    echo -e "${YELLOW}⚠${NC} Profile update may have issues. Check response above."
fi
echo ""

# Test 4: Check database structure (if MySQL is accessible)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Test 4: Database Structure"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Try to connect to MySQL (assuming no password for root)
if command -v mysql &> /dev/null; then
    # Check if we can connect
    if mysql -u root -e "SELECT 1" &> /dev/null; then
        echo "Checking users table structure..."
        mysql -u root smartmeal_db -e "DESCRIBE users;" 2>/dev/null | grep -i profile

        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓${NC} profile_image_url column exists in users table"
        else
            echo -e "${RED}✗${NC} profile_image_url column NOT found!"
            echo "You may need to add it:"
            echo "  ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(255) DEFAULT NULL;"
        fi
        echo ""

        # Show sample data
        echo "Sample user data (first 3 rows):"
        mysql -u root smartmeal_db -e "SELECT firebase_uid, email, display_name, profile_image_url FROM users LIMIT 3;" 2>/dev/null
    else
        echo -e "${YELLOW}⚠${NC} Cannot connect to MySQL (may require password)"
    fi
else
    echo -e "${YELLOW}⚠${NC} MySQL client not found. Skipping database check."
fi
echo ""

# Test 5: Test with actual registered user (if available)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Test 5: Real User Test (Optional)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "If you have a registered test user, update these variables:"
echo "  TEST_USER_EMAIL='your@email.com'"
echo "  TEST_USER_PASSWORD='yourpassword'"
echo ""
echo "Then you can test the complete flow:"
echo "  1. Login"
echo "  2. Update profile with image URL"
echo "  3. Verify the update persists"
echo ""

# Summary
echo "═══════════════════════════════════════════════════════"
echo "Test Summary"
echo "═══════════════════════════════════════════════════════"
echo "The profile picture backend should now:"
echo "  ✓ Accept PUT requests to update profile_image_url"
echo "  ✓ Store the file path in the database"
echo "  ✓ Return the updated profile_image_url on login"
echo ""
echo "Next Steps:"
echo "  1. Build and run the Android app"
echo "  2. Monitor Logcat for profile sync logs"
echo "  3. Test: Set profile picture → Logout → Login"
echo "  4. Profile picture should persist!"
echo ""
echo "For detailed debugging, see: PROFILE_PICTURE_DEBUG.md"
echo "═══════════════════════════════════════════════════════"

