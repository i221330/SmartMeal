-- SmartMeal Enhanced Database Schema
-- This includes master ingredients table and recipe tracking

USE smartmeal_db;

-- Master Ingredients Table (predefined ingredients users can add to pantry)
CREATE TABLE IF NOT EXISTS master_ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    common_unit VARCHAR(50) DEFAULT 'piece',
    image_url TEXT,
    searchable_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_searchable_name (searchable_name)
);

-- Recipe Made History (track when users make recipes)
CREATE TABLE IF NOT EXISTS recipe_made_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    recipe_id VARCHAR(100) NOT NULL,
    made_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    servings_made INT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_recipe (user_id, recipe_id),
    INDEX idx_made_date (made_date)
);

-- Insert Master Ingredients (Comprehensive List)
INSERT INTO master_ingredients (ingredient_id, name, category, common_unit, searchable_name) VALUES
-- Vegetables
('ing_tomato', 'Tomato', 'Vegetables', 'piece', 'tomato tomatoes'),
('ing_onion', 'Onion', 'Vegetables', 'piece', 'onion onions'),
('ing_garlic', 'Garlic', 'Vegetables', 'clove', 'garlic cloves'),
('ing_potato', 'Potato', 'Vegetables', 'piece', 'potato potatoes'),
('ing_carrot', 'Carrot', 'Vegetables', 'piece', 'carrot carrots'),
('ing_cucumber', 'Cucumber', 'Vegetables', 'piece', 'cucumber cucumbers'),
('ing_bell_pepper', 'Bell Pepper', 'Vegetables', 'piece', 'bell pepper peppers capsicum'),
('ing_lettuce', 'Lettuce', 'Vegetables', 'head', 'lettuce salad greens'),
('ing_spinach', 'Spinach', 'Vegetables', 'bunch', 'spinach leafy greens'),
('ing_broccoli', 'Broccoli', 'Vegetables', 'piece', 'broccoli'),
('ing_cauliflower', 'Cauliflower', 'Vegetables', 'head', 'cauliflower'),
('ing_zucchini', 'Zucchini', 'Vegetables', 'piece', 'zucchini courgette'),
('ing_eggplant', 'Eggplant', 'Vegetables', 'piece', 'eggplant aubergine brinjal'),
('ing_mushroom', 'Mushroom', 'Vegetables', 'cup', 'mushroom mushrooms'),
('ing_corn', 'Corn', 'Vegetables', 'piece', 'corn maize'),
('ing_peas', 'Peas', 'Vegetables', 'cup', 'peas green peas'),
('ing_beans', 'Green Beans', 'Vegetables', 'cup', 'beans green beans'),
('ing_cabbage', 'Cabbage', 'Vegetables', 'head', 'cabbage'),
('ing_celery', 'Celery', 'Vegetables', 'stalk', 'celery'),
('ing_asparagus', 'Asparagus', 'Vegetables', 'bunch', 'asparagus'),

-- Fruits
('ing_lemon', 'Lemon', 'Fruits', 'piece', 'lemon lemons citrus'),
('ing_lime', 'Lime', 'Fruits', 'piece', 'lime limes'),
('ing_apple', 'Apple', 'Fruits', 'piece', 'apple apples'),
('ing_banana', 'Banana', 'Fruits', 'piece', 'banana bananas'),
('ing_orange', 'Orange', 'Fruits', 'piece', 'orange oranges'),
('ing_mango', 'Mango', 'Fruits', 'piece', 'mango mangoes'),
('ing_avocado', 'Avocado', 'Fruits', 'piece', 'avocado avocados'),
('ing_strawberry', 'Strawberry', 'Fruits', 'cup', 'strawberry strawberries berries'),
('ing_blueberry', 'Blueberry', 'Fruits', 'cup', 'blueberry blueberries berries'),

