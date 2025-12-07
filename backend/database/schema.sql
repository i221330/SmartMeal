-- SmartMeal Database Schema
-- Run this in phpMyAdmin after starting XAMPP

CREATE DATABASE IF NOT EXISTS smartmeal_db;
USE smartmeal_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    firebase_uid VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    phone_number VARCHAR(20),
    display_name VARCHAR(255),
    profile_image_url TEXT,
    is_premium BOOLEAN DEFAULT FALSE,
    joined_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_sync_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_firebase_uid (firebase_uid),
    INDEX idx_email (email)
);

-- Pantry Items table
CREATE TABLE IF NOT EXISTS pantry_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    item_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity VARCHAR(100) NOT NULL,
    image_url TEXT,
    category VARCHAR(100),
    expiry_date BIGINT,
    last_updated BIGINT NOT NULL,
    is_synced BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_item_id (item_id)
);

-- Shopping Items table
CREATE TABLE IF NOT EXISTS shopping_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    item_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity VARCHAR(100) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    added_from_recipe_id VARCHAR(100),
    category VARCHAR(100),
    last_updated BIGINT NOT NULL,
    is_synced BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_item_id (item_id)
);

-- Recipes table
CREATE TABLE IF NOT EXISTS recipes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_id VARCHAR(100) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url TEXT,
    prep_time INT DEFAULT 0,
    cook_time INT DEFAULT 0,
    servings INT DEFAULT 1,
    difficulty VARCHAR(50) DEFAULT 'Medium',
    cuisine VARCHAR(100),
    diet_type VARCHAR(100),
    ingredients TEXT NOT NULL,
    instructions TEXT NOT NULL,
    is_global BOOLEAN DEFAULT FALSE,
    created_by INT,
    last_updated BIGINT NOT NULL,
    is_synced BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_is_global (is_global),
    INDEX idx_cuisine (cuisine),
    INDEX idx_diet_type (diet_type)
);

-- User Favorite Recipes table
CREATE TABLE IF NOT EXISTS user_favorites (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    recipe_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_favorite (user_id, recipe_id),
    INDEX idx_user_id (user_id)
);

-- Meal Plans table
CREATE TABLE IF NOT EXISTS meal_plans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    plan_id VARCHAR(100) UNIQUE NOT NULL,
    recipe_id VARCHAR(100) NOT NULL,
    recipe_name VARCHAR(255) NOT NULL,
    recipe_image_url TEXT,
    meal_date BIGINT NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    notes TEXT,
    last_updated BIGINT NOT NULL,
    is_synced BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_meal_date (meal_date)
);

-- Insert some sample global recipes
INSERT INTO recipes (recipe_id, title, description, prep_time, cook_time, servings, difficulty, cuisine, ingredients, instructions, is_global, last_updated) VALUES
('recipe_pasta_alfredo', 'Pasta Alfredo', 'Creamy pasta with parmesan cheese', 10, 15, 4, 'Easy', 'Italian',
'[{"name":"pasta","quantity":"1 lb"},{"name":"heavy cream","quantity":"1 cup"},{"name":"parmesan cheese","quantity":"1/2 cup"},{"name":"garlic","quantity":"2 cloves"},{"name":"butter","quantity":"2 tbsp"}]',
'1. Cook pasta according to package instructions\n2. Heat cream and butter in a pan\n3. Add garlic and parmesan\n4. Mix with pasta and serve',
TRUE, UNIX_TIMESTAMP() * 1000),

('recipe_tomato_soup', 'Tomato Soup', 'Classic homemade tomato soup', 10, 20, 4, 'Easy', 'American',
'[{"name":"tomatoes","quantity":"6"},{"name":"onion","quantity":"1"},{"name":"garlic","quantity":"3 cloves"},{"name":"vegetable broth","quantity":"2 cups"},{"name":"olive oil","quantity":"2 tbsp"}]',
'1. Sauté onions and garlic in olive oil\n2. Add tomatoes and broth\n3. Simmer for 20 minutes\n4. Blend until smooth',
TRUE, UNIX_TIMESTAMP() * 1000),

('recipe_caesar_salad', 'Caesar Salad', 'Fresh Caesar salad with croutons', 15, 0, 2, 'Easy', 'Italian',
'[{"name":"romaine lettuce","quantity":"1 head"},{"name":"parmesan cheese","quantity":"1/2 cup"},{"name":"croutons","quantity":"1 cup"},{"name":"caesar dressing","quantity":"1/2 cup"}]',
'1. Chop lettuce and place in a large bowl\n2. Add croutons and parmesan cheese\n3. Toss with caesar dressing\n4. Serve immediately',
TRUE, UNIX_TIMESTAMP() * 1000),

('recipe_chicken_stir_fry', 'Chicken Stir Fry', 'Quick and healthy chicken stir fry', 15, 10, 3, 'Medium', 'Chinese',
'[{"name":"chicken breast","quantity":"1 lb"},{"name":"mixed vegetables","quantity":"2 cups"},{"name":"soy sauce","quantity":"3 tbsp"},{"name":"ginger","quantity":"1 inch"},{"name":"garlic","quantity":"3 cloves"},{"name":"vegetable oil","quantity":"2 tbsp"}]',
'1. Cut chicken into bite-sized pieces\n2. Heat oil in wok\n3. Stir fry chicken until cooked\n4. Add vegetables, garlic, and ginger\n5. Add soy sauce and toss\n6. Serve over rice',
TRUE, UNIX_TIMESTAMP() * 1000),

('recipe_greek_salad', 'Greek Salad', 'Traditional Greek salad with feta', 10, 0, 4, 'Easy', 'Greek',
'[{"name":"cucumber","quantity":"1"},{"name":"tomatoes","quantity":"3"},{"name":"red onion","quantity":"1/2"},{"name":"feta cheese","quantity":"200g"},{"name":"olives","quantity":"1/2 cup"},{"name":"olive oil","quantity":"3 tbsp"},{"name":"lemon juice","quantity":"2 tbsp"}]',
'1. Chop cucumber, tomatoes, and onion\n2. Combine in a bowl\n3. Add olives and crumbled feta\n4. Drizzle with olive oil and lemon juice\n5. Season with salt and oregano',
TRUE, UNIX_TIMESTAMP() * 1000);

