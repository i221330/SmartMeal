<?php
/**
 * Recipes API with Smart Matching
 * Handles recipe operations and pantry-based matching
 */

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

$method = $_SERVER['REQUEST_METHOD'];
$action = isset($_GET['action']) ? $_GET['action'] : '';

switch($method) {
    case 'GET':
        if ($action == 'suggestions' && isset($_GET['user_id'])) {
            getRecipeSuggestions($db, $_GET['user_id']);
        } elseif ($action == 'detail' && isset($_GET['recipe_id'])) {
            getRecipeDetail($db, $_GET['recipe_id']);
        } elseif ($action == 'search' && isset($_GET['query'])) {
            searchRecipes($db, $_GET['query']);
        } else {
            getAllRecipes($db);
        }
        break;

    case 'POST':
        if ($action == 'mark_made') {
            markRecipeAsMade($db);
        }
        break;

    default:
        http_response_code(405);
        echo json_encode(["message" => "Method not allowed"]);
        break;
}

function getAllRecipes($db) {
    try {
        $query = "SELECT * FROM recipes WHERE is_global = TRUE ORDER BY title";
        $stmt = $db->prepare($query);
        $stmt->execute();

        $recipes = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Parse ingredients JSON for each recipe
        foreach ($recipes as &$recipe) {
            $recipe['ingredients'] = json_decode($recipe['ingredients'], true);
            $recipe['ingredient_count'] = count($recipe['ingredients']);
        }

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "count" => count($recipes),
            "data" => $recipes
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching recipes: " . $e->getMessage()
        ]);
    }
}

function getRecipeDetail($db, $recipeId) {
    try {
        $query = "SELECT * FROM recipes WHERE recipe_id = :recipe_id LIMIT 1";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":recipe_id", $recipeId);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            $recipe = $stmt->fetch(PDO::FETCH_ASSOC);
            $recipe['ingredients'] = json_decode($recipe['ingredients'], true);

            http_response_code(200);
            echo json_encode([
                "success" => true,
                "data" => $recipe
            ]);
        } else {
            http_response_code(404);
            echo json_encode([
                "success" => false,
                "message" => "Recipe not found"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching recipe: " . $e->getMessage()
        ]);
    }
}

function getRecipeSuggestions($db, $userId) {
    try {
        // Get user's pantry items
        $pantryQuery = "SELECT LOWER(TRIM(name)) as name FROM pantry_items
                        WHERE user_id = :user_id AND is_deleted = FALSE";
        $pantryStmt = $db->prepare($pantryQuery);
        $pantryStmt->bindParam(":user_id", $userId);
        $pantryStmt->execute();
        $pantryItems = $pantryStmt->fetchAll(PDO::FETCH_COLUMN);

        // Get all recipes
        $recipeQuery = "SELECT * FROM recipes WHERE is_global = TRUE ORDER BY title";
        $recipeStmt = $db->prepare($recipeQuery);
        $recipeStmt->execute();
        $recipes = $recipeStmt->fetchAll(PDO::FETCH_ASSOC);

        $suggestions = [];

        foreach ($recipes as $recipe) {
            $recipeIngredients = json_decode($recipe['ingredients'], true);
            $totalIngredients = count($recipeIngredients);
            $matchedIngredients = 0;
            $missingIngredients = [];

            foreach ($recipeIngredients as $ingredient) {
                $ingredientName = strtolower(trim($ingredient['name']));

                // Check if ingredient is in pantry
                $found = false;
                foreach ($pantryItems as $pantryItem) {
                    if (strpos($ingredientName, $pantryItem) !== false ||
                        strpos($pantryItem, $ingredientName) !== false) {
                        $found = true;
                        break;
                    }
                }

                if ($found) {
                    $matchedIngredients++;
                } else {
                    $missingIngredients[] = $ingredient;
                }
            }

            // Calculate match percentage
            $matchPercentage = $totalIngredients > 0 ?
                              round(($matchedIngredients / $totalIngredients) * 100) : 0;

            $suggestions[] = [
                'recipe_id' => $recipe['recipe_id'],
                'title' => $recipe['title'],
                'description' => $recipe['description'],
                'image_url' => $recipe['image_url'],
                'prep_time' => $recipe['prep_time'],
                'cook_time' => $recipe['cook_time'],
                'servings' => $recipe['servings'],
                'difficulty' => $recipe['difficulty'],
                'cuisine' => $recipe['cuisine'],
                'total_ingredients' => $totalIngredients,
                'matched_ingredients' => $matchedIngredients,
                'missing_ingredients_count' => count($missingIngredients),
                'missing_ingredients' => $missingIngredients,
                'match_percentage' => $matchPercentage,
                'can_make_now' => $matchPercentage == 100
            ];
        }

        // Sort by match percentage (highest first), then by fewest total ingredients
        usort($suggestions, function($a, $b) {
            if ($a['match_percentage'] == $b['match_percentage']) {
                return $a['total_ingredients'] - $b['total_ingredients'];
            }
            return $b['match_percentage'] - $a['match_percentage'];
        });

        // Return top 5
        $topSuggestions = array_slice($suggestions, 0, 5);

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "user_id" => $userId,
            "pantry_items_count" => count($pantryItems),
            "suggestions_count" => count($topSuggestions),
            "data" => $topSuggestions
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error generating suggestions: " . $e->getMessage()
        ]);
    }
}

