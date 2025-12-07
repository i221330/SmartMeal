#!/bin/bash

# Test Profile Picture Sync
echo "======================================"
echo "Testing Profile Picture Sync"
echo "======================================"
echo ""

# Check if XAMPP is running
echo "1. Checking if XAMPP MySQL is running..."
if pgrep -x "mysqld" > /dev/null; then
    echo "✓ MySQL is running"
else
    echo "✗ MySQL is NOT running. Please start XAMPP first."
    exit 1
fi

# Test database connection
echo ""
echo "2. Testing database connection..."
mysql -u root -e "USE smartmeal_db; SELECT 'Database connection successful' as status;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✓ Database connection successful"
else
    echo "✗ Database connection failed"
    exit 1
fi

# Check users table structure
echo ""
echo "3. Checking users table structure..."
mysql -u root -e "USE smartmeal_db; DESCRIBE users;" | grep profile_image_url
if [ $? -eq 0 ]; then
    echo "✓ profile_image_url column exists"
else
    echo "✗ profile_image_url column NOT found!"
    exit 1
fi

# Check current user data
echo ""
echo "4. Checking user data with profile images..."
mysql -u root -e "USE smartmeal_db; SELECT firebase_uid, email, display_name, profile_image_url FROM users LIMIT 5;"

echo ""
echo "======================================"
echo "Test complete!"
echo "======================================"

