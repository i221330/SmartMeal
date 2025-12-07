 #!/bin/bash

# Physical Phone Connection Tester for SmartMeal
# This script helps diagnose connection issues with physical Android phones

echo "📱 SmartMeal Physical Phone Connection Tester"
echo "=============================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test 1: Check if phone is connected
echo "📋 Test 1: Checking if Android device is connected..."
echo ""

DEVICES=$(adb devices 2>/dev/null | grep -v "List of devices" | grep "device" | wc -l)
if [ $DEVICES -gt 0 ]; then
    echo -e "${GREEN}✅ Device connected${NC}"
    adb devices
else
    echo -e "${RED}❌ No device connected${NC}"
    echo ""
    echo "Please:"
    echo "1. Connect your phone via USB"
    echo "2. Enable USB Debugging on your phone:"
    echo "   Settings → About Phone → Tap 'Build Number' 7 times"
    echo "   Settings → Developer Options → Enable USB Debugging"
    echo "3. Allow USB debugging when prompted on phone"
    echo ""
    exit 1
fi
echo ""

# Test 2: Try ADB reverse
echo "📋 Test 2: Testing ADB reverse..."
echo ""

adb reverse tcp:8080 tcp:80 2>/dev/null
REVERSE_RESULT=$?

if [ $REVERSE_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ ADB reverse works on your phone!${NC}"
    adb reverse --list
    echo ""
    echo -e "${BLUE}Good news! You can use localhost:8080 in your app.${NC}"
    ADB_REVERSE_WORKS=true
else
    echo -e "${YELLOW}⚠️  ADB reverse doesn't work on your phone${NC}"
    echo ""
    echo -e "${BLUE}Don't worry! You'll need to use your Mac's IP address instead.${NC}"
    ADB_REVERSE_WORKS=false
fi
echo ""

# Test 3: Get Mac's IP address
echo "📋 Test 3: Finding your Mac's IP address..."
echo ""

MAC_IP=$(ipconfig getifaddr en0 2>/dev/null)
if [ -z "$MAC_IP" ]; then
    MAC_IP=$(ipconfig getifaddr en1 2>/dev/null)
fi

if [ -z "$MAC_IP" ]; then
    echo -e "${YELLOW}⚠️  Could not auto-detect IP address${NC}"
    echo ""
    echo "Please find it manually:"
    echo "System Preferences → Network → Select your connection → Note the IP"
    echo ""
else
    echo -e "${GREEN}✅ Your Mac's IP address: $MAC_IP${NC}"
fi
echo ""

# Test 4: Check if Apache is accessible from Mac
echo "📋 Test 4: Testing Apache on your Mac..."
echo ""

APACHE_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/smartmeal/backend/api/users.php 2>/dev/null)
if [ "$APACHE_TEST" = "200" ]; then
    echo -e "${GREEN}✅ Apache is running and accessible locally${NC}"
    echo "URL: http://localhost/smartmeal/backend/api/users.php"
else
    echo -e "${RED}❌ Apache is not accessible${NC}"
    echo "Expected: HTTP 200, Got: HTTP $APACHE_TEST"
    echo ""
    echo "Please:"
    echo "1. Start XAMPP Apache"
    echo "2. Copy backend files to htdocs/smartmeal/backend/"
    echo "3. See COMPLETE_SETUP_GUIDE.md Step 3"
    echo ""
    exit 1
fi
echo ""

# Test 5: Check if Mac's firewall might block connections
echo "📋 Test 5: Checking firewall status..."
echo ""

FIREWALL_STATUS=$(defaults read /Library/Preferences/com.apple.alf globalstate 2>/dev/null)
if [ "$FIREWALL_STATUS" = "0" ]; then
    echo -e "${GREEN}✅ Firewall is OFF - phone should be able to connect${NC}"
elif [ "$FIREWALL_STATUS" = "1" ] || [ "$FIREWALL_STATUS" = "2" ]; then
    echo -e "${YELLOW}⚠️  Firewall is ON - may block phone connections${NC}"
    echo ""
    echo "You may need to:"
    echo "1. System Preferences → Security & Privacy → Firewall"
    echo "2. Add Apache to allowed apps"
    echo "OR temporarily disable firewall for testing"
else
    echo -e "${YELLOW}⚠️  Could not determine firewall status${NC}"
fi
echo ""

# Summary and Recommendations
echo "=============================================="
echo "📊 SUMMARY & RECOMMENDATIONS"
echo "=============================================="
echo ""

if [ "$ADB_REVERSE_WORKS" = true ]; then
    echo -e "${GREEN}✅ Your phone supports ADB reverse!${NC}"
    echo ""
    echo "You can use the default configuration:"
    echo "  BASE_URL = \"http://localhost:8080/smartmeal/backend/api/\""
    echo ""
    echo "Just run this before testing:"
    echo "  adb reverse tcp:8080 tcp:80"
    echo ""
else
    echo -e "${YELLOW}⚠️  Your phone does NOT support ADB reverse${NC}"
    echo ""
    echo "You need to update ApiClient.kt to use your Mac's IP address."
    echo ""
    if [ ! -z "$MAC_IP" ]; then
        echo -e "${BLUE}Change this line in ApiClient.kt:${NC}"
        echo ""
        echo "  FROM:"
        echo "  const val BASE_URL = \"http://localhost:8080/smartmeal/backend/api/\""
        echo ""
        echo "  TO:"
        echo "  const val BASE_URL = \"http://$MAC_IP/smartmeal/backend/api/\""
        echo ""
        echo "Then rebuild: ./gradlew clean assembleDebug installDebug"
        echo ""
        echo -e "${BLUE}Test from your phone's browser first:${NC}"
        echo "  http://$MAC_IP/smartmeal/backend/api/users.php"
        echo ""
    else
        echo "Find your Mac's IP and update ApiClient.kt accordingly."
        echo "See PHYSICAL_PHONE_SETUP.md for detailed instructions."
        echo ""
    fi
fi

echo "=============================================="
echo ""
echo "📖 For more details, see:"
echo "  - PHYSICAL_PHONE_SETUP.md (comprehensive guide)"
echo "  - COMPLETE_SETUP_GUIDE.md (full setup)"
echo ""
st