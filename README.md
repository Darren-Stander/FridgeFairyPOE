# FridgeFairy 

![WhatsApp Image 2025-10-06 at 18 07 22_a290a98e](https://github.com/user-attachments/assets/c22353f2-cff1-4ece-a006-3bf1bfd4f461)


A mobile budgeting application designed to help users reach financial goals, track expenditure, and make informed financial decisions through an interactive, gamified experience.

## Team Members
- Tayyib Dawood (ST10132915) Leader
- Darren Jade Stander (ST10209886)

## Features

*	**User Authentication:** Email/Password and Google Sign-In via Firebase Auth.
*	**Modern Architecture:** MVVM with ViewModels & LiveData/Flow, Room, Retrofit, Navigation, and ViewBinding.
*	**Material UI & UX:** CoordinatorLayout + Toolbar (back arrow), Floating Action Button, Snackbars for feedback, and RecyclerView lists.

## Custom Features
*	**Pantry/Inventory Management:** Add, edit, and delete food items; optional categories/notes; stored locally.
*	**Recipe Search & Details:** Spoonacular API integration with Retrofit/OkHttp.
*	**Shopping List:** Add items, mark as purchased, swipe-to-delete with UNDO, and “Clear Purchased” in one tap.

## Advanced Features
*	**Offline Data Persistence:** All core data (pantry, shopping list, meal plan) stored locally using Room.

## Getting Started

To get a copy up and running, follow these simple steps:

1.  **Clone the repository:**
    git clone https://github.com/MogamatTayyibDawood/FridgeFairy
   
2.  **Open the project in Android Studio:**
    *   Launch Android Studio.
    *   Select Clone Repository.
 
    *   Android Studio will automatically sync the project and download the necessary Gradle dependencies.

## Running the App

1.  **Select a Target Device:**
    * start an Android Virtual Device (AVD) using the AVD Manager in Android Studio go to Tools > AVD Manager for the demo we used Pixel 9 pro.

2.  **Build and Run:**
    *   Click the Run app button (the green play icon) in the toolbar.
    *   Android Studio will build the project and launch the application.

## Running Tests

This project includes automated tests to ensure core functionality works as expected.

1.  **GitHub Actions:**
    *   This repository is configured with GitHub Actions to automatically build the application and run tests on every push and pull request to the main branch.
    *   You can view the status and logs of these automated workflows under the "Actions" tab of the GitHub repository: need to add the link when it works
      
    <img width="1762" height="601" alt="Screenshot 2025-10-06 090803" src="https://github.com/user-attachments/assets/6a17ab44-00b1-4ca1-8fff-50d7f9cc40bd" />


## Technologies Used

*   **Language:** Kotlin
*   **IDE:** Android Studio
*   **CI/CD:** GitHub Actions
   

# References

## Darren Stander (ST10209886)

### Claude sonnet 4.5

*    https://claude.ai/share/8706b2ed-0b3c-422d-8e97-222168718c78

*    https://claude.ai/share/6f59ca9d-e0c8-443d-9814-a78534e41ba7

*    https://claude.ai/share/503bab8d-1c83-4003-a124-403f083624f5
  
### ChatGPT 5

*    https://chatgpt.com/share/68e41720-d208-800c-a64d-829c5e43f3ce

*    https://chatgpt.com/share/68e41b12-6d30-800c-aabc-936efb5f4873

  ### YT Videos 

*    https://www.youtube.com/watch?v=4xczNJVHW40

*    https://www.youtube.com/watch?v=QAY47cuMW9k

*    https://www.youtube.com/watch?v=P3B2opeHpV8

  ### DeepSeek

<img width="596" height="880" alt="Screenshot 2025-10-06 211545" src="https://github.com/user-attachments/assets/e78b3f49-85e2-4311-accb-459bdcad16c3" />
<img width="530" height="892" alt="Screenshot 2025-10-06 211608" src="https://github.com/user-attachments/assets/7c6dc175-0b84-47cd-a62d-a09dda03bfad" />
<img width="542" height="879" alt="Screenshot 2025-10-06 211622" src="https://github.com/user-attachments/assets/cc072821-4324-4615-8ffb-352742c96ab9" />


## Tayyib Dawood(ST10132915)

### Claude sonnet 4.5

*    https://claude.ai/share/faf79f4a-662c-4604-85c4-5b183a067e1c

## AI Write up

### AI Tools Usage and Citation in Assessment Completion

Throughout the development of this Android application assessment, artificial intelligence tools played a significant supporting role in debugging, feature implementation, and documentation enhancement. The primary AI tools utilized included GitHub Copilot and conversational AI assistants, which were employed strategically to overcome technical challenges specific to Android Studio development and improve code quality.

### Debugging and Technical Problem-Solving

AI assistance proved invaluable in identifying and resolving broken code segments within the Android Studio environment. When encountering issues with Firestore integration in our Android application, AI tools helped diagnose connection problems, authentication errors, and data synchronization challenges specific to the Android platform. The AI provided specific solutions tailored to our Firebase Android SDK configuration, including proper dependency management in Gradle files and AndroidManifest.xml permissions, significantly reducing debugging time and helping us understand the underlying causes of integration failures.

### Feature Development

Several key features benefited from AI support. The dark mode implementation utilized AI to generate appropriate theme resources, style modifications in the styles.xml file, and logic for switching between light and dark themes using Android's AppCompatDelegate. AI suggestions ensured the dark mode was implemented consistently across all Activities and Fragments while maintaining Material Design principles and accessibility standards.
For the application logo integration, AI guidance helped determine the optimal approach for drawable resource management, XML vector drawable creation, and integration into the app's launcher icon and ActionBar. This ensured the logo displayed correctly across different Android devices and screen densities.

### Code Documentation

GitHub Copilot was specifically employed to add comprehensive comments throughout the Kotlin/Java codebase. This automated commenting improved code readability and maintainability, making it easier for team members to understand Activity lifecycles, Fragment transactions, ViewModel logic, and complex business logic flows. The AI-generated comments were reviewed and refined to ensure accuracy and clarity within the Android development context.

### Data Management Solutions

A critical challenge involved the Data Access Object (DAO) layer, where unique user data storage in Firestore presented difficulties. AI tools helped restructure the database schema and implement proper user-specific data isolation, ensuring each user's information remained separate and secure. This guidance included recommendations for Firestore document structures optimized for Android, query optimization to minimize network calls, and implementation of offline persistence capabilities specific to the Firebase Android SDK.

### CI/CD Pipeline Configuration

GitHub Actions configuration for Android builds presented initial challenges that AI assistance helped resolve. The AI provided working YAML configurations specific to Android projects, including proper Gradle caching, APK build steps, and automated testing with Android emulators. This accelerated the continuous integration setup and ensured reliable automated builds for our Android application.

### Documentation Enhancement
Finally, AI was utilized to refine this write-up document itself, improving clarity, structure, and professional presentation while maintaining the authentic description of AI's role in the Android development project.

### Reflection on AI Usage

While AI tools significantly enhanced productivity and problem-solving capabilities throughout the Android development process, all AI-generated solutions were critically reviewed, tested on various Android devices and emulators, and adapted to our specific requirements. The tools served as collaborative assistants rather than autonomous developers, with human oversight ensuring code quality, Android best practices, security, and alignment with project objectives.

