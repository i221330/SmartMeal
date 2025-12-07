#!/bin/bash

# SmartMeal - Update API URL for Physical Phone
# This script helps you update the app to work without adb

echo "🔧 SmartMeal - Physical Phone Setup (No ADB Required)"
echo "======================================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Step 1: Get Mac's IP
echo "📍 Step 1: Finding your Mac's IP address..."
echo ""

# Try multiple methods to get IP
IP1=$(ipconfig getifaddr en0 2>/dev/null)
IP2=$(ipconfig getifaddr en1 2>/dev/null)
IP3=$(ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | head -1)

MAC_IP=""
if [ ! -z "$IP1" ]; then
    MAC_IP="$IP1"
elif [ ! -z "$IP2" ]; then
    MAC_IP="$IP2"
elif [ ! -z "$IP3" ]; then
    MAC_IP="$IP3"
fi

if [ ! -z "$MAC_IP" ]; then
    echo -e "${GREEN}✅ Auto-detected IP: $MAC_IP${NC}"
    echo ""
    echo "Is this correct? (y/n)"
    read -r confirm
    if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        MAC_IP=""
    fi
fi

if [ -z "$MAC_IP" ]; then
    echo -e "${YELLOW}⚠️  Could not auto-detect IP${NC}"
    echo ""
    echo "Please find your Mac's IP manually:"
    echo "  Apple menu → System Preferences → Network"
    echo "  (Look for something like 192.168.1.5)"
    echo ""
    echo "Enter your Mac's IP address:"
    read -r MAC_IP
fi

if [ -z "$MAC_IP" ]; then
    echo -e "${RED}❌ No IP address provided${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}Using IP: $MAC_IP${NC}"
echo ""

# Step 2: Update ApiClient.kt
echo "📍 Step 2: Updating ApiClient.kt..."
echo ""

API_CLIENT_FILE="/Users/mac/StudioProjects/SmartMeal/app/src/main/java/com/example/smartmeal/network/ApiClient.kt"

if [ ! -f "$API_CLIENT_FILE" ]; then
    echo -e "${RED}❌ Could not find ApiClient.kt${NC}"
    echo "Please update manually:"
    echo "  File: app/src/main/java/com/example/smartmeal/network/ApiClient.kt"
    echo "  Change: http://localhost:8080/smartmeal/backend/api/"
    echo "  To: http://$MAC_IP/smartmeal/backend/api/"
    exit 1
fi

# Backup original
cp "$API_CLIENT_FILE" "$API_CLIENT_FILE.backup"
echo -e "${GREEN}✅ Created backup: ApiClient.kt.backup${NC}"

# Update the URL
sed -i '' "s|http://localhost:8080/smartmeal/backend/api/|http://$MAC_IP/smartmeal/backend/api/|g" "$API_CLIENT_FILE"

echo -e "${GREEN}✅ Updated BASE_URL to: http://$MAC_IP/smartmeal/backend/api/${NC}"
echo ""

# Step 3: Instructions
echo "======================================================"
echo "✨ UPDATE COMPLETE!"
echo "======================================================"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo ""
echo "1. Test from your phone's browser first:"
echo "   http://$MAC_IP/smartmeal/backend/api/users.php"
echo ""
echo "2. Make sure:"
echo "   - Phone and Mac on same Wi-Fi"
echo "   - XAMPP Apache is running"
echo "   - Can access URL above in phone browser"
echo ""
echo "3. Rebuild and install the app:"
echo "   cd /Users/mac/StudioProjects/SmartMeal"
echo "   ./gradlew clean assembleDebug installDebug"
echo ""
echo "4. Test the signup flow!"
echo ""
echo -e "${GREEN}Your app will now work WITHOUT needing adb!${NC}"
echo ""
echo "If you need to revert:"
echo "  cp $API_CLIENT_FILE.backup $API_CLIENT_FILE"
echo ""