-- Meat & Poultry
('ing_chicken_breast', 'Chicken Breast', 'Meat & Poultry', 'lb', 'chicken breast poultry'),
('ing_chicken_thigh', 'Chicken Thigh', 'Meat & Poultry', 'lb', 'chicken thigh poultry'),
('ing_ground_beef', 'Ground Beef', 'Meat & Poultry', 'lb', 'ground beef mince meat'),
('ing_beef_steak', 'Beef Steak', 'Meat & Poultry', 'lb', 'beef steak meat'),
('ing_pork_chop', 'Pork Chop', 'Meat & Poultry', 'piece', 'pork chop meat'),
('ing_bacon', 'Bacon', 'Meat & Poultry', 'strip', 'bacon'),
('ing_sausage', 'Sausage', 'Meat & Poultry', 'piece', 'sausage sausages'),
('ing_turkey', 'Turkey', 'Meat & Poultry', 'lb', 'turkey poultry'),
('ing_lamb', 'Lamb', 'Meat & Poultry', 'lb', 'lamb mutton'),

-- Seafood
('ing_salmon', 'Salmon', 'Seafood', 'fillet', 'salmon fish'),
('ing_tuna', 'Tuna', 'Seafood', 'can', 'tuna fish'),
('ing_shrimp', 'Shrimp', 'Seafood', 'lb', 'shrimp prawns seafood'),
('ing_cod', 'Cod', 'Seafood', 'fillet', 'cod fish'),
('ing_tilapia', 'Tilapia', 'Seafood', 'fillet', 'tilapia fish'),

-- Dairy & Eggs
('ing_milk', 'Milk', 'Dairy & Eggs', 'cup', 'milk dairy'),
('ing_butter', 'Butter', 'Dairy & Eggs', 'tbsp', 'butter dairy'),
('ing_cheese', 'Cheese', 'Dairy & Eggs', 'cup', 'cheese dairy'),
('ing_parmesan', 'Parmesan Cheese', 'Dairy & Eggs', 'cup', 'parmesan cheese dairy'),
('ing_mozzarella', 'Mozzarella Cheese', 'Dairy & Eggs', 'cup', 'mozzarella cheese dairy'),
('ing_cheddar', 'Cheddar Cheese', 'Dairy & Eggs', 'cup', 'cheddar cheese dairy'),
('ing_feta', 'Feta Cheese', 'Dairy & Eggs', 'cup', 'feta cheese dairy'),
('ing_yogurt', 'Yogurt', 'Dairy & Eggs', 'cup', 'yogurt dairy'),
('ing_sour_cream', 'Sour Cream', 'Dairy & Eggs', 'cup', 'sour cream dairy'),
('ing_heavy_cream', 'Heavy Cream', 'Dairy & Eggs', 'cup', 'heavy cream dairy'),
('ing_eggs', 'Eggs', 'Dairy & Eggs', 'piece', 'eggs egg'),

-- Grains & Pasta
('ing_rice', 'Rice', 'Grains & Pasta', 'cup', 'rice grain'),
('ing_pasta', 'Pasta', 'Grains & Pasta', 'lb', 'pasta noodles spaghetti'),
('ing_bread', 'Bread', 'Grains & Pasta', 'slice', 'bread'),
('ing_flour', 'All-Purpose Flour', 'Grains & Pasta', 'cup', 'flour all purpose'),
('ing_quinoa', 'Quinoa', 'Grains & Pasta', 'cup', 'quinoa grain'),
('ing_oats', 'Oats', 'Grains & Pasta', 'cup', 'oats oatmeal'),
('ing_breadcrumbs', 'Bread Crumbs', 'Grains & Pasta', 'cup', 'breadcrumbs bread crumbs'),
('ing_couscous', 'Couscous', 'Grains & Pasta', 'cup', 'couscous grain'),
('ing_tortilla', 'Tortilla', 'Grains & Pasta', 'piece', 'tortilla wrap'),

-- Legumes & Beans
('ing_black_beans', 'Black Beans', 'Legumes & Beans', 'can', 'black beans legumes'),
('ing_kidney_beans', 'Kidney Beans', 'Legumes & Beans', 'can', 'kidney beans legumes'),
('ing_chickpeas', 'Chickpeas', 'Legumes & Beans', 'can', 'chickpeas garbanzo beans legumes'),
('ing_lentils', 'Lentils', 'Legumes & Beans', 'cup', 'lentils legumes dal'),
('ing_tofu', 'Tofu', 'Legumes & Beans', 'block', 'tofu soy protein'),

