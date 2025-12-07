# 🚀 GITHUB COMMIT GUIDE - Step by Step

## Complete Guide to Commit Your SmartMeal Project to GitHub

---

## STEP 1: Initialize Git Repository (if not done)

Open **Terminal** and run these commands:

```bash
cd /Users/mac/StudioProjects/SmartMeal

# Initialize git repository
git init

# Check status
git status
```

**What you'll see:** A list of untracked files (red text)

---

## STEP 2: Create .gitignore File

Before committing, create a `.gitignore` file to exclude build files and sensitive data:

```bash
cd /Users/mac/StudioProjects/SmartMeal

# Create .gitignore file
cat > .gitignore << 'EOF'
# Android Studio
.gradle/
.idea/
*.iml
local.properties
.DS_Store
build/
captures/
.externalNativeBuild
.cxx

# Gradle
.gradle
build/
gradle-app.setting
!gradle-wrapper.jar
.gradletasknamecache

# Android
*.apk
*.ap_
*.dex
*.class
bin/
gen/
out/
release/

# Sensitive files
google-services.json
local.properties

# Logs
*.log

# Misc
.navigation/
captures/
output.json
EOF

# Verify it was created
cat .gitignore
```

---

## STEP 3: Add Files to Git

```bash
# Add all files except those in .gitignore
git add .

# Check what will be committed (should show green text)
git status
```

**Expected output:** Files in green = ready to commit

---

## STEP 4: Create Your First Commit

```bash
# Commit with a descriptive message
git commit -m "Initial commit: SmartMeal app with Home and Pantry screens

- Implemented PHP backend with MySQL database
- Home screen with recipe suggestions based on pantry
- Pantry screen with full CRUD operations
- Backend APIs: users, recipes, pantry, shopping, meal_planner
- 30 recipes and 100+ ingredients in database
- Material Design themed UI
- Authentication with signup/login"

# Verify commit was created
git log --oneline -1
```

---

## STEP 5: Create GitHub Repository

### Option A: Using GitHub Website

1. Go to **https://github.com**
2. Log in to your account
3. Click the **"+"** icon (top right) → **"New repository"**
4. Fill in:
   - **Repository name:** `SmartMeal`
   - **Description:** "Android meal planning app with AI recipe suggestions"
   - **Visibility:** Choose Public or Private
   - **DO NOT** check "Initialize with README" (you already have code)
5. Click **"Create repository"**

### Option B: Using GitHub CLI (if installed)

```bash
gh repo create SmartMeal --public --description "Android meal planning app with AI recipe suggestions"
```

---

## STEP 6: Connect Local Repository to GitHub

After creating the GitHub repo, you'll see instructions. Use these commands:

```bash
# Add GitHub as remote origin (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/SmartMeal.git

# Verify remote was added
git remote -v

# Example output:
# origin  https://github.com/YOUR_USERNAME/SmartMeal.git (fetch)
# origin  https://github.com/YOUR_USERNAME/SmartMeal.git (push)
```

---

## STEP 7: Push to GitHub

```bash
# Push to GitHub (main branch)
git push -u origin main

# If you get an error about 'master' branch, rename it:
git branch -M main
git push -u origin main
```

**First time pushing?** You'll be prompted to:
1. Enter your GitHub username
2. Enter your GitHub password (or Personal Access Token)

### If Using Personal Access Token (Recommended):

1. Go to **GitHub.com** → **Settings** → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
2. Click **"Generate new token (classic)"**
3. Give it a name: `SmartMeal-Push`
4. Select scopes: Check **"repo"** (full control of private repositories)
5. Click **"Generate token"**
6. **COPY THE TOKEN** (you won't see it again!)
7. Use this token as your password when pushing

---

## STEP 8: Verify Upload

1. Go to your GitHub repository: `https://github.com/YOUR_USERNAME/SmartMeal`
2. You should see all your files!
3. Check that these folders are there:
   - `app/`
   - `backend/`
   - `gradle/`
   - And all your markdown files

---

## QUICK COMMAND SUMMARY

```bash
# Navigate to project
cd /Users/mac/StudioProjects/SmartMeal

# Initialize (if needed)
git init

# Create .gitignore (copy the content from STEP 2 above)

# Add all files
git add .

# Commit
git commit -m "Initial commit: SmartMeal app with Home and Pantry"

# Add remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/SmartMeal.git

# Push
git branch -M main
git push -u origin main
```

---

## Future Commits (After This Initial Push)

Whenever you make changes:

```bash
# Check what changed
git status

# Add changed files
git add .

# Or add specific files
git add app/src/main/java/com/example/smartmeal/ActivityPantry.kt

# Commit with message
git commit -m "Fixed pantry delete and update operations"

# Push to GitHub
git push
```

---

## Common Issues & Solutions

### Issue 1: "fatal: not a git repository"
```bash
# Solution: Initialize git
git init
```

### Issue 2: "remote origin already exists"
```bash
# Solution: Remove and re-add
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/SmartMeal.git
```

### Issue 3: "rejected - non-fast-forward"
```bash
# Solution: Pull first, then push
git pull origin main --rebase
git push
```

### Issue 4: Authentication failed
```bash
# Solution: Use Personal Access Token instead of password
# Generate token at: GitHub.com → Settings → Developer settings → Personal access tokens
```

---

## What Files Will Be Committed?

✅ **Included:**
- All Kotlin source files (.kt)
- All layout files (.xml)
- Backend PHP files
- Database SQL files
- gradle files
- Markdown documentation files
- AndroidManifest.xml

❌ **Excluded (in .gitignore):**
- build/ folders
- .gradle/
- .idea/ (Android Studio settings)
- *.apk files
- google-services.json (sensitive)
- local.properties (sensitive)

---

## Verification Checklist

After pushing, verify on GitHub that you have:

- [ ] `app/src/main/java/com/example/smartmeal/` folder with all activities
- [ ] `app/src/main/res/layout/` folder with all XML layouts
- [ ] `backend/api/` folder with PHP files
- [ ] `backend/database/` folder with SQL files
- [ ] All documentation markdown files
- [ ] `README.md` or documentation files
- [ ] `build.gradle.kts` files
- [ ] No sensitive files (google-services.json, local.properties)

---

## Next Steps After Successful Push

Once your code is on GitHub:

1. ✅ You have a backup of your work
2. ✅ You can work from any computer
3. ✅ You can share the link with instructors/teammates
4. ✅ You have version history of all changes

**Continue development:**
- Make changes locally
- Test them
- Commit with descriptive messages
- Push to GitHub regularly (daily or after each feature)

---

## 🎯 Ready to Commit?

Follow the steps above in order. If you encounter any errors, check the "Common Issues" section.

**Your repository will be live at:**
`https://github.com/YOUR_USERNAME/SmartMeal`

**Happy coding! 🚀**

