-- SmartMeal Recipe Seed Data
-- 30 diverse recipes with proper ingredients, instructions, and details

USE smartmeal_db;

-- Clear existing recipes first
DELETE FROM recipes WHERE is_global = TRUE;

-- Insert 30 Global Recipes
INSERT INTO recipes (recipe_id, title, description, prep_time, cook_time, servings, difficulty, cuisine, diet_type, ingredients, instructions, is_global, last_updated, image_url) VALUES

-- Easy Recipes (1-10)
('recipe_001', 'Classic Pasta Alfredo', 'Creamy pasta with parmesan cheese', 10, 15, 4, 'Easy', 'Italian', 'Vegetarian',
'[{"name":"pasta","quantity":"1 lb"},{"name":"heavy cream","quantity":"1 cup"},{"name":"parmesan cheese","quantity":"1/2 cup"},{"name":"garlic","quantity":"2 cloves"},{"name":"butter","quantity":"2 tbsp"},{"name":"salt","quantity":"to taste"},{"name":"black pepper","quantity":"to taste"}]',
'1. Bring a large pot of salted water to boil\n2. Cook pasta according to package directions until al dente\n3. While pasta cooks, melt butter in a large pan over medium heat\n4. Add minced garlic and sauté for 1 minute\n5. Pour in heavy cream and bring to a simmer\n6. Add parmesan cheese and stir until melted and smooth\n7. Drain pasta and add to the sauce\n8. Toss to coat evenly\n9. Season with salt and pepper\n10. Serve immediately with extra parmesan',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_002', 'Homemade Tomato Soup', 'Rich and comforting tomato soup', 10, 25, 4, 'Easy', 'American', 'Vegetarian',
'[{"name":"tomato","quantity":"8 pieces"},{"name":"onion","quantity":"1"},{"name":"garlic","quantity":"4 cloves"},{"name":"vegetable broth","quantity":"3 cups"},{"name":"olive oil","quantity":"3 tbsp"},{"name":"sugar","quantity":"1 tsp"},{"name":"salt","quantity":"to taste"},{"name":"black pepper","quantity":"to taste"},{"name":"basil","quantity":"1/4 cup"}]',
'1. Heat olive oil in a large pot over medium heat\n2. Dice onion and sauté until soft, about 5 minutes\n3. Add minced garlic and cook for 1 minute\n4. Roughly chop tomatoes and add to pot\n5. Pour in vegetable broth\n6. Add sugar, salt, and pepper\n7. Bring to a boil, then reduce heat and simmer for 20 minutes\n8. Use an immersion blender to puree until smooth\n9. Stir in fresh basil\n10. Adjust seasoning and serve hot',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_003', 'Fresh Caesar Salad', 'Classic Caesar salad with crunchy croutons', 15, 0, 4, 'Easy', 'Italian', 'Vegetarian',
'[{"name":"lettuce","quantity":"2 heads"},{"name":"parmesan cheese","quantity":"3/4 cup"},{"name":"croutons","quantity":"2 cups"},{"name":"caesar dressing","quantity":"3/4 cup"},{"name":"lemon juice","quantity":"2 tbsp"},{"name":"black pepper","quantity":"to taste"}]',
'1. Wash and thoroughly dry romaine lettuce\n2. Tear lettuce into bite-sized pieces\n3. Place in a large salad bowl\n4. Add croutons\n5. Drizzle with Caesar dressing\n6. Toss to coat evenly\n7. Add freshly grated parmesan cheese\n8. Squeeze lemon juice over top\n9. Add fresh cracked black pepper\n10. Serve immediately',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_004', 'Simple Scrambled Eggs', 'Fluffy and creamy scrambled eggs', 5, 5, 2, 'Easy', 'American', 'Vegetarian',
'[{"name":"eggs","quantity":"6"},{"name":"milk","quantity":"3 tbsp"},{"name":"butter","quantity":"2 tbsp"},{"name":"salt","quantity":"to taste"},{"name":"black pepper","quantity":"to taste"}]',
'1. Crack eggs into a bowl\n2. Add milk, salt, and pepper\n3. Whisk until well combined\n4. Heat butter in a non-stick pan over medium-low heat\n5. Pour in egg mixture\n6. Let sit for 20 seconds without stirring\n7. Gently push eggs from edge to center with spatula\n8. Continue until eggs are softly set but still moist\n9. Remove from heat immediately\n10. Serve hot',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_005', 'Avocado Toast', 'Trendy and nutritious avocado toast', 10, 5, 2, 'Easy', 'American', 'Vegetarian',
'[{"name":"bread","quantity":"4 slices"},{"name":"avocado","quantity":"2"},{"name":"lemon juice","quantity":"2 tbsp"},{"name":"salt","quantity":"to taste"},{"name":"black pepper","quantity":"to taste"},{"name":"eggs","quantity":"2 optional"}]',
'1. Toast bread slices until golden brown\n2. Cut avocados in half and remove pit\n3. Scoop avocado flesh into a bowl\n4. Mash with a fork\n5. Add lemon juice, salt, and pepper\n6. Mix well\n7. Spread avocado mixture evenly on toast\n8. Optional: Top with a fried or poached egg\n9. Sprinkle with extra pepper\n10. Serve immediately',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_006', 'Classic Greek Salad', 'Traditional Greek salad with feta', 15, 0, 4, 'Easy', 'Greek', 'Vegetarian',
'[{"name":"cucumber","quantity":"2"},{"name":"tomato","quantity":"4"},{"name":"onion","quantity":"1"},{"name":"feta cheese","quantity":"200g"},{"name":"olives","quantity":"1 cup"},{"name":"olive oil","quantity":"4 tbsp"},{"name":"lemon juice","quantity":"3 tbsp"},{"name":"oregano","quantity":"1 tsp"},{"name":"salt","quantity":"to taste"}]',
'1. Dice cucumbers and tomatoes into chunks\n2. Thinly slice red onion\n3. Combine vegetables in a large bowl\n4. Add whole olives\n5. Crumble feta cheese on top\n6. In a small bowl, whisk olive oil, lemon juice, oregano, and salt\n7. Pour dressing over salad\n8. Toss gently to combine\n9. Let sit for 5 minutes before serving\n10. Serve at room temperature',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_007', 'Grilled Cheese Sandwich', 'Crispy and gooey grilled cheese', 5, 8, 2, 'Easy', 'American', 'Vegetarian',
'[{"name":"bread","quantity":"4 slices"},{"name":"cheddar cheese","quantity":"4 slices"},{"name":"butter","quantity":"3 tbsp"}]',
'1. Butter one side of each bread slice\n2. Place two slices butter-side down on a plate\n3. Top each with 2 slices of cheese\n4. Place remaining bread slices on top, butter-side up\n5. Heat a skillet over medium heat\n6. Place sandwiches in the pan\n7. Cook for 3-4 minutes until golden brown\n8. Flip carefully with a spatula\n9. Cook other side for 3-4 minutes\n10. Serve hot and melty',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_008', 'Simple Fried Rice', 'Quick fried rice with vegetables', 10, 15, 4, 'Easy', 'Chinese', 'Can be Vegan',
'[{"name":"rice","quantity":"4 cups cooked"},{"name":"eggs","quantity":"2"},{"name":"peas","quantity":"1 cup"},{"name":"carrot","quantity":"1"},{"name":"onion","quantity":"1"},{"name":"garlic","quantity":"3 cloves"},{"name":"soy sauce","quantity":"4 tbsp"},{"name":"vegetable oil","quantity":"3 tbsp"},{"name":"sesame oil","quantity":"1 tsp"}]',
'1. Use day-old rice for best results\n2. Heat oil in a large wok or pan\n3. Scramble eggs and set aside\n4. Dice carrots and onion, mince garlic\n5. Stir-fry vegetables until tender\n6. Add rice and break up any clumps\n7. Add soy sauce and stir well\n8. Add back the scrambled eggs\n9. Drizzle with sesame oil\n10. Toss everything together and serve hot',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_009', 'Caprese Salad', 'Fresh mozzarella with tomatoes and basil', 10, 0, 4, 'Easy', 'Italian', 'Vegetarian',
'[{"name":"tomato","quantity":"4 large"},{"name":"mozzarella cheese","quantity":"1 lb"},{"name":"basil","quantity":"1 bunch"},{"name":"olive oil","quantity":"1/4 cup"},{"name":"balsamic vinegar","quantity":"2 tbsp"},{"name":"salt","quantity":"to taste"},{"name":"black pepper","quantity":"to taste"}]',
'1. Slice tomatoes into 1/4 inch rounds\n2. Slice mozzarella into similar thickness\n3. Arrange tomato and mozzarella slices alternately on a platter\n4. Tuck fresh basil leaves between slices\n5. Drizzle with olive oil\n6. Drizzle with balsamic vinegar\n7. Sprinkle with salt and freshly ground pepper\n8. Let sit for 5 minutes to meld flavors\n9. Serve at room temperature\n10. Best enjoyed within an hour of assembly',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_010', 'Peanut Butter Banana Smoothie', 'Creamy and protein-packed smoothie', 5, 0, 2, 'Easy', 'American', 'Vegetarian',
'[{"name":"banana","quantity":"2"},{"name":"peanut butter","quantity":"3 tbsp"},{"name":"milk","quantity":"2 cups"},{"name":"honey","quantity":"2 tbsp"},{"name":"vanilla extract","quantity":"1 tsp"}]',
'1. Peel and break bananas into chunks\n2. Add bananas to blender\n3. Add peanut butter\n4. Pour in milk\n5. Add honey and vanilla extract\n6. Blend on high until smooth and creamy\n7. Add ice if desired for a colder smoothie\n8. Blend again briefly\n9. Pour into glasses\n10. Serve immediately',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

-- Medium Difficulty (11-20)
('recipe_011', 'Chicken Stir Fry', 'Quick and healthy chicken stir fry', 20, 15, 4, 'Medium', 'Chinese', NULL,
'[{"name":"chicken breast","quantity":"1.5 lb"},{"name":"bell pepper","quantity":"2"},{"name":"broccoli","quantity":"2 cups"},{"name":"carrot","quantity":"2"},{"name":"onion","quantity":"1"},{"name":"garlic","quantity":"4 cloves"},{"name":"ginger","quantity":"1 inch"},{"name":"soy sauce","quantity":"1/4 cup"},{"name":"vegetable oil","quantity":"3 tbsp"},{"name":"cornstarch","quantity":"2 tsp"}]',
'1. Cut chicken into bite-sized pieces\n2. Marinate chicken with 2 tbsp soy sauce and cornstarch\n3. Slice all vegetables into uniform pieces\n4. Heat wok over high heat with oil\n5. Stir-fry chicken until cooked through, remove and set aside\n6. Add more oil if needed, stir-fry vegetables\n7. Add minced garlic and ginger\n8. Return chicken to wok\n9. Add remaining soy sauce\n10. Toss everything together and serve over rice',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_012', 'Beef Tacos', 'Flavorful beef tacos with toppings', 15, 20, 6, 'Medium', 'Mexican', NULL,
'[{"name":"ground beef","quantity":"1.5 lb"},{"name":"onion","quantity":"1"},{"name":"garlic","quantity":"3 cloves"},{"name":"tomato","quantity":"2"},{"name":"lettuce","quantity":"1 head"},{"name":"cheddar cheese","quantity":"1 cup"},{"name":"sour cream","quantity":"1/2 cup"},{"name":"tortilla","quantity":"12"},{"name":"cumin","quantity":"2 tsp"},{"name":"chili powder","quantity":"2 tsp"}]',
'1. Brown ground beef in a large pan\n2. Add diced onion and minced garlic\n3. Season with cumin, chili powder, salt, and pepper\n4. Cook until beef is fully cooked\n5. Warm tortillas in another pan or microwave\n6. Dice tomatoes and shred lettuce\n7. Grate cheese if needed\n8. Set up taco bar with all ingredients\n9. Let everyone assemble their own tacos\n10. Serve with lime wedges',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_013', 'Lemon Garlic Shrimp Pasta', 'Light and flavorful shrimp pasta', 15, 20, 4, 'Medium', 'Italian', NULL,
'[{"name":"shrimp","quantity":"1 lb"},{"name":"pasta","quantity":"1 lb"},{"name":"garlic","quantity":"6 cloves"},{"name":"lemon","quantity":"2"},{"name":"butter","quantity":"4 tbsp"},{"name":"olive oil","quantity":"2 tbsp"},{"name":"parsley","quantity":"1/4 cup"},{"name":"parmesan cheese","quantity":"1/2 cup"},{"name":"black pepper","quantity":"to taste"}]',
'1. Cook pasta according to package directions\n2. Season shrimp with salt and pepper\n3. Heat olive oil in a large pan\n4. Cook shrimp until pink, about 2 minutes per side\n5. Remove shrimp and set aside\n6. Add butter and minced garlic to pan\n7. Sauté garlic until fragrant\n8. Add lemon juice and zest\n9. Return shrimp to pan with drained pasta\n10. Toss with parsley and parmesan, serve hot',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_014', 'Vegetable Curry', 'Rich and creamy vegetable curry', 20, 30, 6, 'Medium', 'Indian', 'Vegan',
'[{"name":"potato","quantity":"3"},{"name":"cauliflower","quantity":"1 head"},{"name":"peas","quantity":"1 cup"},{"name":"onion","quantity":"2"},{"name":"tomato","quantity":"3"},{"name":"garlic","quantity":"5 cloves"},{"name":"ginger","quantity":"2 inch"},{"name":"coconut milk","quantity":"1 can"},{"name":"curry powder","quantity":"3 tbsp"},{"name":"vegetable oil","quantity":"3 tbsp"}]',
'1. Cube potatoes and cauliflower\n2. Heat oil in a large pot\n3. Sauté diced onions until golden\n4. Add minced ginger and garlic\n5. Add curry powder and toast spices for 1 minute\n6. Add diced tomatoes and cook until soft\n7. Add potatoes and cauliflower\n8. Pour in coconut milk\n9. Simmer covered for 20 minutes until vegetables are tender\n10. Add peas, cook 5 more minutes, serve with rice',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_015', 'Baked Salmon', 'Herb-crusted baked salmon fillets', 10, 20, 4, 'Medium', 'American', NULL,
'[{"name":"salmon","quantity":"4 fillets"},{"name":"lemon","quantity":"2"},{"name":"garlic","quantity":"4 cloves"},{"name":"olive oil","quantity":"3 tbsp"},{"name":"dill","quantity":"2 tbsp"},{"name":"parsley","quantity":"2 tbsp"},{"name":"salt","quantity":"to taste"},{"name":"black pepper","quantity":"to taste"}]',
'1. Preheat oven to 400°F (200°C)\n2. Line a baking sheet with parchment paper\n3. Place salmon fillets skin-side down\n4. Mix olive oil, minced garlic, chopped herbs, salt, and pepper\n5. Brush mixture over salmon\n6. Arrange lemon slices on top\n7. Bake for 15-20 minutes until salmon flakes easily\n8. Check doneness at 15 minutes\n9. Remove from oven and let rest 2 minutes\n10. Serve with fresh lemon wedges',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_016', 'Chicken Fajitas', 'Sizzling chicken fajitas with peppers', 20, 15, 4, 'Medium', 'Mexican', NULL,
'[{"name":"chicken breast","quantity":"1.5 lb"},{"name":"bell pepper","quantity":"3"},{"name":"onion","quantity":"2"},{"name":"lime","quantity":"2"},{"name":"tortilla","quantity":"8"},{"name":"cumin","quantity":"1 tbsp"},{"name":"chili powder","quantity":"1 tbsp"},{"name":"garlic","quantity":"3 cloves"},{"name":"vegetable oil","quantity":"3 tbsp"}]',
'1. Slice chicken into thin strips\n2. Marinate with lime juice, cumin, chili powder, and minced garlic\n3. Slice bell peppers and onions into strips\n4. Heat oil in a large skillet over high heat\n5. Cook chicken until browned and cooked through\n6. Remove chicken and set aside\n7. Sauté peppers and onions until tender-crisp\n8. Return chicken to pan, toss together\n9. Warm tortillas\n10. Serve with sour cream, cheese, and salsa',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_017', 'Mushroom Risotto', 'Creamy Italian risotto with mushrooms', 10, 35, 4, 'Medium', 'Italian', 'Vegetarian',
'[{"name":"rice","quantity":"2 cups arborio"},{"name":"mushroom","quantity":"1 lb"},{"name":"onion","quantity":"1"},{"name":"garlic","quantity":"3 cloves"},{"name":"vegetable broth","quantity":"6 cups"},{"name":"white wine","quantity":"1/2 cup"},{"name":"parmesan cheese","quantity":"1 cup"},{"name":"butter","quantity":"4 tbsp"},{"name":"olive oil","quantity":"2 tbsp"}]',
'1. Heat broth in a pot and keep warm\n2. Slice mushrooms and sauté in butter until golden, set aside\n3. Heat olive oil, sauté diced onion until soft\n4. Add garlic and rice, toast for 2 minutes\n5. Add wine and stir until absorbed\n6. Add broth one ladle at a time, stirring constantly\n7. Continue until rice is creamy and al dente, about 25 minutes\n8. Stir in mushrooms, butter, and parmesan\n9. Season with salt and pepper\n10. Serve immediately with extra parmesan',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_018', 'Pad Thai', 'Classic Thai rice noodles', 25, 15, 4, 'Medium', 'Thai', NULL,
'[{"name":"rice noodles","quantity":"8 oz"},{"name":"shrimp","quantity":"1 lb"},{"name":"eggs","quantity":"2"},{"name":"bean sprouts","quantity":"2 cups"},{"name":"peanuts","quantity":"1/2 cup"},{"name":"garlic","quantity":"4 cloves"},{"name":"lime","quantity":"2"},{"name":"fish sauce","quantity":"3 tbsp"},{"name":"sugar","quantity":"2 tbsp"},{"name":"vegetable oil","quantity":"3 tbsp"}]',
'1. Soak rice noodles in warm water for 30 minutes, drain\n2. Mix fish sauce, sugar, and lime juice for sauce\n3. Heat oil in wok over high heat\n4. Cook shrimp until pink, remove and set aside\n5. Scramble eggs, remove\n6. Add garlic, stir-fry briefly\n7. Add drained noodles and sauce\n8. Toss until noodles are coated and soft\n9. Add back shrimp, eggs, and bean sprouts\n10. Serve topped with crushed peanuts and lime wedges',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_019', 'Beef Chili', 'Hearty and spicy beef chili', 15, 60, 8, 'Medium', 'American', NULL,
'[{"name":"ground beef","quantity":"2 lb"},{"name":"kidney beans","quantity":"2 cans"},{"name":"black beans","quantity":"1 can"},{"name":"tomato","quantity":"2 cans"},{"name":"onion","quantity":"2"},{"name":"bell pepper","quantity":"2"},{"name":"garlic","quantity":"5 cloves"},{"name":"chili powder","quantity":"3 tbsp"},{"name":"cumin","quantity":"2 tbsp"},{"name":"tomato paste","quantity":"2 tbsp"}]',
'1. Brown ground beef in a large pot, drain excess fat\n2. Add diced onions and peppers, cook until soft\n3. Add minced garlic, chili powder, and cumin\n4. Stir in tomato paste\n5. Add canned tomatoes with juice\n6. Drain and add beans\n7. Season with salt and pepper\n8. Bring to a boil, then simmer for 45 minutes\n9. Stir occasionally\n10. Serve with shredded cheese, sour cream, and cornbread',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_020', 'Teriyaki Chicken', 'Sweet and savory teriyaki chicken', 15, 25, 4, 'Medium', 'Japanese', NULL,
'[{"name":"chicken thigh","quantity":"2 lb"},{"name":"soy sauce","quantity":"1/2 cup"},{"name":"sugar","quantity":"1/4 cup"},{"name":"ginger","quantity":"2 inch"},{"name":"garlic","quantity":"4 cloves"},{"name":"sesame oil","quantity":"2 tsp"},{"name":"cornstarch","quantity":"2 tbsp"},{"name":"green onion","quantity":"3"},{"name":"sesame seeds","quantity":"2 tbsp"}]',
'1. Cut chicken into bite-sized pieces\n2. Mix soy sauce, sugar, minced ginger, garlic, and sesame oil for sauce\n3. Heat oil in a pan over medium-high heat\n4. Cook chicken until golden and cooked through\n5. Remove chicken from pan\n6. Add sauce to pan and bring to a simmer\n7. Mix cornstarch with water to make a slurry\n8. Add to sauce to thicken\n9. Return chicken to pan and coat with sauce\n10. Garnish with sliced green onions and sesame seeds, serve over rice',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

-- More Diverse Recipes (21-30)
('recipe_021', 'Margherita Pizza', 'Classic Italian pizza with fresh basil', 20, 15, 4, 'Medium', 'Italian', 'Vegetarian',
'[{"name":"pizza dough","quantity":"1 lb"},{"name":"tomato sauce","quantity":"1 cup"},{"name":"mozzarella cheese","quantity":"2 cups"},{"name":"tomato","quantity":"3"},{"name":"basil","quantity":"1 bunch"},{"name":"olive oil","quantity":"3 tbsp"},{"name":"garlic","quantity":"2 cloves"},{"name":"salt","quantity":"to taste"}]',
'1. Preheat oven to 475°F (245°C) with pizza stone if available\n2. Roll out pizza dough to desired thickness\n3. Mix tomato sauce with minced garlic\n4. Spread sauce evenly on dough\n5. Tear mozzarella and distribute over pizza\n6. Slice tomatoes thinly and arrange on top\n7. Drizzle with olive oil\n8. Bake for 12-15 minutes until crust is golden\n9. Remove from oven\n10. Top with fresh basil leaves, slice and serve',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_022', 'Chicken Quesadilla', 'Cheesy chicken quesadillas', 15, 15, 4, 'Easy', 'Mexican', NULL,
'[{"name":"chicken breast","quantity":"1 lb"},{"name":"tortilla","quantity":"8"},{"name":"cheddar cheese","quantity":"2 cups"},{"name":"bell pepper","quantity":"1"},{"name":"onion","quantity":"1"},{"name":"cumin","quantity":"1 tsp"},{"name":"chili powder","quantity":"1 tsp"},{"name":"vegetable oil","quantity":"2 tbsp"},{"name":"sour cream","quantity":"for serving"}]',
'1. Cook and shred chicken, season with cumin and chili powder\n2. Dice peppers and onions, sauté until soft\n3. Mix chicken with vegetables\n4. Place tortilla in a pan over medium heat\n5. Sprinkle cheese on half the tortilla\n6. Add chicken mixture on top of cheese\n7. Fold tortilla in half\n8. Cook until golden brown, about 3 minutes per side\n9. Remove and cut into wedges\n10. Serve with sour cream and salsa',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_023', 'Minestrone Soup', 'Italian vegetable and bean soup', 15, 35, 6, 'Medium', 'Italian', 'Vegan',
'[{"name":"onion","quantity":"1"},{"name":"carrot","quantity":"2"},{"name":"celery","quantity":"2 stalks"},{"name":"zucchini","quantity":"1"},{"name":"tomato","quantity":"1 can"},{"name":"kidney beans","quantity":"1 can"},{"name":"pasta","quantity":"1 cup small"},{"name":"vegetable broth","quantity":"6 cups"},{"name":"garlic","quantity":"4 cloves"},{"name":"basil","quantity":"2 tsp"}]',
'1. Dice all vegetables uniformly\n2. Heat olive oil in large pot\n3. Sauté onion, carrot, and celery until soft\n4. Add minced garlic, cook 1 minute\n5. Add zucchini and cook 3 minutes\n6. Pour in broth and canned tomatoes\n7. Add basil, salt, and pepper\n8. Bring to a boil, add pasta\n9. Simmer 10 minutes\n10. Add drained beans, heat through and serve',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_024', 'Chicken Parmesan', 'Breaded chicken with marinara and cheese', 20, 30, 4, 'Medium', 'Italian', NULL,
'[{"name":"chicken breast","quantity":"4"},{"name":"breadcrumbs","quantity":"2 cups"},{"name":"parmesan cheese","quantity":"1 cup"},{"name":"mozzarella cheese","quantity":"2 cups"},{"name":"tomato sauce","quantity":"2 cups"},{"name":"eggs","quantity":"2"},{"name":"flour","quantity":"1 cup"},{"name":"olive oil","quantity":"1/2 cup"},{"name":"basil","quantity":"1/4 cup"}]',
'1. Pound chicken breasts to even thickness\n2. Set up breading station: flour, beaten eggs, breadcrumbs mixed with parmesan\n3. Coat each chicken: flour, egg, breadcrumbs\n4. Heat oil in large pan\n5. Fry chicken until golden, about 4 minutes per side\n6. Transfer to baking dish\n7. Top each with marinara sauce\n8. Add mozzarella and more parmesan\n9. Bake at 375°F for 15 minutes until cheese melts\n10. Garnish with fresh basil and serve',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_025', 'French Toast', 'Classic cinnamon French toast', 10, 15, 4, 'Easy', 'French', 'Vegetarian',
'[{"name":"bread","quantity":"8 slices"},{"name":"eggs","quantity":"4"},{"name":"milk","quantity":"1/2 cup"},{"name":"cinnamon","quantity":"2 tsp"},{"name":"vanilla extract","quantity":"2 tsp"},{"name":"butter","quantity":"4 tbsp"},{"name":"maple syrup","quantity":"for serving"},{"name":"sugar","quantity":"2 tbsp"}]',
'1. Whisk together eggs, milk, cinnamon, vanilla, and sugar\n2. Heat butter in a large skillet over medium heat\n3. Dip bread slices in egg mixture, coating both sides\n4. Let excess drip off\n5. Place in hot pan\n6. Cook until golden brown, about 3 minutes per side\n7. Work in batches, adding more butter as needed\n8. Keep finished toast warm in oven\n9. Serve hot with maple syrup\n10. Optional toppings: fresh berries, whipped cream, powdered sugar',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_026', 'Chicken Noodle Soup', 'Comforting homemade chicken soup', 15, 45, 6, 'Medium', 'American', NULL,
'[{"name":"chicken breast","quantity":"1 lb"},{"name":"egg noodles","quantity":"2 cups"},{"name":"carrot","quantity":"3"},{"name":"celery","quantity":"3 stalks"},{"name":"onion","quantity":"1"},{"name":"garlic","quantity":"4 cloves"},{"name":"chicken broth","quantity":"8 cups"},{"name":"bay leaf","quantity":"2"},{"name":"thyme","quantity":"1 tsp"}]',
'1. Cut chicken into bite-sized pieces\n2. Dice carrots, celery, and onion\n3. Heat oil in large pot\n4. Sauté vegetables until soft, about 5 minutes\n5. Add minced garlic, cook 1 minute\n6. Add chicken and cook until no longer pink\n7. Pour in chicken broth\n8. Add bay leaves and thyme\n9. Bring to a boil, add noodles\n10. Simmer 10 minutes until noodles are tender, season and serve',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_027', 'Vegetable Stir Fry', 'Colorful vegetable stir fry', 15, 10, 4, 'Easy', 'Chinese', 'Vegan',
'[{"name":"broccoli","quantity":"2 cups"},{"name":"bell pepper","quantity":"2"},{"name":"carrot","quantity":"2"},{"name":"snow peas","quantity":"1 cup"},{"name":"mushroom","quantity":"1 cup"},{"name":"garlic","quantity":"4 cloves"},{"name":"ginger","quantity":"1 inch"},{"name":"soy sauce","quantity":"3 tbsp"},{"name":"sesame oil","quantity":"2 tsp"},{"name":"vegetable oil","quantity":"3 tbsp"}]',
'1. Cut all vegetables into uniform bite-sized pieces\n2. Heat wok or large pan over high heat\n3. Add oil and swirl to coat\n4. Add hardest vegetables first (broccoli, carrots)\n5. Stir-fry for 2-3 minutes\n6. Add remaining vegetables\n7. Add minced garlic and ginger\n8. Continue stir-frying for 3-4 minutes\n9. Add soy sauce and sesame oil\n10. Toss to coat, serve immediately over rice',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_028', 'Tuna Salad', 'Classic tuna salad for sandwiches', 10, 0, 4, 'Easy', 'American', NULL,
'[{"name":"tuna","quantity":"3 cans"},{"name":"mayonnaise","quantity":"1/2 cup"},{"name":"celery","quantity":"2 stalks"},{"name":"onion","quantity":"1/4"},{"name":"lemon juice","quantity":"2 tbsp"},{"name":"salt","quantity":"to taste"},{"name":"black pepper","quantity":"to taste"}]',
'1. Drain tuna well\n2. Flake tuna into a bowl with a fork\n3. Finely dice celery and red onion\n4. Add vegetables to tuna\n5. Add mayonnaise and lemon juice\n6. Mix well until combined\n7. Season with salt and pepper to taste\n8. Refrigerate for 30 minutes for flavors to meld\n9. Serve on bread, crackers, or lettuce\n10. Store in refrigerator up to 3 days',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_029', 'Pancakes', 'Fluffy buttermilk pancakes', 10, 15, 6, 'Easy', 'American', 'Vegetarian',
'[{"name":"flour","quantity":"2 cups"},{"name":"milk","quantity":"2 cups"},{"name":"eggs","quantity":"2"},{"name":"butter","quantity":"4 tbsp melted"},{"name":"sugar","quantity":"2 tbsp"},{"name":"baking powder","quantity":"2 tsp"},{"name":"salt","quantity":"1/2 tsp"},{"name":"vanilla extract","quantity":"1 tsp"}]',
'1. Mix dry ingredients: flour, sugar, baking powder, salt\n2. In separate bowl, whisk eggs, milk, melted butter, and vanilla\n3. Pour wet ingredients into dry\n4. Mix just until combined (lumps are okay)\n5. Heat griddle or pan over medium heat\n6. Grease lightly with butter\n7. Pour 1/4 cup batter for each pancake\n8. Cook until bubbles form on surface\n9. Flip and cook until golden brown\n10. Serve with butter and maple syrup',
TRUE, UNIX_TIMESTAMP() * 1000, NULL),

('recipe_030', 'Spaghetti Carbonara', 'Classic Roman pasta with eggs and bacon', 10, 20, 4, 'Medium', 'Italian', NULL,
'[{"name":"spaghetti","quantity":"1 lb"},{"name":"bacon","quantity":"8 strips"},{"name":"eggs","quantity":"4"},{"name":"parmesan cheese","quantity":"1 cup"},{"name":"garlic","quantity":"3 cloves"},{"name":"black pepper","quantity":"2 tsp"},{"name":"salt","quantity":"to taste"}]',
'1. Cook spaghetti according to package directions, reserve 1 cup pasta water\n2. Cut bacon into small pieces\n3. Cook bacon until crispy, remove and set aside\n4. Add minced garlic to bacon fat, sauté briefly\n5. In a bowl, whisk eggs, parmesan, and lots of black pepper\n6. Drain pasta and add to pan with garlic\n7. Remove from heat\n8. Quickly add egg mixture and toss\n9. Add pasta water a little at a time to create creamy sauce\n10. Add bacon, toss, and serve immediately with extra parmesan',
TRUE, UNIX_TIMESTAMP() * 1000, NULL);

