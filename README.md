# NoteWriterKMP

NoteWriterKMP is a modern, cross-platform note-taking application built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It allows users to create, manage, and search notes with a beautiful, responsive UI.

## 🚀 Features

- **Cross-Platform**: Shared logic and UI across Android and iOS.
- **Notes Management**: Create, edit, and view notes.
- **Search**: Quickly find notes with real-time search functionality.
- **Flexible View Modes**: Toggle between Grid and List views for your notes.
- **AI Assistant**: Integration for AI-powered note-taking assistance (In Progress).
- **Modern UI**: Built with Material 3 and Compose Multiplatform.
- **Local Persistence**: Uses SQLDelight for reliable local data storage.

## 🛠 Tech Stack

- **Kotlin Multiplatform**: Core logic sharing.
- **Compose Multiplatform**: Shared UI for Android and iOS.
- **SQLDelight**: Type-safe database for local storage.
- **Koin**: Lightweight dependency injection.
- **Navigation Compose**: Type-safe navigation within the app.
- **KotlinX Coroutines**: Asynchronous programming.
- **KotlinX Serialization**: Data serialization.

## 📂 Project Structure

- `/shared`: Contains the core logic and shared UI (Compose Multiplatform).
    - `commonMain`: Shared code for all platforms (Logic, UI, Database).
    - `androidMain`: Android-specific implementations and DI.
    - `iosMain`: iOS-specific implementations.
- `/androidApp`: Android-specific entry point and configuration.
- `/iosApp`: iOS-specific entry point (SwiftUI) and configuration.

## 🚀 Getting Started

### Prerequisites

- Android Studio Koala or newer.
- Xcode 15 or newer (for iOS development).
- Kotlin Multiplatform plugin installed in Android Studio.

### Running the App

#### Android
1. Open the project in Android Studio.
2. Select `androidApp` in the run configurations.
3. Click **Run**.

#### iOS
1. Open the `iosApp/iosApp.xcworkspace` file in Xcode.
2. Select a simulator or physical device.
3. Click **Run**.

Alternatively, you can run the Android app via CLI:
```bash
./gradlew :androidApp:installDebug
```

## 🤝 Contributing

Contributions are welcome! If you'd like to improve NoteWriterKMP, feel free to fork the repository and submit a pull request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