function searchRecipes($db, $searchTerm) {
    try {
        $searchTerm = "%" . strtolower($searchTerm) . "%";

        $query = "SELECT * FROM recipes
                  WHERE is_global = TRUE
                  AND (LOWER(title) LIKE :search
                       OR LOWER(description) LIKE :search
                       OR LOWER(cuisine) LIKE :search
                       OR LOWER(ingredients) LIKE :search)
                  ORDER BY title
                  LIMIT 50";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":search", $searchTerm);
        $stmt->execute();

        $recipes = $stmt->fetchAll(PDO::FETCH_ASSOC);

        foreach ($recipes as &$recipe) {
            $recipe['ingredients'] = json_decode($recipe['ingredients'], true);
            $recipe['ingredient_count'] = count($recipe['ingredients']);
        }

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "count" => count($recipes),
            "data" => $recipes
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error searching recipes: " . $e->getMessage()
        ]);
    }
}

function markRecipeAsMade($db) {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->user_id) || !isset($data->recipe_id)) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields: user_id, recipe_id"
        ]);
        return;
    }

    try {
        // Record in history
        $query = "INSERT INTO recipe_made_history (user_id, recipe_id, servings_made)
                  VALUES (:user_id, :recipe_id, :servings)";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $data->user_id);
        $stmt->bindParam(":recipe_id", $data->recipe_id);
        $servings = isset($data->servings_made) ? $data->servings_made : 1;
        $stmt->bindParam(":servings", $servings);

        if ($stmt->execute()) {
            // Get recipe ingredients to subtract from pantry
            $recipeQuery = "SELECT ingredients FROM recipes WHERE recipe_id = :recipe_id LIMIT 1";
            $recipeStmt = $db->prepare($recipeQuery);
            $recipeStmt->bindParam(":recipe_id", $data->recipe_id);
            $recipeStmt->execute();
            $recipe = $recipeStmt->fetch(PDO::FETCH_ASSOC);

            $ingredientsSubtracted = [];

            if ($recipe) {
                $ingredients = json_decode($recipe['ingredients'], true);

                // For each ingredient, try to find and update in pantry
                // Note: This is simplified - real quantity tracking would need more complex logic
                foreach ($ingredients as $ingredient) {
                    $ingredientName = $ingredient['name'];

                    // Try to find in pantry (loose matching)
                    $pantryQuery = "SELECT id, name, quantity FROM pantry_items
                                   WHERE user_id = :user_id
                                   AND LOWER(name) LIKE :name
                                   AND is_deleted = FALSE
                                   LIMIT 1";
                    $pantryStmt = $db->prepare($pantryQuery);
                    $pantryStmt->bindParam(":user_id", $data->user_id);
                    $searchName = "%" . strtolower($ingredientName) . "%";
                    $pantryStmt->bindParam(":name", $searchName);
                    $pantryStmt->execute();

                    if ($pantryStmt->rowCount() > 0) {
                        $pantryItem = $pantryStmt->fetch(PDO::FETCH_ASSOC);
                        $ingredientsSubtracted[] = $pantryItem['name'];

                        // Mark as updated (simplified - not actually changing quantity in this version)
                        // In production, you'd parse and decrease the quantity
                        $updateQuery = "UPDATE pantry_items
                                       SET last_updated = :timestamp
                                       WHERE id = :id";
                        $updateStmt = $db->prepare($updateQuery);
                        $timestamp = round(microtime(true) * 1000);
                        $updateStmt->bindParam(":timestamp", $timestamp);
                        $updateStmt->bindParam(":id", $pantryItem['id']);
                        $updateStmt->execute();
                    }
                }
            }

            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Recipe marked as made",
                "ingredients_used" => $ingredientsSubtracted
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to record recipe"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error marking recipe as made: " . $e->getMessage()
        ]);
    }
}
?>

