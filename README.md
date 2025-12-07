# SmartMeal - AI-Assisted Recipe & Meal Planner

A comprehensive Android application for recipe management, meal planning, pantry tracking, and intelligent shopping list generation.


## Quick Start

### Prerequisites
- Android Studio (latest version)
- XAMPP (for PHP backend and MySQL)
- Android device or emulator (API 24+)
- Java 17+

### Running the Backend
```bash
1. Install and start XAMPP
2. Start Apache and MySQL
3. Import backend/database/schema.sql to MySQL
4. Place backend folder in htdocs/smartmeal/
5. Access at http://localhost/smartmeal/backend/api/
```

### Running the App
```bash
1. Open project in Android Studio
2. Update BASE_URL in ApiConfig.kt with your server IP
3. Build and run: ./gradlew assembleDebug
4. Install on device or emulator
```

## Features
✅ User authentication (signup/login)  
✅ Pantry management with autocomplete  
✅ Smart shopping list with auto-population  
✅ Weekly meal planner  
✅ 30+ pre-loaded recipes  
✅ Pantry-based recipe suggestions (Wow Factor)  
✅ Offline support with local storage  
✅ Data sync with PHP/MySQL backend  

## Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **Local DB**: Room (SQLite)
- **Network**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **Backend**: PHP 8.2 + MySQL


## License
Academic Project - Software for Mobile Devices Course

## Author
Daniyal Khawar - December 2025

