#!/bin/bash

# Test script for name update functionality
# This tests the PHP backend endpoint for updating user display name

echo "================================"
echo "Testing Name Update Endpoint"
echo "================================"
echo ""

# Configuration
BASE_URL="http://192.168.1.4/smartmeal/backend/api"
USER_ID="user_675449d5c82a33.82969809"  # Replace with your test user ID

echo "📍 Testing update endpoint: ${BASE_URL}/users.php?action=profile"
echo "👤 User ID: ${USER_ID}"
echo ""

# Test 1: Update display name
echo "Test 1: Updating display name to 'Test User Updated'"
echo "----------------------------------------------"

RESPONSE=$(curl -s -X PUT "${BASE_URL}/users.php?action=profile" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "'${USER_ID}'",
    "display_name": "Test User Updated"
  }')

echo "Response:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
echo ""

# Test 2: Verify the update by fetching user profile
echo "Test 2: Fetching user profile to verify update"
echo "----------------------------------------------"

RESPONSE=$(curl -s -X GET "${BASE_URL}/users.php?action=profile&user_id=${USER_ID}")

echo "Response:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
echo ""

# Test 3: Update back to original name
echo "Test 3: Updating display name back to 'Test User'"
echo "----------------------------------------------"

RESPONSE=$(curl -s -X PUT "${BASE_URL}/users.php?action=profile" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "'${USER_ID}'",
    "display_name": "Test User"
  }')

echo "Response:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
echo ""

echo "================================"
echo "Tests Complete!"
echo "================================"
echo ""
echo "💡 To use this script with your user:"
echo "   1. Log in to the app"
echo "   2. Check the logs for your user_id"
echo "   3. Update USER_ID variable in this script"
echo "   4. Run: chmod +x test_name_update.sh && ./test_name_update.sh"