-- Spices & Herbs
('ing_salt', 'Salt', 'Spices & Herbs', 'tsp', 'salt seasoning'),
('ing_black_pepper', 'Black Pepper', 'Spices & Herbs', 'tsp', 'black pepper pepper seasoning'),
('ing_cumin', 'Cumin', 'Spices & Herbs', 'tsp', 'cumin spice'),
('ing_paprika', 'Paprika', 'Spices & Herbs', 'tsp', 'paprika spice'),
('ing_chili_powder', 'Chili Powder', 'Spices & Herbs', 'tsp', 'chili powder spice hot'),
('ing_oregano', 'Oregano', 'Spices & Herbs', 'tsp', 'oregano herb'),
('ing_basil', 'Basil', 'Spices & Herbs', 'tbsp', 'basil herb'),
('ing_thyme', 'Thyme', 'Spices & Herbs', 'tsp', 'thyme herb'),
('ing_rosemary', 'Rosemary', 'Spices & Herbs', 'tsp', 'rosemary herb'),
('ing_cilantro', 'Cilantro', 'Spices & Herbs', 'tbsp', 'cilantro coriander herb'),
('ing_parsley', 'Parsley', 'Spices & Herbs', 'tbsp', 'parsley herb'),
('ing_cinnamon', 'Cinnamon', 'Spices & Herbs', 'tsp', 'cinnamon spice'),
('ing_ginger', 'Ginger', 'Spices & Herbs', 'inch', 'ginger spice root'),
('ing_turmeric', 'Turmeric', 'Spices & Herbs', 'tsp', 'turmeric spice'),
('ing_curry_powder', 'Curry Powder', 'Spices & Herbs', 'tbsp', 'curry powder spice'),
('ing_bay_leaf', 'Bay Leaf', 'Spices & Herbs', 'piece', 'bay leaf herb'),
('ing_nutmeg', 'Nutmeg', 'Spices & Herbs', 'tsp', 'nutmeg spice'),
('ing_cardamom', 'Cardamom', 'Spices & Herbs', 'piece', 'cardamom spice'),

-- Oils & Condiments
('ing_olive_oil', 'Olive Oil', 'Oils & Condiments', 'tbsp', 'olive oil cooking oil'),
('ing_vegetable_oil', 'Vegetable Oil', 'Oils & Condiments', 'tbsp', 'vegetable oil cooking oil'),
('ing_coconut_oil', 'Coconut Oil', 'Oils & Condiments', 'tbsp', 'coconut oil cooking oil'),
('ing_soy_sauce', 'Soy Sauce', 'Oils & Condiments', 'tbsp', 'soy sauce condiment'),
('ing_vinegar', 'Vinegar', 'Oils & Condiments', 'tbsp', 'vinegar'),
('ing_balsamic_vinegar', 'Balsamic Vinegar', 'Oils & Condiments', 'tbsp', 'balsamic vinegar'),
('ing_ketchup', 'Ketchup', 'Oils & Condiments', 'tbsp', 'ketchup tomato sauce'),
('ing_mustard', 'Mustard', 'Oils & Condiments', 'tsp', 'mustard condiment'),
('ing_mayonnaise', 'Mayonnaise', 'Oils & Condiments', 'tbsp', 'mayonnaise mayo'),
('ing_hot_sauce', 'Hot Sauce', 'Oils & Condiments', 'tsp', 'hot sauce spicy'),
('ing_worcestershire', 'Worcestershire Sauce', 'Oils & Condiments', 'tbsp', 'worcestershire sauce'),
('ing_fish_sauce', 'Fish Sauce', 'Oils & Condiments', 'tbsp', 'fish sauce'),
('ing_sesame_oil', 'Sesame Oil', 'Oils & Condiments', 'tsp', 'sesame oil'),

