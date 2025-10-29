#!/bin/bash

# Automated Fix Script for Fludde Android Project Compilation Errors
# Run this from the project root directory

echo "=========================================="
echo "Fludde Build Fixes - Automated Script"
echo "=========================================="
echo ""

# Check if we're in the right directory
if [ ! -d "app/src/main/java/com/example/fludde" ]; then
    echo "❌ Error: Not in the correct directory!"
    echo "Please run this script from the project root (fludde/)"
    exit 1
fi

echo "✓ Found project structure"
echo ""

# Fix 1: Rename Errorhandler.java to ErrorHandler.java
echo "[1/3] Renaming Errorhandler.java to ErrorHandler.java..."
if [ -f "app/src/main/java/com/example/fludde/utils/Errorhandler.java" ]; then
    mv app/src/main/java/com/example/fludde/utils/Errorhandler.java app/src/main/java/com/example/fludde/utils/ErrorHandler.java
    echo "  ✓ File renamed successfully"
else
    echo "  ⚠ Errorhandler.java not found or already renamed"
fi
echo ""

# Fix 2: Add package declaration to User.java
echo "[2/3] Fixing User.java package declaration..."
USER_FILE="app/src/main/java/com/example/fludde/User.java"
if [ -f "$USER_FILE" ]; then
    # Check if package declaration is missing
    if ! grep -q "^package com.example.fludde;" "$USER_FILE"; then
        # Add package declaration at the beginning
        echo "package com.example.fludde;" | cat - "$USER_FILE" > temp && mv temp "$USER_FILE"
        echo "  ✓ Package declaration added"
    else
        echo "  ⚠ Package declaration already exists"
    fi
else
    echo "  ⚠ User.java not found"
fi
echo ""

# Fix 3: Fix ComposeParentFragment container ID
echo "[3/3] Fixing ComposeParentFragment.java container ID..."
COMPOSE_FILE="app/src/main/java/com/example/fludde/fragments/ComposeParentFragment.java"
if [ -f "$COMPOSE_FILE" ]; then
    sed -i.bak 's/R\.id\.flChildContainer/R.id.child_fragment_container/g' "$COMPOSE_FILE"
    rm -f "${COMPOSE_FILE}.bak"
    echo "  ✓ Container ID fixed"
else
    echo "  ⚠ ComposeParentFragment.java not found"
fi
echo ""

echo "=========================================="
echo "Automated fixes complete!"
echo "=========================================="
echo ""
echo "⚠️  MANUAL FIXES STILL REQUIRED:"
echo ""
echo "1. Replace MockData.java with the provided fixed version"
echo "2. Fix adapter files (3 files):"
echo "   - Change getBindingAdapterPosition() to getAdapterPosition()"
echo "3. Fix LoginActivity.java ErrorHandler calls"
echo "4. Fix SignupActivity.java ErrorHandler calls"
echo "5. Fix SearchFragment.java mockUsers() call"
echo ""
echo "See JAVA_COMPILATION_FIXES.md for detailed instructions."
echo ""
echo "After fixing, run:"
echo "  ./gradlew clean"
echo "  ./gradlew build"
echo ""