# Simple Text Echo App

A modern Android application demonstrating text validation with a simulated external server, built with Jetpack Compose and Clean Architecture principles.

## Overview

This application validates user text input through a simulated server API, displaying success or error states with appropriate UI feedback. 
The implementation focuses on clean architecture, proper state management, dependency injection, and comprehensive testing.

## Features

- Text input with multi-line support and clear functionality
- Simulated server validation with realistic network behavior
- Loading states with visual feedback
- Comprehensive error handling with user-friendly messages
- Material3 design with smooth state transitions

## Architecture

The project implements **Clean Architecture** with **MVVM** pattern, organized into three distinct layers:

### Project Structure

```
app/src/main/java/com/joist/simpleechoapp/
├── domain/                          # Business logic (framework-independent)
│   ├── model/ValidationResult.kt
│   ├── repository/TextValidationRepository.kt
│   ├── usecase/ValidateTextUseCase.kt
│   └── util/StringProvider.kt
│
├── data/                            # Data management
│   ├── remote/ValidationService.kt
│   ├── repository/TextValidationRepositoryImpl.kt
│   └── util/AndroidStringProvider.kt
│
├── presentation/                    # UI layer
│   └── echo/
│       ├── EchoScreen.kt
│       ├── EchoViewModel.kt
│       └── EchoUiState.kt
│
├── di/                              # Dependency injection
│   ├── AppModule.kt
│   └── ViewModelFactory.kt
│
└── MainActivity.kt
```

### Architecture Layers

**Domain Layer** - Pure Kotlin with no Android dependencies
- `ValidationResult`: Sealed interface for type-safe result handling
- `TextValidationRepository`: Repository abstraction
- `ValidateTextUseCase`: Business rules (empty check, whitespace trimming)
- `StringProvider`: String resource abstraction for framework independence

**Data Layer** - Data sources and implementations
- `ValidationService`: Simulates API with realistic delays (500-1500ms) and 70% success rate
- `TextValidationRepositoryImpl`: Repository implementation with error handling
- `AndroidStringProvider`: Android-specific string resource implementation

**Presentation Layer** - UI and state management
- `EchoViewModel`: State management with StateFlow
- `EchoUiState`: Sealed interface for UI states (Idle, Loading, Success, Error)
- `EchoScreen`: Jetpack Compose UI with Material3

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material3
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Manual DI
- **Async**: Coroutines & Flow
- **Testing**: JUnit, MockK, Turbine (17 unit tests)

## Key Technical Decisions

### Clean Architecture Implementation
Implemented full layer separation to demonstrate scalable architecture patterns.
The domain layer remains completely framework-independent, enabling easy testing and potential migration to KMP (Kotlin Multiplatform) if needed.

### Manual Dependency Injection
Selected manual DI over Hilt to match project scope.
The implementation demonstrates understanding of dependency inversion principles without introducing framework overhead unnecessary for this scale.

### String Resource Abstraction
Created `StringProvider` interface to maintain Clean Architecture principles while accessing Android string resources.
This allows the domain and data layers to use localized strings without Android framework dependencies, improving testability.

### State Management Pattern
Used `StateFlow` with sealed interfaces for type-safe state handling.
Follows unidirectional data flow pattern, making state mutations predictable and easy to test.

### Direct Event Handling
Implemented direct function calls for UI events rather than sealed event classes (MVI pattern).
For this scope with 3-4 simple events, direct functions provide clarity without additional abstraction overhead.

## Challenges & Learnings

### AI-Assisted Development with Architectural Oversight
This project was developed using AI tools (Claude Code) as a development accelerator while maintaining strict code quality standards.
The primary challenge was ensuring AI suggestions aligned with project scope without introducing over-engineering.

**Approach:**
- Critically evaluated each architectural component for necessity and purpose
- Removed redundant abstractions that didn't add value at this scope
- Validated every class, interface, and pattern against SOLID principles
- Ensured domain layer remained framework-independent without unnecessary complexity
- Reviewed all generated code for unused imports, redundant logic, and premature optimization

**Result:** Clean implementation where AI served as a productivity tool, not a decision-maker.
Every component has clear architectural justification and can be explained in detail.

### Balancing Architecture with Scope
The project requirements emphasized avoiding over-engineering while demonstrating professional practices.
This required careful judgment about which patterns to apply now versus defer for production scenarios.
For example: choosing manual DI over Hilt, using direct event handlers instead of MVI, and keeping the architecture expandable but appropriately scoped.

## Running the Project

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11+
- Android SDK API 24+

### Build and Run
```bash
# Clone and open in Android Studio
# Sync Gradle dependencies
# Run on emulator or device
```

### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run specific test suite
./gradlew test --tests ValidateTextUseCaseTest
```

## Testing

The project includes **17 comprehensive unit tests** covering all architectural layers:

- **ValidateTextUseCaseTest**: Business logic validation (empty text, whitespace trimming, error propagation)
- **EchoViewModelTest**: State management and transitions (using Turbine for Flow testing)
- **TextValidationRepositoryImplTest**: Data layer error handling and mapping

All tests follow Arrange-Act-Assert pattern and use MockK for flexible mocking.

## Code Quality

- KDoc comments on all public APIs
- SOLID principles throughout
- No hardcoded strings (all in strings.xml)
- Proper null safety
- Consistent Kotlin conventions
- No unused code or redundant abstractions

## Potential Enhancements

For production scale, consider:
- Retrofit integration for real API calls
- Room database for offline support
- Hilt for larger dependency graphs
- Accessibility enhancements (TalkBack, content descriptions)

---

**Note**: This project demonstrates appropriate architectural decisions for scope while maintaining scalability patterns for future growth.
