<?php
// Simple test - just output something
error_reporting(E_ALL);
ini_set('display_errors', 1);
header("Content-Type: text/plain");

echo "PHP is working!\n";
echo "PHP Version: " . phpversion() . "\n";
echo "Current directory: " . getcwd() . "\n";
echo "Script location: " . __FILE__ . "\n";

// Try to include database config
$configPath = __DIR__ . '/../config/database.php';
echo "\nLooking for config at: $configPath\n";
echo "Config exists: " . (file_exists($configPath) ? "YES" : "NO") . "\n";

if (file_exists($configPath)) {
    echo "\nAttempting to load database config...\n";
    try {
        include_once $configPath;
        echo "Config loaded successfully!\n";

        echo "\nAttempting database connection...\n";
        $database = new Database();
        $db = $database->getConnection();

        if ($db) {
            echo "Database connected successfully!\n";
        } else {
            echo "Database connection returned null!\n";
        }
    } catch (Exception $e) {
        echo "ERROR: " . $e->getMessage() . "\n";
        echo "File: " . $e->getFile() . "\n";
        echo "Line: " . $e->getLine() . "\n";
    }
} else {
    echo "\nDirectory listing of parent:\n";
    $parent = dirname(__DIR__);
    echo "Parent dir: $parent\n";
    if (is_dir($parent)) {
        $contents = scandir($parent);
        foreach ($contents as $item) {
            echo "  - $item\n";
        }
    }
}
?>

