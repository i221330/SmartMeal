<?php
/**
 * User API Endpoints - CLEAN VERSION
 * Handles user registration and profile management
 */

// Force error display for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);
ini_set('log_errors', 1);

// Start output buffering to catch any errors
ob_start();

try {
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    header("Access-Control-Allow-Methods: POST, GET, PUT");
    header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

    // Debug logging
    error_log("users.php: Script started");

    // Include database config
    if (!file_exists('../config/database.php')) {
        throw new Exception("Database configuration file not found");
    }

    include_once '../config/database.php';
    error_log("users.php: Database config loaded");

    // Create database connection
    $database = new Database();
    $db = $database->getConnection();

    if (!$db) {
        throw new Exception("Database connection failed");
    }

    error_log("users.php: Database connected");

    $method = $_SERVER['REQUEST_METHOD'];
    $action = isset($_GET['action']) ? $_GET['action'] : '';

    error_log("users.php: Method=$method, Action=$action");

    switch($method) {
        case 'POST':
            if ($action == 'register') {
                registerUser($db);
            } elseif ($action == 'login') {
                loginUser($db);
            } else {
                http_response_code(400);
                echo json_encode(["message" => "Invalid action"]);
            }
            break;

        case 'PUT':
            if ($action == 'profile') {
                updateUserProfile($db);
            } else {
                http_response_code(400);
                echo json_encode(["message" => "Invalid action"]);
            }
            break;

        case 'GET':
            if ($action == 'profile') {
                getUserProfile($db);
            } else {
                // Default API info response
                http_response_code(200);
                echo json_encode([
                    "message" => "SmartMeal User API",
                    "status" => "active",
                    "version" => "1.0",
                    "database_connected" => true,
                    "endpoints" => [
                        "POST ?action=register" => "Register new user",
                        "POST ?action=login" => "Login user",
                        "GET ?action=profile&user_id=xxx" => "Get user profile",
                        "PUT ?action=profile" => "Update user profile"
                    ]
                ]);
            }
            break;

        default:
            http_response_code(405);
            echo json_encode(["message" => "Method not allowed"]);
            break;
    }

} catch (Exception $e) {
    error_log("users.php: ERROR - " . $e->getMessage());

    http_response_code(500);
    echo json_encode([
        "error" => true,
        "message" => $e->getMessage(),
        "file" => basename($e->getFile()),
        "line" => $e->getLine()
    ]);
}

ob_end_flush();

// ============ FUNCTIONS ============

function registerUser($db) {
    error_log("registerUser: Function called");

    $data = json_decode(file_get_contents("php://input"));

    error_log("registerUser: Email=" . ($data->email ?? 'not set'));

    if (!isset($data->email) || !isset($data->password) || !isset($data->display_name)) {
        http_response_code(400);
        echo json_encode(["message" => "Missing required fields: email, password, or display_name"]);
        return;
    }

    try {
        // Check if user already exists
        $checkQuery = "SELECT id FROM users WHERE email = :email LIMIT 1";
        $checkStmt = $db->prepare($checkQuery);
        $checkStmt->bindParam(":email", $data->email);
        $checkStmt->execute();

        if ($checkStmt->rowCount() > 0) {
            error_log("registerUser: User already exists");
            http_response_code(409);
            echo json_encode(["message" => "User with this email already exists"]);
            return;
        }

        // Hash password
        $hashedPassword = password_hash($data->password, PASSWORD_DEFAULT);
        $userId = uniqid('user_', true);

        error_log("registerUser: Creating user with ID: $userId");

        // Insert user
        $query = "INSERT INTO users (firebase_uid, email, password_hash, display_name, phone_number, profile_image_url, joined_date)
                  VALUES (:uid, :email, :password, :display_name, :phone, :image, NOW())";

        $stmt = $db->prepare($query);
        $stmt->bindParam(":uid", $userId);
        $stmt->bindParam(":email", $data->email);
        $stmt->bindParam(":password", $hashedPassword);
        $stmt->bindParam(":display_name", $data->display_name);

        $phone = isset($data->phone_number) ? $data->phone_number : null;
        $stmt->bindParam(":phone", $phone);

        $image = isset($data->profile_image_url) ? $data->profile_image_url : null;
        $stmt->bindParam(":image", $image);

        if ($stmt->execute()) {
            error_log("registerUser: User created successfully");
            http_response_code(201);
            echo json_encode([
                "message" => "User registered successfully",
                "user_id" => $userId,
                "email" => $data->email,
                "display_name" => $data->display_name
            ]);
        } else {
            error_log("registerUser: Execute failed");
            http_response_code(500);
            echo json_encode(["message" => "Unable to register user"]);
        }
    } catch (Exception $e) {
        error_log("registerUser: Exception - " . $e->getMessage());
        http_response_code(500);
        echo json_encode(["message" => "Registration error: " . $e->getMessage()]);
    }
}

