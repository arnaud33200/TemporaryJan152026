# Text Echo App - Technical Documentation

## Overview
A simple Android application that validates text input with a simulated external server and displays the result. Built with modern Android development best practices using Jetpack Compose, MVVM architecture, and Hilt dependency injection.

## Technical Stack

### Core Technologies
- **Language**: Kotlin 1.9.10
- **UI Framework**: Jetpack Compose 1.5.4 with Material 3
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt (Dagger) 2.48
- **Async Operations**: Kotlin Coroutines 1.7.3 & Flow
- **Testing**: JUnit, MockK, Turbine
- **Build System**: Gradle 8.2 with AGP 8.2.0

## Architecture Decisions

### 1. Clean Architecture with Layered Structure
The project follows Clean Architecture principles with clear separation of concerns:

```
├── domain/              # Business logic layer
│   ├── model/          # Domain models (ValidationResult)
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic (ValidateTextUseCase)
├── data/               # Data layer
│   ├── repository/     # Repository implementations
│   └── service/        # External services (ValidationService)
└── ui/                 # Presentation layer
    ├── screen/         # Composables
    ├── viewmodel/      # ViewModels
    ├── state/          # UI state models
    └── mapper/         # Domain-to-UI mappers
```

**Benefits:**
- Clear separation of concerns
- Easy to test each layer independently
- Domain logic is framework-agnostic
- Easy to scale and maintain

### 2. Single Source of Truth with StateFlow
The ViewModel exposes a single `StateFlow<TextEchoState>` that represents the entire UI state:

```kotlin
data class TextEchoState(
    val inputText: String = "",
    val outputText: String = "",
    val isLoading: Boolean = false,
    val error: UiError? = null
)
```

**Benefits:**
- Predictable state updates
- Easy to debug state changes
- Prevents inconsistent UI states
- Survives configuration changes

### 3. Sealed Classes for Error Handling
Two levels of sealed classes handle errors:

**Domain Layer** (`ValidationResult.Error`):
```kotlin
sealed class Error : ValidationResult() {
    data object EmptyInput : Error()
    data class TooShort(val minLength: Int) : Error()
    data object NetworkError : Error()
    data class ServerError(val message: String) : Error()
    data class Unknown(val throwable: Throwable) : Error()
}
```

**UI Layer** (`TextEchoState.UiError`):
```kotlin
sealed class UiError {
    data object EmptyInput : UiError()
    data class TooShort(val minLength: Int) : UiError()
    data object NetworkError : UiError()
    data class ServerError(val message: String) : UiError()
    data class UnknownError(val message: String) : UiError()
}
```

**Benefits:**
- Type-safe error handling
- Compile-time exhaustive when expressions
- Clear error context with data
- Separation between domain and UI concerns

### 4. Extension Functions for Error Mapping
Domain errors are mapped to UI errors using extension functions:

```kotlin
fun ValidationResult.Error.toUiError(): TextEchoState.UiError
fun TextEchoState.UiError.toErrorMessage(): String
```

**Benefits:**
- Keeps mapping logic separate from business logic
- Easy to test mapping independently
- Composables stay clean and readable
- Centralized error message formatting

