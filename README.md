# Dog Breed App

An Android application that showcases different dog breeds with two main features:
1. A quiz game to test your knowledge of dog breeds
2. A comprehensive list of all dog breeds with images and sub-breeds information

## Features

### 🎮 Dog Breed Quiz
- Test your knowledge with a fun quiz game
- Identify dog breeds from their images
- 10 questions per game with automatic scoring
- Beautiful UI with smooth animations

### 📋 Dog Breeds List
- Browse all available dog breeds
- View images of each breed (using first image from the breed's image collection)
- See sub-breeds information
- Optimized list display with compact items (7-8 items per screen)
- Efficient image loading with caching

### 🚀 Technical Features
- Built with Jetpack Compose for modern Android UI
- Uses Ktor for networking
- Implements local caching with SharedPreferences
- Follows MVVM architecture pattern
- Repository pattern for data management
- Three-level caching strategy (Memory → Local → Network)

## Architecture

The app follows a clean MVVM (Model-View-ViewModel) architecture:

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│   UI Layer  │◄──►│ ViewModel    │◄──►│ Repository  │
└─────────────┘    └──────────────┘    └─────────────┘
                                          │
                                          ▼
                                ┌──────────────────┐
                                │   Data Sources   │
                                │ ┌──────────────┐ │
                                │ │ Network API  │ │
                                │ └──────────────┘ │
                                │ ┌──────────────┐ │
                                │ │ Local Cache  │ │
                                │ └──────────────┘ │
                                └──────────────────┘
```

## Project Structure

```
com.liye.dogapidemo
├── data
│   ├── model
│   │   ├── DogBreed.kt
│   │   ├── DogImage.kt
│   │   └── QuizQuestion.kt
│   ├── network
│   │   └── DogApiService.kt
│   └── repository
│       ├── DogRepository.kt
│       └── LocalCacheRepository.kt
├── presentation
│   ├── ui
│   │   └── DogQuizScreen.kt
│   └── viewmodel
│       ├── DogQuizViewModel.kt
│       └── DogListViewModel.kt
├── EntranceActivity.kt
├── MainActivity.kt
└── DogListActivity.kt
```

## Key Components

### EntranceActivity
The main entry point of the app with two options:
- Start Quiz Challenge: Opens the dog breed quiz game
- View All Breeds: Opens the comprehensive dog breeds list

### MainActivity
Hosts the DogQuizScreen for the quiz game functionality.

### DogListActivity
Displays a scrollable list of all dog breeds with:
- Compact list items for better screen utilization
- Breed images loaded from the first image in each breed's collection
- Sub-breeds information displayed below each breed name
- Optimized image loading that prevents flickering when scrolling

### DogRepository
Implements a three-level caching strategy:
1. Memory cache for fastest access
2. Local SharedPreferences cache for offline access
3. Network requests for fresh data
- Returns cached data immediately while refreshing in the background

### LocalCacheRepository
Handles local caching of dog breed data using SharedPreferences with custom JSON serialization.

## Tech Stack

- **Kotlin** - Modern, concise, and safe programming language
- **Jetpack Compose** - Modern toolkit for building native UI
- **Ktor** - Framework for building asynchronous clients and servers
- **Kotlinx Serialization** - Multiplatform serialization library
- **Coil** - Image loading library for Android
- **ViewModel** - Store and manage UI-related data in a lifecycle conscious way
- **SharedPreferences** - Framework for storing key-value pairs

## Dependencies

- `androidx.core:core-ktx` - Kotlin extensions for Android core libraries
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle-aware components
- `androidx.activity:activity-compose` - Compose integration with Activity
- `androidx.compose.*` - Jetpack Compose components
- `io.ktor:*` - Ktor client and serialization
- `io.coil-kt:coil-compose` - Image loading
- `org.jetbrains.kotlinx:kotlinx-serialization-json` - JSON serialization

## Getting Started

### Prerequisites

- Android Studio Jellyfish (2023.3.1) or later
- Kotlin 1.9.24 or later
- Android SDK API level 24 or higher

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/InnerNight/DogApiDemo.git
   ```

2. Open the project in Android Studio

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run the app:
   ```bash
   ./gradlew installDebug
   ```

## Usage

1. Launch the app to see the entrance screen
2. Choose between:
   - "Start Quiz Challenge" to test your dog breed knowledge
   - "View All Breeds" to browse the complete list of dog breeds
3. In the quiz:
   - Identify the dog breed from the image
   - Select the correct answer from 4 options
   - See your score at the end of 10 questions
4. In the breeds list:
   - Scroll through all available dog breeds
   - View images and sub-breeds information
   - Efficient loading with optimized item size

## Caching Strategy

The app implements a three-level caching strategy for optimal performance:

1. **Memory Cache** - Fastest access for recently used data
2. **Local Cache** - SharedPreferences storage for offline access
3. **Network** - Fetch fresh data from the Dog CEO API

Data is loaded in this order: Memory → Local → Network, ensuring quick loading times and offline capability.
Background refresh ensures data stays up-to-date without blocking the UI.

## API

The app uses the [Dog CEO API](https://dog.ceo/dog-api/) which provides:
- List of all dog breeds
- Random images for each breed
- Images for specific breeds and sub-breeds
- Collections of images for each breed

## Testing

The app includes unit tests for:
- Repository layer
- ViewModel logic
- Data parsing and caching

Run tests with:
```bash
./gradlew test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Dog CEO API](https://dog.ceo/dog-api/) for providing the dog breed data and images
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the modern UI toolkit
- [Ktor](https://ktor.io/) for the HTTP client