function loginUser($db) {
    error_log("loginUser: Function called");

    $data = json_decode(file_get_contents("php://input"));

    error_log("loginUser: Email=" . ($data->email ?? 'not set'));

    if (!isset($data->email) || !isset($data->password)) {
        http_response_code(400);
        echo json_encode(["message" => "Missing email or password"]);
        return;
    }

    try {
        $query = "SELECT * FROM users WHERE email = :email LIMIT 1";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":email", $data->email);
        $stmt->execute();

        if ($stmt->rowCount() === 0) {
            error_log("loginUser: User not found");
            http_response_code(401);
            echo json_encode(["message" => "Invalid email or password"]);
            return;
        }

        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        // Verify password
        if (password_verify($data->password, $user['password_hash'])) {
            error_log("loginUser: Login successful");
            http_response_code(200);
            echo json_encode([
                "message" => "Login successful",
                "user" => [
                    "id" => $user['id'],
                    "user_id" => $user['firebase_uid'],
                    "email" => $user['email'],
                    "display_name" => $user['display_name'],
                    "phone_number" => $user['phone_number'],
                    "profile_image_url" => $user['profile_image_url'],
                    "is_premium" => (bool)$user['is_premium'],
                    "joined_date" => $user['joined_date']
                ]
            ]);
        } else {
            error_log("loginUser: Invalid password");
            http_response_code(401);
            echo json_encode(["message" => "Invalid email or password"]);
        }
    } catch (Exception $e) {
        error_log("loginUser: Exception - " . $e->getMessage());
        http_response_code(500);
        echo json_encode(["message" => "Login error: " . $e->getMessage()]);
    }
}

function getUserProfile($db) {
    $user_id = isset($_GET['user_id']) ? $_GET['user_id'] : '';

    if (empty($user_id)) {
        http_response_code(400);
        echo json_encode(["message" => "User ID required"]);
        return;
    }

    try {
        $query = "SELECT * FROM users WHERE firebase_uid = :user_id LIMIT 1";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $user_id);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            http_response_code(200);
            echo json_encode($row);
        } else {
            http_response_code(404);
            echo json_encode(["message" => "User not found"]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(["message" => "Error: " . $e->getMessage()]);
    }
}

function updateUserProfile($db) {
    error_log("═══════════════════════════════════════");
    error_log("updateUserProfile: Function called");

    $data = json_decode(file_get_contents("php://input"));

    error_log("updateUserProfile: Request data received");
    error_log("  - user_id: " . ($data->user_id ?? 'NOT SET'));
    error_log("  - display_name: " . ($data->display_name ?? 'NOT SET'));
    error_log("  - phone_number: " . ($data->phone_number ?? 'NOT SET'));
    error_log("  - profile_image_url: " . ($data->profile_image_url ?? 'NOT SET'));

    if (!isset($data->user_id)) {
        error_log("updateUserProfile: ERROR - user_id not provided");
        http_response_code(400);
        echo json_encode(["message" => "User ID required"]);
        return;
    }

    try {
        // Build dynamic update query based on provided fields
        $updates = [];
        $params = [":user_id" => $data->user_id];

        if (isset($data->display_name)) {
            $updates[] = "display_name = :display_name";
            $params[":display_name"] = $data->display_name;
            error_log("  → Will update display_name");
        }

        if (isset($data->phone_number)) {
            $updates[] = "phone_number = :phone_number";
            $params[":phone_number"] = $data->phone_number;
            error_log("  → Will update phone_number");
        }

        if (isset($data->profile_image_url)) {
            $updates[] = "profile_image_url = :profile_image_url";
            $params[":profile_image_url"] = $data->profile_image_url;
            error_log("  → Will update profile_image_url to: " . $data->profile_image_url);
        }

        if (empty($updates)) {
            error_log("updateUserProfile: ERROR - No fields to update");
            http_response_code(400);
            echo json_encode(["message" => "No fields to update"]);
            return;
        }

        $query = "UPDATE users SET " . implode(", ", $updates) . " WHERE firebase_uid = :user_id";
        error_log("updateUserProfile: SQL Query: $query");

        $stmt = $db->prepare($query);

        foreach ($params as $key => $value) {
            $stmt->bindValue($key, $value);
        }

        if ($stmt->execute()) {
            $rowCount = $stmt->rowCount();
            error_log("updateUserProfile: ✓ SQL Execute successful!");
            error_log("  - Rows affected: $rowCount");

            // Fetch updated user data
            $selectQuery = "SELECT * FROM users WHERE firebase_uid = :user_id LIMIT 1";
            $selectStmt = $db->prepare($selectQuery);
            $selectStmt->bindParam(":user_id", $data->user_id);
            $selectStmt->execute();

            if ($selectStmt->rowCount() > 0) {
                $user = $selectStmt->fetch(PDO::FETCH_ASSOC);
                error_log("updateUserProfile: ✓ User data fetched");
                error_log("  - profile_image_url in DB: " . ($user['profile_image_url'] ?? 'NULL'));
                error_log("═══════════════════════════════════════");

                http_response_code(200);
                echo json_encode([
                    "message" => "Profile updated successfully",
                    "user" => [
                        "user_id" => $user['firebase_uid'],
                        "email" => $user['email'],
                        "display_name" => $user['display_name'],
                        "phone_number" => $user['phone_number'],
                        "profile_image_url" => $user['profile_image_url'],
                        "is_premium" => (bool)$user['is_premium'],
                        "joined_date" => $user['joined_date']
                    ]
                ]);
            } else {
                error_log("updateUserProfile: WARNING - User not found after update");
                error_log("═══════════════════════════════════════");
                http_response_code(200);
                echo json_encode(["message" => "Profile updated successfully"]);
            }
        } else {
            error_log("updateUserProfile: ✗ SQL Execute failed");
            error_log("  - Error info: " . print_r($stmt->errorInfo(), true));
            error_log("═══════════════════════════════════════");
            http_response_code(500);
            echo json_encode(["message" => "Unable to update profile"]);
        }
    } catch (Exception $e) {
        error_log("updateUserProfile: ✗ Exception - " . $e->getMessage());
        error_log("  - File: " . $e->getFile());
        error_log("  - Line: " . $e->getLine());
        error_log("═══════════════════════════════════════");
        http_response_code(500);
        echo json_encode(["message" => "Update error: " . $e->getMessage()]);
    }
}
?>

