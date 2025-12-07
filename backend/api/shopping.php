<?php
/**
 * Shopping List API - Enhanced with Auto-Add and Pantry Integration
 * Handles shopping list CRUD with intelligent pantry checking
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
        if (isset($_GET['user_id'])) {
            getUserShoppingList($db, $_GET['user_id']);
        }
        break;

    case 'POST':
        if ($action == 'add_from_recipe') {
            addIngredientsFromRecipe($db);
        } elseif ($action == 'move_to_pantry') {
            moveItemToPantry($db);
        } else {
            addShoppingItem($db);
        }
        break;

    case 'PUT':
        updateShoppingItem($db);
        break;

    case 'DELETE':
        if (isset($_GET['item_id'])) {
            deleteShoppingItem($db, $_GET['item_id']);
        }
        break;

    default:
        http_response_code(405);
        echo json_encode(["message" => "Method not allowed"]);
        break;
}

function getUserShoppingList($db, $userId) {
    try {
        $query = "SELECT * FROM shopping_items
                  WHERE user_id = :user_id AND is_deleted = FALSE
                  ORDER BY category, name";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $userId);
        $stmt->execute();

        $items = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Group by category
        $grouped = [];
        $completed = 0;
        $pending = 0;

        foreach ($items as $item) {
            if ($item['is_completed']) {
                $completed++;
            } else {
                $pending++;
            }

            $category = $item['category'] ?: 'Other';
            if (!isset($grouped[$category])) {
                $grouped[$category] = [];
            }
            $grouped[$category][] = $item;
        }

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "user_id" => $userId,
            "total_items" => count($items),
            "pending_items" => $pending,
            "completed_items" => $completed,
            "data" => $items,
            "grouped_by_category" => $grouped
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching shopping list: " . $e->getMessage()
        ]);
    }
}

function addShoppingItem($db) {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->user_id) || !isset($data->name) || !isset($data->quantity)) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields: user_id, name, quantity"
        ]);
        return;
    }

    try {
        $itemId = "shop_" . $data->user_id . "_" . uniqid();
        $timestamp = round(microtime(true) * 1000);

        $query = "INSERT INTO shopping_items
                  (user_id, item_id, name, quantity, is_completed, added_from_recipe_id, category, last_updated)
                  VALUES (:user_id, :item_id, :name, :quantity, FALSE, :recipe_id, :category, :timestamp)";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $data->user_id);
        $stmt->bindParam(":item_id", $itemId);
        $stmt->bindParam(":name", $data->name);
        $stmt->bindParam(":quantity", $data->quantity);

        $recipeId = isset($data->added_from_recipe_id) ? $data->added_from_recipe_id : null;
        $stmt->bindParam(":recipe_id", $recipeId);

        $category = isset($data->category) ? $data->category : null;
        $stmt->bindParam(":category", $category);

        $stmt->bindParam(":timestamp", $timestamp);

        if ($stmt->execute()) {
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Item added to shopping list",
                "item_id" => $itemId
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to add item"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error adding item: " . $e->getMessage()
        ]);
    }
}

function addIngredientsFromRecipe($db) {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->user_id) || !isset($data->recipe_id) || !isset($data->ingredients)) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields: user_id, recipe_id, ingredients"
        ]);
        return;
    }

    try {
        // Get user's pantry to check what they already have
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

        $timestamp = round(microtime(true) * 1000);
        $addedItems = [];
        $alreadyHaveItems = [];

        // Process each ingredient
        foreach ($data->ingredients as $ingredient) {
            $ingredientName = strtolower(trim($ingredient->name));
            $hasInPantry = false;
            $pantryQuantity = null;

            // Check if user has this ingredient
            foreach ($pantryLookup as $pantryName => $quantity) {
                if (strpos($ingredientName, $pantryName) !== false ||
                    strpos($pantryName, $ingredientName) !== false) {
                    $hasInPantry = true;
                    $pantryQuantity = $quantity;
                    break;
                }
            }

            // Determine quantity to add to shopping list
            $quantityToAdd = $ingredient->quantity;
            $status = 'need_to_buy';

            if ($hasInPantry) {
                // User confirmed they want to buy full amount or remaining amount
                if (isset($ingredient->buy_full_amount) && $ingredient->buy_full_amount) {
                    $quantityToAdd = $ingredient->quantity;
                    $status = 'buying_full_amount';
                } else {
                    // They have some, buying remaining
                    // In real implementation, you'd calculate remaining amount
                    // For now, we mark it differently
                    $status = 'have_some_buying_rest';
                    if (isset($ingredient->confirmed_quantity)) {
                        $quantityToAdd = $ingredient->confirmed_quantity;
                    }
                }
            }

            // Add to shopping list
            $itemId = "shop_" . $data->user_id . "_" . uniqid();

            $query = "INSERT INTO shopping_items
                     (user_id, item_id, name, quantity, is_completed, added_from_recipe_id, category, last_updated)
                     VALUES (:user_id, :item_id, :name, :quantity, FALSE, :recipe_id, :category, :timestamp)";

            $stmt = $db->prepare($query);
            $stmt->bindParam(":user_id", $data->user_id);
            $stmt->bindParam(":item_id", $itemId);
            $stmt->bindParam(":name", $ingredient->name);
            $stmt->bindParam(":quantity", $quantityToAdd);
            $stmt->bindParam(":recipe_id", $data->recipe_id);

            $category = isset($ingredient->category) ? $ingredient->category : null;
            $stmt->bindParam(":category", $category);
            $stmt->bindParam(":timestamp", $timestamp);

            if ($stmt->execute()) {
                $addedItems[] = [
                    "item_id" => $itemId,
                    "name" => $ingredient->name,
                    "quantity" => $quantityToAdd,
                    "status" => $status,
                    "has_in_pantry" => $hasInPantry,
                    "pantry_quantity" => $pantryQuantity
                ];
            }
        }

        http_response_code(201);
        echo json_encode([
            "success" => true,
            "message" => "Ingredients added to shopping list",
            "items_added" => count($addedItems),
            "data" => $addedItems
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error adding ingredients: " . $e->getMessage()
        ]);
    }
}

function moveItemToPantry($db) {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->item_id) || !isset($data->user_id) || !isset($data->quantity_bought)) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields: item_id, user_id, quantity_bought"
        ]);
        return;
    }

    try {
        // Get shopping item details
        $query = "SELECT * FROM shopping_items WHERE item_id = :item_id LIMIT 1";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":item_id", $data->item_id);
        $stmt->execute();

        if ($stmt->rowCount() == 0) {
            http_response_code(404);
            echo json_encode([
                "success" => false,
                "message" => "Shopping item not found"
            ]);
            return;
        }

        $shoppingItem = $stmt->fetch(PDO::FETCH_ASSOC);
        $timestamp = round(microtime(true) * 1000);

        // Check if item already exists in pantry (case-insensitive)
        $itemNameLower = strtolower(trim($shoppingItem['name']));
        $checkPantryQuery = "SELECT item_id, quantity FROM pantry_items
                            WHERE user_id = :user_id
                            AND LOWER(TRIM(name)) = :name_lower
                            AND is_deleted = FALSE
                            LIMIT 1";
        $checkPantryStmt = $db->prepare($checkPantryQuery);
        $checkPantryStmt->bindParam(":user_id", $data->user_id);
        $checkPantryStmt->bindParam(":name_lower", $itemNameLower);
        $checkPantryStmt->execute();

        $existingPantryItem = $checkPantryStmt->fetch(PDO::FETCH_ASSOC);

        if ($existingPantryItem) {
            // Item exists in pantry, update quantity by adding
            $existingQty = parseQuantity($existingPantryItem['quantity']);
            $newQty = parseQuantity($data->quantity_bought);

            // Add quantities if units match or one is empty
            if ($existingQty['unit'] === $newQty['unit'] ||
                empty($existingQty['unit']) ||
                empty($newQty['unit'])) {

                $totalValue = $existingQty['value'] + $newQty['value'];
                $unit = $existingQty['unit'] ?: $newQty['unit'];
                $updatedQuantity = $totalValue . ($unit ? ' ' . $unit : '');

                $updatePantryQuery = "UPDATE pantry_items
                                     SET quantity = :quantity,
                                         last_updated = :timestamp
                                     WHERE item_id = :item_id";
                $updatePantryStmt = $db->prepare($updatePantryQuery);
                $updatePantryStmt->bindParam(":quantity", $updatedQuantity);
                $updatePantryStmt->bindParam(":timestamp", $timestamp);
                $updatePantryStmt->bindParam(":item_id", $existingPantryItem['item_id']);
                $updatePantryStmt->execute();

                $pantryItemId = $existingPantryItem['item_id'];
                $actionTaken = "updated";
            } else {
                // Units don't match, create separate entry
                $pantryItemId = insertItemToPantry($db, $data->user_id, $shoppingItem, $data->quantity_bought, $timestamp);
                $actionTaken = "created_separate";
            }
        } else {
            // Item doesn't exist in pantry, add new
            $pantryItemId = insertItemToPantry($db, $data->user_id, $shoppingItem, $data->quantity_bought, $timestamp);
            $actionTaken = "created";
        }

        if ($pantryItemId) {
            // Mark shopping item as completed (bought)
            $updateQuery = "UPDATE shopping_items
                           SET is_completed = TRUE, last_updated = :timestamp
                           WHERE item_id = :item_id";
            $updateStmt = $db->prepare($updateQuery);
            $updateStmt->bindParam(":timestamp", $timestamp);
            $updateStmt->bindParam(":item_id", $data->item_id);
            $updateStmt->execute();

            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Item moved to pantry",
                "pantry_item_id" => $pantryItemId,
                "quantity_added" => $data->quantity_bought,
                "action" => $actionTaken
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to add to pantry"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error moving to pantry: " . $e->getMessage()
        ]);
    }
}

function updateShoppingItem($db) {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->item_id)) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required field: item_id"
        ]);
        return;
    }

    try {
        $timestamp = round(microtime(true) * 1000);
        $updateFields = [];
        $params = [":item_id" => $data->item_id, ":timestamp" => $timestamp];

        if (isset($data->name)) {
            $updateFields[] = "name = :name";
            $params[":name"] = $data->name;
        }

        if (isset($data->quantity)) {
            $updateFields[] = "quantity = :quantity";
            $params[":quantity"] = $data->quantity;
        }

        if (isset($data->is_completed)) {
            $updateFields[] = "is_completed = :is_completed";
            $params[":is_completed"] = $data->is_completed ? 1 : 0;
        }

        if (isset($data->category)) {
            $updateFields[] = "category = :category";
            $params[":category"] = $data->category;
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

        $query = "UPDATE shopping_items SET " . implode(", ", $updateFields) . " WHERE item_id = :item_id";
        $stmt = $db->prepare($query);

        foreach ($params as $key => $value) {
            $stmt->bindValue($key, $value);
        }

        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Item updated"
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to update item"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error updating item: " . $e->getMessage()
        ]);
    }
}

function deleteShoppingItem($db, $itemId) {
    try {
        $timestamp = round(microtime(true) * 1000);

        // Soft delete
        $query = "UPDATE shopping_items
                  SET is_deleted = TRUE, last_updated = :timestamp
                  WHERE item_id = :item_id";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":item_id", $itemId);
        $stmt->bindParam(":timestamp", $timestamp);

        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Item deleted from shopping list"
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to delete item"
            ]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error deleting item: " . $e->getMessage()
        ]);
    }
}

/**
 * Helper function to insert a new item into pantry
 */