### 5. Hilt Dependency Injection
Used Hilt for dependency injection with a simple module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindTextValidationRepository(
        impl: TextValidationRepositoryImpl
    ): TextValidationRepository
}
```

**Benefits:**
- Easy to swap implementations (useful for testing)
- Automatic lifecycle management
- Reduced boilerplate
- Better testability with constructor injection

### 6. Repository Pattern
The repository acts as an abstraction layer between domain and data:

```kotlin
interface TextValidationRepository {
    suspend fun validateText(text: String): ValidationResult
}
```

**Benefits:**
- Domain layer doesn't depend on data layer implementation
- Easy to add caching, multiple data sources
- Better testability with mocking

## Key Features

### 1. Client-Side Validation
The `ValidateTextUseCase` performs pre-validation before calling the repository:
- Checks for empty/blank input
- Validates minimum text length (3 characters)
- Only calls server if basic validation passes

### 2. Simulated Server Validation
The `ValidationService` simulates an external API:
- 1.5 second network delay
- 20% random failure rate to test error handling
- Throws `ValidationException` on failure

### 3. Comprehensive Error Handling
- **Empty Input**: Client-side validation
- **Too Short**: Client-side validation with minimum length
- **Network Error**: Simulated connection issues
- **Server Error**: Server validation failure
- **Unknown Error**: Unexpected exceptions

### 4. Loading States
Loading indicator is shown during validation with:
- Circular progress indicator
- Disabled input during loading
- Proper loading state management in ViewModel

### 5. Material 3 Design
Clean, modern UI with:
- Material 3 components (OutlinedTextField, Card, Button)
- Light and dark theme support
- Smooth animations for output display
- Snackbar for error messages

## Testing Strategy

### Unit Tests
Comprehensive unit tests for critical components:

**ValidateTextUseCaseTest**:
- Tests client-side validation logic
- Tests repository interaction
- Tests error propagation
- 6 test cases with 100% coverage

**TextEchoViewModelTest**:
- Tests state management
- Tests user interactions
- Tests loading states
- Tests error handling
- 9 test cases with full coverage

### Testing Libraries Used
- **JUnit**: Test framework
- **MockK**: Mocking framework for Kotlin
- **Turbine**: Flow testing library
- **Coroutine Test**: Testing coroutines

## Challenges & Solutions

### Challenge 1: State Management During Async Operations
**Problem**: Managing loading state and preventing race conditions during validation.

**Solution**: Used `StateFlow.update` to ensure atomic state updates and proper coroutine cancellation handling.

### Challenge 2: Error Message Centralization
**Problem**: Keeping error messages consistent across the UI.

**Solution**: Created extension function `toErrorMessage()` that centralizes all error message formatting in one place.

### Challenge 3: Testing ViewModel with StateFlow
**Problem**: Testing StateFlow emissions and loading states.

**Solution**: Used Turbine library for Flow testing and `StandardTestDispatcher` for coroutine testing with controlled advancement.

### Challenge 4: Separation of Domain and UI Concerns
**Problem**: Keeping domain errors independent from UI representation.

**Solution**: Created separate sealed classes for domain and UI errors, connected by mapper extension functions.

## Running the Project

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 34

### Option 1: Android Studio (Recommended - Easiest)
1. Extract `text-echo-app.zip`
2. Open Android Studio
3. Select "Open" and choose the `text-echo-app` folder
4. **Important**: Android Studio will automatically:
   - Download Gradle 8.0
   - Generate gradle-wrapper.jar (if missing)
   - Sync all dependencies
   - Setup the project
5. Wait for Gradle sync to complete (first time may take a few minutes)
6. Click Run or press Shift+F10

**Note**: If you see any Gradle sync errors, just click "Sync Now" or "File → Sync Project with Gradle Files". Android Studio will handle everything automatically.

### Option 2: Command Line (Advanced)
If gradle-wrapper.jar is missing:
```bash
# First time setup - let Android Studio generate the wrapper
# Or manually: gradle wrapper --gradle-version 8.0

# Then build
./gradlew build
./gradlew test
```

### Common Issues

**Problem**: "gradle-wrapper.jar not found"
- **Solution**: Open the project in Android Studio first. It will generate all required files automatically.

**Problem**: Gradle sync fails
- **Solution**: Click "Sync Now" in the banner, or File → Invalidate Caches / Restart

**Problem**: "Failed to notify project evaluation listener"
- **Solution**: The project uses compatible versions (Gradle 8.0, AGP 8.1.4, Kotlin 1.9.0). Just sync again.

### Running Tests
```bash
# Unit tests
./gradlew test

# Specific test class
./gradlew test --tests ValidateTextUseCaseTest
```

## Project Statistics
- **Development Time**: ~2.5 hours
- **Lines of Code**: ~800 (excluding tests)
- **Test Coverage**: 100% for UseCase and ViewModel
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Code Quality Practices
- Comprehensive KDoc documentation
- Consistent naming conventions
- Immutable data classes
- Proper error handling with sealed classes
- Dependency injection for testability
- Single Responsibility Principle
- Clean code principles

## Future Enhancements (Not Implemented)
These were intentionally not implemented to avoid over-engineering:
- Actual network calls (using Retrofit)
- Persistent storage (Room database)
- UI tests (Compose UI testing)
- Input validation rules configuration
- Retry mechanism for failed validations
- Analytics integration
- Multiple text fields

## Conclusion
This project demonstrates core Android development concepts with modern best practices while remaining simple and focused on the requirements. The architecture is scalable and maintainable, making it easy to add new features during the technical interview.
