# FridgeFairy need to add logo


A mobile budgeting application designed to help users reach financial goals, track expenditure, and make informed financial decisions through an interactive, gamified experience.

## Team Members
- Tayyib Dawood (ST10132915)
- Darren Jade Stander (ST10209886)

## Features

*	**User Authentication:** Email/Password and Google Sign-In via Firebase Auth.
*	**Modern Architecture:** MVVM with ViewModels & LiveData/Flow, Room, Retrofit, Navigation, and ViewBinding.
*	**Material UI & UX:** CoordinatorLayout + Toolbar (back arrow), Floating Action Button, Snackbars for feedback, and RecyclerView lists.

## Custom Features
*	**Pantry/Inventory Management:** Add, edit, and delete food items; optional categories/notes; stored locally.
*	**Recipe Search & Details:** Spoonacular API integration with Retrofit/OkHttp; view recipe info and images (Coil).
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

## Technologies Used

*   **Language:** Kotlin
*   **IDE:** Android Studio
*   **CI/CD:** GitHub Actions
   <img width="1762" height="601" alt="Screenshot 2025-10-06 090803" src="https://github.com/user-attachments/assets/6a17ab44-00b1-4ca1-8fff-50d7f9cc40bd" />


## References
Darren Stander (st10209886)
