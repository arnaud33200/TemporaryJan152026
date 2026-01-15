# Gradle Wrapper Setup

## Important Note

The `gradle-wrapper.jar` file will be automatically generated when you first open this project in Android Studio.

## If Android Studio doesn't generate it automatically:

1. Open the project in Android Studio
2. Click on "File" -> "Sync Project with Gradle Files"
3. Or run in terminal: `gradle wrapper --gradle-version 8.0`

## Alternative: Manual Setup

If you need to build from command line before opening in Android Studio:

```bash
# Install Gradle 8.0 manually
sdk install gradle 8.0

# Or use your system's Gradle
gradle wrapper --gradle-version 8.0
```

## What Android Studio will do automatically:

1. Download Gradle 8.0
2. Generate gradle-wrapper.jar
3. Setup the wrapper scripts
4. Sync all dependencies

**You don't need to worry about this - just open the project in Android Studio and it will handle everything.**
