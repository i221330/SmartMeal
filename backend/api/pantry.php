<?php
/**
 * Pantry API - Enhanced with User Pantry Management
 * Handles CRUD operations for user's pantry items
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
            getUserPantry($db, $_GET['user_id']);
        }
        break;

    case 'POST':
        addPantryItem($db);
        break;

    case 'PUT':
        updatePantryItem($db);
        break;

    case 'DELETE':
        if (isset($_GET['item_id'])) {
            deletePantryItem($db, $_GET['item_id']);
        }
        break;

    default:
        http_response_code(405);
        echo json_encode(["message" => "Method not allowed"]);
        break;
}

function getUserPantry($db, $userId) {
    try {
        $query = "SELECT * FROM pantry_items
                  WHERE user_id = :user_id AND is_deleted = FALSE
                  ORDER BY category, name";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $userId);
        $stmt->execute();

        $items = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Group by category for easier display
        $grouped = [];
        foreach ($items as $item) {
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
            "data" => $items,
            "grouped_by_category" => $grouped
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching pantry: " . $e->getMessage()
        ]);
    }
}

function addPantryItem($db) {
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
        // Generate unique item_id
        $itemId = "pantry_" . $data->user_id . "_" . uniqid();
        $timestamp = round(microtime(true) * 1000);

        $query = "INSERT INTO pantry_items
                  (user_id, item_id, name, quantity, image_url, category, expiry_date, last_updated)
                  VALUES (:user_id, :item_id, :name, :quantity, :image_url, :category, :expiry_date, :timestamp)";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $data->user_id);
        $stmt->bindParam(":item_id", $itemId);
        $stmt->bindParam(":name", $data->name);
        $stmt->bindParam(":quantity", $data->quantity);

        $imageUrl = isset($data->image_url) ? $data->image_url : null;
        $stmt->bindParam(":image_url", $imageUrl);

        $category = isset($data->category) ? $data->category : null;
        $stmt->bindParam(":category", $category);

        $expiryDate = isset($data->expiry_date) ? $data->expiry_date : null;
        $stmt->bindParam(":expiry_date", $expiryDate);

        $stmt->bindParam(":timestamp", $timestamp);

        if ($stmt->execute()) {
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Item added to pantry",
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

function updatePantryItem($db) {
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

        // Build dynamic update query
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

        if (isset($data->category)) {
            $updateFields[] = "category = :category";
            $params[":category"] = $data->category;
        }

        if (isset($data->expiry_date)) {
            $updateFields[] = "expiry_date = :expiry_date";
            $params[":expiry_date"] = $data->expiry_date;
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

        $query = "UPDATE pantry_items SET " . implode(", ", $updateFields) . " WHERE item_id = :item_id";

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

function deletePantryItem($db, $itemId) {
    try {
        // Soft delete
        $timestamp = round(microtime(true) * 1000);

        $query = "UPDATE pantry_items
                  SET is_deleted = TRUE, last_updated = :timestamp
                  WHERE item_id = :item_id";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":item_id", $itemId);
        $stmt->bindParam(":timestamp", $timestamp);

        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Item deleted from pantry"
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
?>

