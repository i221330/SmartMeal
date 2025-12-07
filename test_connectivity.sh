#!/bin/bash

echo "🧪 TESTING BACKEND CONNECTIVITY"
echo "================================"
echo ""

echo "1️⃣ Testing from Mac (localhost):"
RESULT1=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "http://localhost/smartmeal/backend/api/users.php?action=register" -H "Content-Type: application/json" -d "{\"email\":\"test$(date +%s)@test.com\",\"password\":\"test123\",\"display_name\":\"Test\"}" 2>&1 | tail -1)
if echo "$RESULT1" | grep -q "HTTP_CODE:201"; then
    echo "✅ Localhost works"
else
    echo "❌ Localhost failed: $RESULT1"
fi
echo ""

echo "2️⃣ Testing from Mac (via IP 192.168.100.11):"
RESULT2=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "http://192.168.100.11/smartmeal/backend/api/users.php?action=register" -H "Content-Type: application/json" -d "{\"email\":\"test$(date +%s)@test.com\",\"password\":\"test123\",\"display_name\":\"Test\"}" 2>&1 | tail -1)
if echo "$RESULT2" | grep -q "HTTP_CODE:201"; then
    echo "✅ IP access works from Mac"
else
    echo "❌ IP access failed: $RESULT2"
fi
echo ""

echo "3️⃣ Checking Firewall Status:"
FIREWALL=$(sudo /usr/libexec/ApplicationFirewall/socketfilterfw --getglobalstate 2>&1)
echo "$FIREWALL"
echo ""

echo "4️⃣ Checking Apache Status:"
if pgrep -f httpd > /dev/null; then
    echo "✅ Apache is running"
else
    echo "❌ Apache is not running"
fi
echo ""

echo "5️⃣ Checking What Apache is Listening On:"
sudo lsof -iTCP:80 -sTCP:LISTEN 2>&1 | head -3
echo ""

echo "================================"
echo "📊 DIAGNOSIS:"
echo ""
if echo "$RESULT1" | grep -q "HTTP_CODE:201" && echo "$RESULT2" | grep -q "HTTP_CODE:201"; then
    echo "✅ Backend is working correctly!"
    echo "✅ Accessible via both localhost and IP"
    echo ""
    echo "⚠️ If Android still can't connect, the issue is:"
    echo "   1. Emulator network configuration"
    echo "   2. Emulator firewall/routing"
    echo "   3. Try restarting the emulator"
else
    echo "❌ Backend has connectivity issues"
    echo "   Check Apache configuration and firewall"
fi

