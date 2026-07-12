# Notiq - Compose Multiplatform Note-Taking App

<div align="center">

🎥 [Watch Demo Video](https://raw.githubusercontent.com/trickswithaman/NoteWriterKMP/master/media/demo.mov)
  <video src="https://raw.githubusercontent.com/trickswithaman/NoteWriterKMP/master/media/demo.mov" width="400" autoplay loop muted playsinline>
    <a href="https://raw.githubusercontent.com/trickswithaman/NoteWriterKMP/master/media/demo.mov">Download Demo Video</a>
  </video>
</div>

Notiq is a modern, privacy-focused, cross-platform note-taking application built with **Compose Multiplatform**. It features a 100% shared UI codebase for Android and iOS, blending nostalgic design elements (like a Rotary Dialer) with modern Material 3 aesthetics.

---

## 🚀 Current Capabilities (What the app can do now)

Notiq is fully functional for core note-taking workflows:
- **Create & Edit**: Compose rich text notes with real-time Markdown rendering.
- **Organize**: Toggle between **List** and **Grid** views and **Pin** important notes to the top.
- **Search**: Find notes instantly via a real-time filtering system.
- **Stylize**: Apply **Bold**, *Italic*, <u>Underline</u>, and **Custom Hex Colors** to text via a dedicated toolbar.
- **Secure**: Access the app through a unique **Rotary Phone-style Passcode Lock**.
- **Personalize**: Switch between **Light**, **Dark**, and **System Default** themes.
- **Offline Persistence**: All notes and settings are saved locally and persist across app restarts.

---

## 🛠 Technology Architecture

Notiq follows **Clean Architecture** principles to ensure the codebase is modular, testable, and scalable.

### 1. Layers
- **Domain Layer**: Contains the core business logic.
    - **Models**: Plain Kotlin data classes (e.g., `NoteEntity`).
    - **Use Cases**: Specific business rules (e.g., `GetNotesUseCase`, `AddNoteUseCase`).
    - **Repositories**: Interfaces defining data operations.
- **Data Layer**: Responsible for data retrieval and persistence.
    - **Implementation**: `NotesRepositoryImpl` handles data flow.
    - **Local Source**: **SQLDelight** for type-safe SQLite database operations.
    - **Mappers**: Converts DB entities to Domain models.
- **Presentation Layer**: Built with **Compose Multiplatform**.
    - **MVVM Pattern**: ViewModels (`NotesListViewModel`, `SettingsViewModel`) manage UI state using `StateFlow`.
    - **UI**: 100% shared Compose code in `commonMain`.

### 2. Dependency Injection (DI)
Powered by **Koin**, Notiq uses a modular DI approach:
- **`appModule`**: Defines shared dependencies like DataSources, Repositories, UseCases, and ViewModels.
- **`platformModule`**: Handles platform-specific injections (e.g., SQLDelight database drivers for Android and iOS).
- **Initialization**: Managed via a shared `initKoin` function called from the Android `Application` class and iOS `MainViewController`.

---

## 🧰 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) |
| **Language** | Kotlin Multiplatform (KMP) |
| **Database** | [SQLDelight](https://cashapp.github.io/sqldelight/) (SQLite) |
| **DI** | [Koin](https://insert-koin.io/) |
| **Navigation** | Jetpack Navigation Compose (Multiplatform) |
| **Concurrency** | KotlinX Coroutines |
| **Persistence** | Multiplatform Settings |
| **Time** | KotlinX Datetime |
| **UI Components** | Material 3 |

---

## 📂 Project Structure

- **`shared/`**: The core logic and UI.
    - **`commonMain/`**: Contains the 3-layer architecture (Data, Domain, Presentation) and the shared `App()` entry point.
    - **`androidMain/`**: Android-specific implementations (e.g., Database Driver).
    - **`iosMain/`**: iOS-specific implementations (e.g., Native SQLite Driver).
- **`androidApp/`**: Thin Android wrapper (MainActivity).
- **`iosApp/`**: SwiftUI wrapper hosting the `UIViewController` that renders the shared Compose UI.

---

## 🏗 Setup & Installation

### Prerequisites
- **Android Studio Koala+**
- **Xcode 15+** (for iOS)
- **Kotlin Multiplatform Plugin**

### Running the App
1. **Clone the repository.**
2. **Android**: Select `androidApp` in the run configurations and click Play.
3. **iOS**: Open `iosApp/iosApp.xcworkspace` in Xcode and run on a simulator/device.
4. **CLI**:
   ```bash
   ./gradlew :androidApp:installDebug
   ```

---

## 🤝 Contributing
Contributions are welcome! Fork the repo and submit a PR for any features or bug fixes.

