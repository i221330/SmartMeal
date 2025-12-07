<?php
/**
 * Meal Planner API - With Auto Shopping List Generation
 * Handles meal planning with intelligent ingredient management
 */

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

$method = $_SERVER['REQUEST_METHOD'];
$action = isset($_GET['action']) ? $_GET['action'] : '';

switch($method) {
    case 'GET':
        if (isset($_GET['user_id']) && isset($_GET['date'])) {
            getMealsForDate($db, $_GET['user_id'], $_GET['date']);
        } elseif (isset($_GET['user_id']) && isset($_GET['start_date']) && isset($_GET['end_date'])) {
            getMealsForWeek($db, $_GET['user_id'], $_GET['start_date'], $_GET['end_date']);
        } elseif (isset($_GET['user_id'])) {
            getAllUserMeals($db, $_GET['user_id']);
        }
        break;

    case 'POST':
        addMealToPlan($db);
        break;

    case 'PUT':
        updateMeal($db);
        break;

    case 'DELETE':
        if (isset($_GET['plan_id'])) {
            deleteMeal($db, $_GET['plan_id']);
        }
        break;

    default:
        http_response_code(405);
        echo json_encode(["message" => "Method not allowed"]);
        break;
}

function getMealsForDate($db, $userId, $date) {
    try {
        // Convert date string to timestamp
        $dateTimestamp = strtotime($date) * 1000;
        $dayStart = $dateTimestamp;
        $dayEnd = $dayStart + (24 * 60 * 60 * 1000); // Add 24 hours

        $query = "SELECT * FROM meal_plans
                  WHERE user_id = :user_id
                  AND meal_date >= :day_start
                  AND meal_date < :day_end
                  AND is_deleted = FALSE
                  ORDER BY FIELD(meal_type, 'breakfast', 'lunch', 'dinner')";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $userId);
        $stmt->bindParam(":day_start", $dayStart);
        $stmt->bindParam(":day_end", $dayEnd);
        $stmt->execute();

        $meals = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Organize by meal type
        $organized = [
            "breakfast" => null,
            "lunch" => null,
            "dinner" => null
        ];

        foreach ($meals as $meal) {
            $organized[$meal['meal_type']] = $meal;
        }

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "user_id" => $userId,
            "date" => $date,
            "meals_count" => count($meals),
            "meals" => $organized,
            "all_meals" => $meals
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching meals: " . $e->getMessage()
        ]);
    }
}

function getMealsForWeek($db, $userId, $startDate, $endDate) {
    try {
        $startTimestamp = strtotime($startDate) * 1000;
        $endTimestamp = strtotime($endDate) * 1000 + (24 * 60 * 60 * 1000);

        $query = "SELECT * FROM meal_plans
                  WHERE user_id = :user_id
                  AND meal_date >= :start_date
                  AND meal_date < :end_date
                  AND is_deleted = FALSE
                  ORDER BY meal_date, FIELD(meal_type, 'breakfast', 'lunch', 'dinner')";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $userId);
        $stmt->bindParam(":start_date", $startTimestamp);
        $stmt->bindParam(":end_date", $endTimestamp);
        $stmt->execute();

        $meals = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Group by date
        $grouped = [];
        foreach ($meals as $meal) {
            $mealDate = date('Y-m-d', $meal['meal_date'] / 1000);
            if (!isset($grouped[$mealDate])) {
                $grouped[$mealDate] = [
                    "breakfast" => null,
                    "lunch" => null,
                    "dinner" => null
                ];
            }
            $grouped[$mealDate][$meal['meal_type']] = $meal;
        }

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "user_id" => $userId,
            "start_date" => $startDate,
            "end_date" => $endDate,
            "total_meals" => count($meals),
            "grouped_by_date" => $grouped,
            "all_meals" => $meals
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching week meals: " . $e->getMessage()
        ]);
    }
}

function getAllUserMeals($db, $userId) {
    try {
        $query = "SELECT * FROM meal_plans
                  WHERE user_id = :user_id AND is_deleted = FALSE
                  ORDER BY meal_date DESC, FIELD(meal_type, 'breakfast', 'lunch', 'dinner')
                  LIMIT 100";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $userId);
        $stmt->execute();

        $meals = $stmt->fetchAll(PDO::FETCH_ASSOC);

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "user_id" => $userId,
            "total_meals" => count($meals),
            "data" => $meals
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching meals: " . $e->getMessage()
        ]);
    }
}

