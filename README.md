# Namma Kathey (ನಮ್ಮ ಕಥೆ)

**Namma Kathey** is an immersive Android application designed to educate and inspire children about the rich history and legendary heroes of Karnataka. Through engaging storytelling, interactive quizzes, and modern AI capabilities, the app brings the tales of brave warriors, visionary reformers, and celebrated poets to life.

## 🌟 Features

- **Explore Districts**: Navigate through various districts of Karnataka and discover what makes each of them famous.
- **Hero Biographies**: Learn about iconic figures like Kittur Chennamma, Nadaprabhu Kempegowda, Kuvempu, and more.
- **Bilingual Support**: Fully accessible in both **English** and **Kannada**, allowing children to learn in their preferred language.
- **AI Story Mode**: Powered by **Google Gemini**, this feature generates unique, child-friendly, inspiring stories about Karnataka's heroes on demand.
- **Interactive Quizzes**: Test your knowledge after reading stories to earn points and unlock badges.
- **Badge Gallery**: Track your progress and collect badges as you learn about different heroes.
- **Find Statues**: Integrated with Google Maps to help users find historical statues and landmarks across the state.

## 🛠️ Tech Stack

- **UI Framework**: Jetpack Compose
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Navigation**: Compose Navigation
- **Networking/AI**: Google AI SDK (Generative AI for Gemini)
- **Data Handling**: Gson for local JSON parsing
- **Image Loading**: Coil

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or newer
- JDK 11 or higher
- A Google Gemini API Key from [Google AI Studio](https://aistudio.google.com/)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/NammaKathey.git
   ```

2. **Configure the API Key**:
   Create a `local.properties` file in the root directory (if it doesn't exist) and add your Gemini API key:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```

3. **Build the project**:
   Open the project in Android Studio, sync Gradle, and run the app on an emulator or a physical device.

## 📁 Project Structure

- `ui/screens/`: Contains all Compose-based screens (Home, Detail, Story, Quiz, etc.).
- `viewmodel/`: Contains the `StoryViewModel` which manages app state and logic.
- `repository/`: Handles data fetching from local JSON and AI interactions via `GeminiHelper`.
- `model/`: Data classes for Districts, Heroes, Stories, and Quizzes.
- `assets/`: Contains `stories.json`, the primary data source for historical content.

## 🤝 Contributing

Contributions are welcome! If you have ideas for new features or want to add more stories about Karnataka's heroes, feel free to open an issue or submit a pull request.

