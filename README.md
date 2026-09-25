# MedTrack – Medication Management Android App

## Overview

MedTrack is an Android application developed using Kotlin and Jetpack Compose to help patients manage their medications and monitor their symptoms.

The application allows patients to view their prescribed medications, record medication intake, and track symptoms. It also integrates the Gemini API to provide AI-assisted medication guidance and information.

A clinician interface is included to allow clinicians to view patient information and prescribed medications.

## Features

- Patient login and authentication
- View and manage medications
- Record medication intake
- Track symptoms
- View prescribed medications
- Clinician interface for patient and medication information
- AI-assisted medication guidance using Gemini API
- Local data storage using Room Database

## Technologies

- Kotlin
- Android Studio
- Jetpack Compose
- Room Database
- MVVM Architecture
- Gemini API

## Architecture

MedTrack follows the MVVM (Model-View-ViewModel) architecture.

- **View:** Jetpack Compose UI
- **ViewModel:** Handles UI-related data and application logic
- **Repository:** Manages data access
- **DAO:** Handles database operations
- **Room Database:** Stores application data locally

## Requirements

To run MedTrack, you will need:

- Android Studio
- Internet connection
- Gemini API key

## Setup

### 1. Gemini API Key

The Gemini API key is required for the AI medication guidance feature.

Open the `local.properties` file in the project and add your Gemini API key:

```properties
apiKey=YOUR_GEMINI_API_KEY
```

## How to Run

1. Clone this repository.

2. Open the project in Android Studio.

3. Allow Android Studio to sync the Gradle files.

4. Add your Gemini API key to the appropriate configuration.

5. Connect an Android device or start an Android Emulator.

6. Build and run the application.

## Security

The Gemini API key is not included in this repository. A valid API key must be provided when setting up the application.

For the clinician password, replace:

```text
dollar-entry-apples
```

## Author

**Cho Yea Won**

Bachelor of Computer Science (Data Science)  
Monash University Malaysia