function addMealToPlan($db) {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->user_id) || !isset($data->recipe_id) || !isset($data->meal_date) || !isset($data->meal_type)) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields: user_id, recipe_id, meal_date, meal_type"
        ]);
        return;
    }

    try {
        // Get recipe details
        $recipeQuery = "SELECT recipe_id, title, image_url, ingredients FROM recipes
                       WHERE recipe_id = :recipe_id LIMIT 1";
        $recipeStmt = $db->prepare($recipeQuery);
        $recipeStmt->bindParam(":recipe_id", $data->recipe_id);
        $recipeStmt->execute();

        if ($recipeStmt->rowCount() == 0) {
            http_response_code(404);
            echo json_encode([
                "success" => false,
                "message" => "Recipe not found"
            ]);
            return;
        }

        $recipe = $recipeStmt->fetch(PDO::FETCH_ASSOC);
        $ingredients = json_decode($recipe['ingredients'], true);

        // Get user's pantry
        $pantryQuery = "SELECT LOWER(TRIM(name)) as name, quantity FROM pantry_items
                       WHERE user_id = :user_id AND is_deleted = FALSE";
        $pantryStmt = $db->prepare($pantryQuery);
        $pantryStmt->bindParam(":user_id", $data->user_id);
        $pantryStmt->execute();
        $pantryItems = $pantryStmt->fetchAll(PDO::FETCH_ASSOC);

        // Create pantry lookup
        $pantryLookup = [];
        foreach ($pantryItems as $item) {
            $pantryLookup[$item['name']] = $item['quantity'];
        }

        // Check which ingredients are missing
        $ingredientsForShopping = [];

        foreach ($ingredients as $ingredient) {
            $ingredientName = strtolower(trim($ingredient['name']));
            $hasInPantry = false;
            $pantryQuantity = null;

            foreach ($pantryLookup as $pantryName => $quantity) {
                if (strpos($ingredientName, $pantryName) !== false ||
                    strpos($pantryName, $ingredientName) !== false) {
                    $hasInPantry = true;
                    $pantryQuantity = $quantity;
                    break;
                }
            }

            // Add ALL ingredients with "already have" flag
            $ingredientsForShopping[] = [
                "name" => $ingredient['name'],
                "quantity" => $ingredient['quantity'],
                "has_in_pantry" => $hasInPantry,
                "pantry_quantity" => $pantryQuantity,
                "need_to_buy" => !$hasInPantry
            ];
        }

        // Convert meal date to timestamp
        $mealTimestamp = strtotime($data->meal_date) * 1000;
        $planId = "plan_" . $data->user_id . "_" . uniqid();
        $timestamp = round(microtime(true) * 1000);

        // Add meal to plan
        $query = "INSERT INTO meal_plans
                  (user_id, plan_id, recipe_id, recipe_name, recipe_image_url, meal_date, meal_type, notes, last_updated)
                  VALUES (:user_id, :plan_id, :recipe_id, :recipe_name, :recipe_image, :meal_date, :meal_type, :notes, :timestamp)";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $data->user_id);
        $stmt->bindParam(":plan_id", $planId);
        $stmt->bindParam(":recipe_id", $recipe['recipe_id']);
        $stmt->bindParam(":recipe_name", $recipe['title']);
        $stmt->bindParam(":recipe_image", $recipe['image_url']);
        $stmt->bindParam(":meal_date", $mealTimestamp);
        $stmt->bindParam(":meal_type", $data->meal_type);

        $notes = isset($data->notes) ? $data->notes : null;
        $stmt->bindParam(":notes", $notes);

        $stmt->bindParam(":timestamp", $timestamp);

        if ($stmt->execute()) {
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Meal added to plan",
                "data" => [
                    "plan_id" => $planId,
                    "ingredients_info" => [
                        "total_ingredients" => count($ingredientsForShopping),
                        "already_have" => count(array_filter($ingredientsForShopping, function($i) { return $i['has_in_pantry']; })),
                        "need_to_buy" => count(array_filter($ingredientsForShopping, function($i) { return !$i['has_in_pantry']; })),
                        "details" => $ingredientsForShopping
                    ]
                ]
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to add meal"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error adding meal: " . $e->getMessage()
        ]);
    }
}

function updateMeal($db) {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->plan_id)) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required field: plan_id"
        ]);
        return;
    }

    try {
        $timestamp = round(microtime(true) * 1000);
        $updateFields = [];
        $params = [":plan_id" => $data->plan_id, ":timestamp" => $timestamp];

        if (isset($data->meal_date)) {
            $mealTimestamp = strtotime($data->meal_date) * 1000;
            $updateFields[] = "meal_date = :meal_date";
            $params[":meal_date"] = $mealTimestamp;
        }

        if (isset($data->meal_type)) {
            $updateFields[] = "meal_type = :meal_type";
            $params[":meal_type"] = $data->meal_type;
        }

        if (isset($data->notes)) {
            $updateFields[] = "notes = :notes";
            $params[":notes"] = $data->notes;
        }

        if (empty($updateFields)) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "No fields to update"
            ]);
            return;
        }

        $updateFields[] = "last_updated = :timestamp";

        $query = "UPDATE meal_plans SET " . implode(", ", $updateFields) . " WHERE plan_id = :plan_id";
        $stmt = $db->prepare($query);

        foreach ($params as $key => $value) {
            $stmt->bindValue($key, $value);
        }

        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Meal updated"
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to update meal"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error updating meal: " . $e->getMessage()
        ]);
    }
}

function deleteMeal($db, $planId) {
    try {
        $timestamp = round(microtime(true) * 1000);

        // Soft delete
        $query = "UPDATE meal_plans
                  SET is_deleted = TRUE, last_updated = :timestamp
                  WHERE plan_id = :plan_id";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":plan_id", $planId);
        $stmt->bindParam(":timestamp", $timestamp);

        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Meal deleted from plan"
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to delete meal"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error deleting meal: " . $e->getMessage()
        ]);
    }
}
?>