function insertItemToPantry($db, $userId, $shoppingItem, $quantityBought, $timestamp) {
    $pantryItemId = "pantry_" . $userId . "_" . uniqid();

    $pantryQuery = "INSERT INTO pantry_items
                   (user_id, item_id, name, quantity, category, last_updated)
                   VALUES (:user_id, :item_id, :name, :quantity, :category, :timestamp)";

    $pantryStmt = $db->prepare($pantryQuery);
    $pantryStmt->bindParam(":user_id", $userId);
    $pantryStmt->bindParam(":item_id", $pantryItemId);
    $pantryStmt->bindParam(":name", $shoppingItem['name']);
    $pantryStmt->bindParam(":quantity", $quantityBought);
    $pantryStmt->bindParam(":category", $shoppingItem['category']);
    $pantryStmt->bindParam(":timestamp", $timestamp);

    if ($pantryStmt->execute()) {
        return $pantryItemId;
    } else {
        return null;
    }
}

/**
 * Parse quantity string to extract numeric value and unit
 * Examples: "2 cups" -> [value: 2, unit: "cups"], "5" -> [value: 5, unit: ""]
 */
function parseQuantity($quantityStr) {
    $quantityStr = trim($quantityStr);

    // Try to match number followed by optional unit
    if (preg_match('/^([\d.]+)\s*(.*)$/', $quantityStr, $matches)) {
        return [
            'value' => floatval($matches[1]),
            'unit' => trim($matches[2])
        ];
    }

    // If parsing fails, treat as 1 with the whole string as unit
    return [
        'value' => 1,
        'unit' => $quantityStr
    ];
}
?>
