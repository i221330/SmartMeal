<?php
/**
 * Master Ingredients API
 * Provides predefined ingredients for users to add to pantry
 */

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

$method = $_SERVER['REQUEST_METHOD'];

switch($method) {
    case 'GET':
        if (isset($_GET['search'])) {
            searchIngredients($db, $_GET['search']);
        } elseif (isset($_GET['category'])) {
            getIngredientsByCategory($db, $_GET['category']);
        } else {
            getAllIngredients($db);
        }
        break;

    default:
        http_response_code(405);
        echo json_encode(["message" => "Method not allowed"]);
        break;
}

function getAllIngredients($db) {
    try {
        $query = "SELECT * FROM master_ingredients ORDER BY category, name";
        $stmt = $db->prepare($query);
        $stmt->execute();

        $ingredients = $stmt->fetchAll(PDO::FETCH_ASSOC);

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "count" => count($ingredients),
            "data" => $ingredients
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching ingredients: " . $e->getMessage()
        ]);
    }
}

function searchIngredients($db, $searchTerm) {
    try {
        $searchTerm = "%" . strtolower($searchTerm) . "%";

        $query = "SELECT * FROM master_ingredients
                  WHERE LOWER(name) LIKE :search
                  OR LOWER(searchable_name) LIKE :search
                  ORDER BY name
                  LIMIT 50";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":search", $searchTerm);
        $stmt->execute();

        $ingredients = $stmt->fetchAll(PDO::FETCH_ASSOC);

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "count" => count($ingredients),
            "data" => $ingredients
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error searching ingredients: " . $e->getMessage()
        ]);
    }
}

function getIngredientsByCategory($db, $category) {
    try {
        $query = "SELECT * FROM master_ingredients
                  WHERE category = :category
                  ORDER BY name";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":category", $category);
        $stmt->execute();

        $ingredients = $stmt->fetchAll(PDO::FETCH_ASSOC);

        http_response_code(200);
        echo json_encode([
            "success" => true,
            "category" => $category,
            "count" => count($ingredients),
            "data" => $ingredients
        ]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Error fetching category: " . $e->getMessage()
        ]);
    }
}
?>

