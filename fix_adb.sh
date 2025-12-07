#!/bin/bash

# Fix ADB Command Not Found - SmartMeal Project Setup
# This script will help you set up ADB in your PATH

echo "🔧 ADB Setup Script for SmartMeal"
echo "=================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Step 1: Find Android SDK
echo "📍 Step 1: Locating Android SDK..."
echo ""

SDK_LOCATIONS=(
    "/Users/mac/Library/Android/sdk"
    "$HOME/Library/Android/sdk"
    "$ANDROID_HOME"
    "$ANDROID_SDK_ROOT"
)

SDK_PATH=""
for location in "${SDK_LOCATIONS[@]}"; do
    if [ -d "$location" ]; then
        echo -e "${GREEN}✅ Found SDK at: $location${NC}"
        SDK_PATH="$location"
        break
    fi
done

if [ -z "$SDK_PATH" ]; then
    echo -e "${RED}❌ Android SDK not found in standard locations${NC}"
    echo ""
    echo "Please install Android SDK via Android Studio:"
    echo "1. Open Android Studio"
    echo "2. Go to: Settings/Preferences → Appearance & Behavior → System Settings → Android SDK"
    echo "3. Note the 'Android SDK Location' path"
    echo "4. Click 'Apply' to install if not installed"
    echo ""
    exit 1
fi

# Step 2: Check for ADB
echo ""
echo "📍 Step 2: Checking for ADB..."
echo ""

ADB_PATH="$SDK_PATH/platform-tools/adb"

if [ -f "$ADB_PATH" ]; then
    echo -e "${GREEN}✅ ADB found at: $ADB_PATH${NC}"
else
    echo -e "${RED}❌ ADB not found at: $ADB_PATH${NC}"
    echo ""
    echo "To install ADB via Android Studio:"
    echo "1. Open Android Studio"
    echo "2. Go to: Settings → Appearance & Behavior → System Settings → Android SDK"
    echo "3. Click 'SDK Tools' tab"
    echo "4. Check 'Android SDK Platform-Tools'"
    echo "5. Click 'Apply' to install"
    echo ""
    exit 1
fi

# Step 3: Add to PATH
echo ""
echo "📍 Step 3: Adding ADB to PATH..."
echo ""

SHELL_RC=""
if [ -n "$ZSH_VERSION" ]; then
    SHELL_RC="$HOME/.zshrc"
elif [ -n "$BASH_VERSION" ]; then
    SHELL_RC="$HOME/.bash_profile"
fi

if [ -z "$SHELL_RC" ]; then
    echo -e "${YELLOW}⚠️  Could not determine shell type${NC}"
    echo "Please manually add this to your shell config file:"
    echo ""
    echo "export ANDROID_HOME=$SDK_PATH"
    echo "export PATH=\$PATH:\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/tools"
    echo ""
    exit 1
fi

# Check if already in PATH
if grep -q "ANDROID_HOME" "$SHELL_RC" 2>/dev/null; then
    echo -e "${YELLOW}⚠️  Android SDK paths already exist in $SHELL_RC${NC}"
    echo "Current configuration:"
    grep "ANDROID" "$SHELL_RC" 2>/dev/null
    echo ""
    echo -e "${BLUE}If ADB still doesn't work, you may need to update the path.${NC}"
else
    echo -e "${GREEN}✅ Adding Android SDK to $SHELL_RC${NC}"
    echo "" >> "$SHELL_RC"
    echo "# Android SDK - Added by SmartMeal setup" >> "$SHELL_RC"
    echo "export ANDROID_HOME=$SDK_PATH" >> "$SHELL_RC"
    echo "export PATH=\$PATH:\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/tools" >> "$SHELL_RC"
    echo -e "${GREEN}✅ Configuration added${NC}"
fi

# Step 4: Instructions
echo ""
echo "=================================="
echo "✨ SETUP COMPLETE"
echo "=================================="
echo ""
echo -e "${BLUE}To use ADB immediately, run:${NC}"
echo ""
echo "  source $SHELL_RC"
echo ""
echo "OR close and reopen your terminal."
echo ""
echo -e "${BLUE}Then verify ADB works:${NC}"
echo ""
echo "  adb --version"
echo "  adb devices"
echo ""
echo -e "${BLUE}Quick test after sourcing:${NC}"
echo ""
echo "  source $SHELL_RC && adb --version"
echo ""

