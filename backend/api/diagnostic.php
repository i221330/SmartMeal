<?php
/**
 * SmartMeal Backend Diagnostic Test
 * This file helps diagnose backend issues
 */

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json; charset=UTF-8");

$diagnostics = [
    "php_version" => phpversion(),
    "current_directory" => getcwd(),
    "script_path" => __FILE__,
];

// Test 1: Check if database config file exists
$configPath = __DIR__ . '/../config/database.php';
$diagnostics["config_file_path"] = $configPath;
$diagnostics["config_file_exists"] = file_exists($configPath);

if (file_exists($configPath)) {
    // Test 2: Try to include database config
    try {
        include_once $configPath;
        $diagnostics["config_file_loaded"] = true;

        // Test 3: Try to create database connection
        try {
            $database = new Database();
            $diagnostics["database_class_exists"] = true;

            $db = $database->getConnection();
            if ($db) {
                $diagnostics["database_connected"] = true;

                // Test 4: Check if smartmeal_db exists
                $stmt = $db->query("SHOW DATABASES LIKE 'smartmeal_db'");
                $diagnostics["smartmeal_db_exists"] = ($stmt->rowCount() > 0);

                if ($diagnostics["smartmeal_db_exists"]) {
                    // Test 5: Check if users table exists
                    $db->exec("USE smartmeal_db");
                    $stmt = $db->query("SHOW TABLES LIKE 'users'");
                    $diagnostics["users_table_exists"] = ($stmt->rowCount() > 0);

                    if ($diagnostics["users_table_exists"]) {
                        // Test 6: Check if password_hash column exists
                        $stmt = $db->query("DESCRIBE users");
                        $columns = $stmt->fetchAll(PDO::FETCH_COLUMN);
                        $diagnostics["password_hash_column_exists"] = in_array('password_hash', $columns);
                        $diagnostics["users_columns"] = $columns;
                    }
                }
            } else {
                $diagnostics["database_connected"] = false;
                $diagnostics["error"] = "Database connection returned null";
            }
        } catch (Exception $e) {
            $diagnostics["database_connected"] = false;
            $diagnostics["database_error"] = $e->getMessage();
        }
    } catch (Exception $e) {
        $diagnostics["config_file_loaded"] = false;
        $diagnostics["config_load_error"] = $e->getMessage();
    }
} else {
    $diagnostics["error"] = "Database config file not found at: " . $configPath;
    $diagnostics["directory_contents"] = scandir(__DIR__ . '/..');
}

// Output results
http_response_code(200);
echo json_encode($diagnostics, JSON_PRETTY_PRINT);
?>