-- Sweeteners & Baking
('ing_sugar', 'Sugar', 'Sweeteners & Baking', 'cup', 'sugar sweetener'),
('ing_brown_sugar', 'Brown Sugar', 'Sweeteners & Baking', 'cup', 'brown sugar sweetener'),
('ing_honey', 'Honey', 'Sweeteners & Baking', 'tbsp', 'honey sweetener'),
('ing_maple_syrup', 'Maple Syrup', 'Sweeteners & Baking', 'tbsp', 'maple syrup sweetener'),
('ing_vanilla_extract', 'Vanilla Extract', 'Sweeteners & Baking', 'tsp', 'vanilla extract baking'),
('ing_baking_powder', 'Baking Powder', 'Sweeteners & Baking', 'tsp', 'baking powder leavening'),
('ing_baking_soda', 'Baking Soda', 'Sweeteners & Baking', 'tsp', 'baking soda leavening'),
('ing_cocoa_powder', 'Cocoa Powder', 'Sweeteners & Baking', 'tbsp', 'cocoa powder chocolate'),
('ing_chocolate_chips', 'Chocolate Chips', 'Sweeteners & Baking', 'cup', 'chocolate chips baking'),

-- Broths & Stocks
('ing_chicken_broth', 'Chicken Broth', 'Broths & Stocks', 'cup', 'chicken broth stock'),
('ing_beef_broth', 'Beef Broth', 'Broths & Stocks', 'cup', 'beef broth stock'),
('ing_vegetable_broth', 'Vegetable Broth', 'Broths & Stocks', 'cup', 'vegetable broth stock'),

-- Nuts & Seeds
('ing_almonds', 'Almonds', 'Nuts & Seeds', 'cup', 'almonds nuts'),
('ing_walnuts', 'Walnuts', 'Nuts & Seeds', 'cup', 'walnuts nuts'),
('ing_peanuts', 'Peanuts', 'Nuts & Seeds', 'cup', 'peanuts nuts'),
('ing_cashews', 'Cashews', 'Nuts & Seeds', 'cup', 'cashews nuts'),
('ing_pine_nuts', 'Pine Nuts', 'Nuts & Seeds', 'tbsp', 'pine nuts'),
('ing_sesame_seeds', 'Sesame Seeds', 'Nuts & Seeds', 'tbsp', 'sesame seeds'),
('ing_sunflower_seeds', 'Sunflower Seeds', 'Nuts & Seeds', 'tbsp', 'sunflower seeds'),
('ing_chia_seeds', 'Chia Seeds', 'Nuts & Seeds', 'tbsp', 'chia seeds'),

-- Miscellaneous
('ing_croutons', 'Croutons', 'Miscellaneous', 'cup', 'croutons bread'),
('ing_caesar_dressing', 'Caesar Dressing', 'Miscellaneous', 'cup', 'caesar dressing salad'),
('ing_ranch_dressing', 'Ranch Dressing', 'Miscellaneous', 'cup', 'ranch dressing salad'),
('ing_olives', 'Olives', 'Miscellaneous', 'cup', 'olives'),
('ing_capers', 'Capers', 'Miscellaneous', 'tbsp', 'capers'),
('ing_pickles', 'Pickles', 'Miscellaneous', 'piece', 'pickles cucumber'),
('ing_peanut_butter', 'Peanut Butter', 'Miscellaneous', 'tbsp', 'peanut butter spread'),
('ing_jam', 'Jam', 'Miscellaneous', 'tbsp', 'jam jelly preserve'),
('ing_tomato_paste', 'Tomato Paste', 'Miscellaneous', 'tbsp', 'tomato paste'),
('ing_tomato_sauce', 'Tomato Sauce', 'Miscellaneous', 'cup', 'tomato sauce'),
('ing_salsa', 'Salsa', 'Miscellaneous', 'cup', 'salsa sauce'),
('ing_coconut_milk', 'Coconut Milk', 'Miscellaneous', 'can', 'coconut milk'),
('ing_wine', 'Wine', 'Miscellaneous', 'cup', 'wine cooking alcohol');

