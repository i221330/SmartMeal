#!/bin/bash

# SmartMeal - Quick Image Setup Script
# This script helps you quickly set up images for your recipes

echo "🍽️  SmartMeal - Image Setup Helper"
echo "=================================="
echo ""

# Check if XAMPP is running
echo "📋 Checking XAMPP status..."
if curl -s http://localhost/phpmyadmin > /dev/null 2>&1; then
    echo "✅ XAMPP MySQL is running!"
else
    echo "❌ XAMPP is not running or phpMyAdmin is not accessible"
    echo "   Please start XAMPP and run MySQL, then try again."
    exit 1
fi

echo ""
echo "📸 Image Setup Options:"
echo ""
echo "Option 1: Import SQL file with images (Recommended)"
echo "  - Opens phpMyAdmin in your browser"
echo "  - You'll manually import: recipe_seed_data_with_images.sql"
echo ""
echo "Option 2: Use placeholder images (Quick test)"
echo "  - Updates recipes with dynamic placeholder URLs"
echo "  - Good for testing, but images will be random"
echo ""

read -p "Choose option (1 or 2): " option

case $option in
    1)
        echo ""
        echo "📂 Opening phpMyAdmin..."
        echo "   Database: smartmeal_db"
        echo "   File to import: backend/database/recipe_seed_data_with_images.sql"
        echo ""
        echo "Steps:"
        echo "  1. Click on 'smartmeal_db' in left sidebar"
        echo "  2. Click 'Import' tab"
        echo "  3. Choose file: recipe_seed_data_with_images.sql"
        echo "  4. Click 'Go'"
        echo ""
        open "http://localhost/phpmyadmin"
        ;;
    2)
        echo ""
        echo "🔄 Updating recipes with placeholder images..."

        # Create a temporary SQL file with placeholder URLs
        cat > /tmp/update_recipe_images.sql << 'EOF'
USE smartmeal_db;

UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?pasta' WHERE recipe_id = 'recipe_001';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?soup,tomato' WHERE recipe_id = 'recipe_002';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?salad,caesar' WHERE recipe_id = 'recipe_003';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?eggs,scrambled' WHERE recipe_id = 'recipe_004';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?avocado,toast' WHERE recipe_id = 'recipe_005';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?greek,salad' WHERE recipe_id = 'recipe_006';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?grilled,cheese' WHERE recipe_id = 'recipe_007';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?fried,rice' WHERE recipe_id = 'recipe_008';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?caprese' WHERE recipe_id = 'recipe_009';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?smoothie,banana' WHERE recipe_id = 'recipe_010';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?chicken,stirfry' WHERE recipe_id = 'recipe_011';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?tacos,beef' WHERE recipe_id = 'recipe_012';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?shrimp,pasta' WHERE recipe_id = 'recipe_013';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?curry,vegetable' WHERE recipe_id = 'recipe_014';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?salmon,baked' WHERE recipe_id = 'recipe_015';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?fajitas' WHERE recipe_id = 'recipe_016';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?risotto' WHERE recipe_id = 'recipe_017';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?padthai' WHERE recipe_id = 'recipe_018';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?chili,beef' WHERE recipe_id = 'recipe_019';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?teriyaki,chicken' WHERE recipe_id = 'recipe_020';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?pizza,margherita' WHERE recipe_id = 'recipe_021';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?quesadilla' WHERE recipe_id = 'recipe_022';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?minestrone' WHERE recipe_id = 'recipe_023';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?chicken,parmesan' WHERE recipe_id = 'recipe_024';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?frenchtoast' WHERE recipe_id = 'recipe_025';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?soup,chicken' WHERE recipe_id = 'recipe_026';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?stirfry,vegetables' WHERE recipe_id = 'recipe_027';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?tuna,salad' WHERE recipe_id = 'recipe_028';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?pancakes' WHERE recipe_id = 'recipe_029';
UPDATE recipes SET image_url = 'https://source.unsplash.com/400x400/?carbonara' WHERE recipe_id = 'recipe_030';
EOF

        # Execute the SQL file
        mysql -u root smartmeal_db < /tmp/update_recipe_images.sql 2>/dev/null

        if [ $? -eq 0 ]; then
            echo "✅ Recipe images updated with placeholders!"
            echo "   Note: Images will be random from Unsplash"
        else
            echo "❌ Failed to update recipes"
            echo "   You may need to run this command manually:"
            echo "   mysql -u root smartmeal_db < /tmp/update_recipe_images.sql"
        fi

        rm /tmp/update_recipe_images.sql
        ;;
    *)
        echo "Invalid option. Please run again and choose 1 or 2."
        exit 1
        ;;
esac

echo ""
echo "✨ Next steps:"
echo "   1. Rebuild your app in Android Studio (Build > Rebuild Project)"
echo "   2. Run the app on your device/emulator"
echo "   3. Navigate to Recipes to see the images!"
echo ""
echo "📖 For more options, see: IMAGE_INTEGRATION_GUIDE.md"
echo ""

