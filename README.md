NammaKathey (ನಮ್ಮ ಕಥೆ)
An AI-powered personalized storytelling app rooted in the culture and heritage of Karnataka.

Overview
NammaKathey is an Android application that generates personalized stories for children using Generative AI. The app allows users to enter a child’s name and instantly creates safe, inspiring, and culturally relevant stories where the child becomes the hero of an adventure set in Karnataka.

The project combines modern Android development with AI-powered storytelling to create a unique educational and entertaining experience.

Problem Statement
Modern digital storytelling platforms often face the following issues:

1. Lack of Personalization
Children usually remain passive listeners instead of active participants in stories.

2. Cultural Erosion
Many storytelling applications focus on generic global narratives and fail to connect children with local traditions, heritage, and history.

3. Content Fatigue
Parents often struggle to create fresh, engaging, and meaningful stories regularly.

Solution
NammaKathey solves these challenges using Generative AI to create personalized and culturally rich stories where the child becomes the main character in adventures inspired by Karnataka’s history, landmarks, traditions, and festivals.

Features
Personalized hero customization
AI-generated storytelling using Gemini 2.0 Flash
Karnataka-based cultural storytelling
Child-safe and age-appropriate content generation
Modern UI built using Jetpack Compose
Fast story generation with smooth user experience

Tech Stack
Kotlin – Programming Language
Jetpack Compose – UI Development
Google Gemini 2.0 Flash – AI Story Generation
Hilt (Dagger-Hilt) – Dependency Injection
Retrofit – Networking
OkHttp – HTTP Client
Gradle Kotlin DSL – Build System
Git & GitHub – Version Control

Project Architecture
The application follows a clean and modular Android architecture using:
MVVM Architecture
Repository Pattern
Dependency Injection with Hilt
Compose-based UI components

Installation Guide
1. Clone the Repository
git clone https://github.com/DeepaGhantal/NammaKathey.git
2. Open in Android Studio
Open the project folder in Android Studio.
3. Generate Gemini API Key
Visit Google AI Studio
Generate a Gemini API Key
4. Configure API Key
Open the local.properties file and add: GEMINI_API_KEY=your_api_key_here
5. Sync Gradle
Click:Sync Project with Gradle Files
Wait for all dependencies to download successfully.

Run the Application
Using Terminal
./gradlew installDebug
Using Android Studio
Click the Run button in Android Studio to launch the application on an emulator or physical device.

Folder Structure
NammaKathey/
├── app/
│   ├── src/main/java/com/example/nammakathey/
│   │   ├── ui/             # Jetpack Compose Screens & ViewModels
│   │   ├── repository/     # AI Logic and Data Handling
│   │   ├── di/             # Hilt Dependency Injection Modules
│   │   └── MainActivity.kt # Application Entry Point
│   ├── build.gradle.kts
├── gradle/
├── local.properties
└── README.md

Demo
GitHub Repository
https://github.com/DeepaGhantal/NammaKathey

Future Improvements
-Offline story storage using Room Database
-Kannada language support
-AI-generated illustrations for stories
-Text-to-Speech narration feature
-PDF export for story booklets
-User authentication and cloud sync
-Story history and favorites section

Learning Outcomes
-Through this project, the following skills were developed:
-Android App Development using Kotlin
-Modern UI Design with Jetpack Compose
-Integration of Generative AI APIs
-API Handling and Networking
-Dependency Injection using Hilt
-Project Structuring and GitHub Management

Contributing
Contributions, suggestions, and improvements are welcome.
-Fork the repository
-Create a feature branch
-Commit your changes
-Push to your branch
-Open a Pull Request


