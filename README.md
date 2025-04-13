# ReadTrac - Track Your Reading Journey

ReadTrac is a modern Android application that helps users track their reading progress, manage book collections, write reviews, and discover personalized book recommendations.

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen" alt="Platform: Android">
  <img src="https://img.shields.io/badge/Kotlin-1.9.0-blue" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-purple" alt="Jetpack Compose">
</p>

## 📚 Features

- **Book Tracking**: Add books to your collection and track reading progress
- **Rating System**: Rate books on a 5-star scale
- **Review Management**: Write and manage private or public reviews
- **Reading Statistics**: View your reading progress across your entire collection
- **Book Recommendations**: Receive personalized book recommendations based on your reading history and preferences
- **External Book Integration**: Search and add books from external APIs (Google Books)
- **Modern UI**: Clean and intuitive Material 3 design with Jetpack Compose

## 📱 Screenshots

| Home Screen | Book Details | Reviews   | Recommendations   |
| ----------- | ------------ | --------- | ----------------- |
| [Home]      | [Details]    | [Reviews] | [Recommendations] |

## 🛠️ Technologies

- **Modern Android Development**:

  - 100% Kotlin
  - Jetpack Compose UI toolkit
  - Material 3 Design

- **Architecture**:

  - MVVM (Model-View-ViewModel) pattern
  - Repository pattern for data handling
  - Clean Architecture principles

- **Dependency Injection**:

  - Hilt for dependency injection

- **Local Storage**:

  - Room Database for local persistence

- **Network**:

  - Retrofit for API communication
  - Google Books API integration

- **Asynchronous Programming**:

  - Kotlin Coroutines
  - Flow for reactive streams

- **Testing**:
  - JUnit for unit tests
  - Mockito for mocking
  - Compose testing for UI tests

## ⚙️ Getting Started

### Prerequisites

- Android Studio Electric Eel or newer
- Android SDK 26+
- Gradle 8.0+
- JDK 11

### Setup & Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/ReadTrac.git
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the application on an emulator or physical device

## 📐 Architecture

ReadTrac follows the MVVM (Model-View-ViewModel) architecture pattern along with the Repository pattern:

- **UI Layer**: Jetpack Compose UI components and screens
- **ViewModel Layer**: Handles UI logic and state management
- **Repository Layer**: Abstracts data sources and operations
- **Data Layer**:
  - Local: Room Database
  - Remote: Retrofit API client

## 🧪 Testing

The project includes:

- Unit tests for ViewModels and Repositories
- Integration tests for database operations


## 🙏 Acknowledgments

- Google Books API for book data
- Material Design team for design guidelines
- JetBrains for Kotlin and Android development tools
