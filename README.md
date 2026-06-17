# NoteWriterKMP

NoteWriterKMP is a modern, cross-platform note-taking application built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It features a slick UI, local persistence, and real-time note management.

## 🚀 Features

- **Cross-Platform**: Shared logic and UI across Android and iOS.
- **Auto-save**: Never lose a thought; notes are saved automatically as you type.
- **Notes Management**: Create, edit, and delete notes with ease.
- **Real-time Search**: Quickly find notes with an integrated top search bar.
- **Flexible View Modes**: Seamlessly toggle between Grid and List layouts.
- **Modern UI/UX**: Built with Material 3, featuring a modern bottom navigation bar and smooth splash screen animations.
- **AI Assistant**: Dedicated space for upcoming AI-powered note-taking features (In Progress).
- **Local Persistence**: Reliable offline storage using SQLDelight.

## 🛠 Tech Stack

- **Kotlin Multiplatform**: Core business logic sharing.
- **Compose Multiplatform**: Declarative UI for both platforms.
- **SQLDelight**: Type-safe local database.
- **Koin**: Dependency injection for shared and platform-specific modules.
- **Navigation Compose**: Modern, type-safe navigation.
- **KotlinX Coroutines**: Efficient background processing.
- **KotlinX Serialization & Datetime**: Robust data handling and time management.
- **Multiplatform Settings**: Persistent key-value storage.

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
