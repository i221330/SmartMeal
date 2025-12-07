# 🔧 URGENT FIX: 404 Error - Backend Files Not Found

## The Problem:

The app is getting 404 errors because the backend PHP files are not in XAMPP's htdocs folder.

Error: `http://192.168.1.4/smartmeal/backend/api/` returns "Object not found!"

---

## ✅ SOLUTION: Copy Files to XAMPP Manually

### Step 1: Create Directories

Open **Terminal** and run:

```bash
cd /Applications/XAMPP/xamppfiles/htdocs
sudo mkdir -p smartmeal/backend/api
sudo mkdir -p smartmeal/backend/config
sudo mkdir -p smartmeal/backend/database
```

### Step 2: Copy Backend Files

```bash
# Copy config file
sudo cp /Users/mac/StudioProjects/SmartMeal/backend/config/database.php \
  /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/config/

# Copy all API files
sudo cp /Users/mac/StudioProjects/SmartMeal/backend/api/users.php \
  /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/

sudo cp /Users/mac/StudioProjects/SmartMeal/backend/api/ingredients.php \
  /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/

sudo cp /Users/mac/StudioProjects/SmartMeal/backend/api/recipes.php \
  /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/

sudo cp /Users/mac/StudioProjects/SmartMeal/backend/api/pantry.php \
  /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/

sudo cp /Users/mac/StudioProjects/SmartMeal/backend/api/shopping.php \
  /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/

sudo cp /Users/mac/StudioProjects/SmartMeal/backend/api/meal_planner.php \
  /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/
```

### Step 3: Set Permissions

```bash
sudo chmod -R 755 /Applications/XAMPP/xamppfiles/htdocs/smartmeal
sudo chown -R daemon:daemon /Applications/XAMPP/xamppfiles/htdocs/smartmeal
```

### Step 4: Verify Files Exist

```bash
ls -la /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/
```

Should show:
- ingredients.php
- recipes.php
- pantry.php
- shopping.php
- meal_planner.php
- users.php

---

## 🧪 Step 5: Test Backend URLs

Open in browser:

**Test 1: Ingredients API**
```
http://192.168.1.4/smartmeal/backend/api/ingredients.php
```
Should return JSON with ingredients

**Test 2: Recipes API**
```
http://192.168.1.4/smartmeal/backend/api/recipes.php?action=suggestions&user_id=1
```
Should return JSON with recipe suggestions

**Test 3: Meal Planner API**
```
http://192.168.1.4/smartmeal/backend/api/meal_planner.php?user_id=1&date=2025-12-07
```
Should return JSON with meals (empty for new user)

---

## ⚠️ If Still Getting 404:

### Check XAMPP is Running:
1. Open XAMPP Control Panel
2. Make sure **Apache** and **MySQL** are green/running
3. If not, click "Start" for both

### Check Apache Error Log:
```bash
tail -50 /Applications/XAMPP/xamppfiles/logs/error_log
```

### Restart Apache:
In XAMPP Control Panel:
1. Click "Stop" on Apache
2. Wait 2 seconds
3. Click "Start" on Apache

---

## 🔍 Alternative: Use Finder

If terminal commands don't work:

1. Open **Finder**
2. Press **Cmd+Shift+G**
3. Go to: `/Applications/XAMPP/xamppfiles/htdocs`
4. Create folder: `smartmeal`
5. Inside smartmeal, create: `backend`
6. Inside backend, create: `api` and `config`
7. **Manually copy** files from:
   - Source: `/Users/mac/StudioProjects/SmartMeal/backend/api/`
   - Destination: `/Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/`

---

## 📋 Quick Verification Checklist:

After copying files, verify:

- [ ] Directory exists: `/Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/`
- [ ] File exists: `ingredients.php`
- [ ] File exists: `recipes.php`
- [ ] File exists: `meal_planner.php`
- [ ] File exists: `pantry.php`
- [ ] File exists: `shopping.php`
- [ ] File exists: `users.php`
- [ ] Apache is running (green in XAMPP)
- [ ] MySQL is running (green in XAMPP)
- [ ] URL works: `http://192.168.1.4/smartmeal/backend/api/recipes.php`

---

## 🎯 After Files Are Copied:

1. **Restart the app** (rebuild in Android Studio)
2. Open Home screen
3. Check Logcat - should see:
   ```
   HomeRepository: Successfully fetched X suggestions
   ActivityHome: Recipe suggestions loaded: 5 recipes
   ```

---

## 💡 Why This Happened:

The backend files were created in the project folder but never copied to XAMPP's htdocs (the web server's document root). XAMPP can only serve files from `/Applications/XAMPP/xamppfiles/htdocs/`.

**Once files are in htdocs, the 404 error will be resolved!**

---

## ⚡ Quick Test Command:

After copying, test with:
```bash
curl http://192.168.1.4/smartmeal/backend/api/recipes.php?action=suggestions&user_id=1
```

Should return JSON, not HTML 404 error!

---

**Copy the files using the commands above or manually via Finder, then test the URLs in browser!**

