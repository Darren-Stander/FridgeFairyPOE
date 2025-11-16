# FridgeFairy 

![WhatsApp Image 2025-10-06 at 18 07 22_a290a98e](https://github.com/user-attachments/assets/c22353f2-cff1-4ece-a006-3bf1bfd4f461)


A mobile budgeting application designed to help users reach financial goals, track expenditure, and make informed financial decisions through an interactive, gamified experience.

## Link to YouTube Video
https://youtu.be/G3L8bU_WCc8

## Purpose of the App

*    FridgeFairy is an Android app that helps people reduce food waste and plan meals by turning the contents of their fridge/pantry into actionable decisions:
*    Track food inventory (name, category, quantity, storage location, expiry).
*    Surface what’s expiring soon so users act before items spoil.
*    Build and manage a shopping list with quick add, mark-as-purchased, and swipe-to-delete + UNDO.
*    Per-user data separation via Firebase Authentication (email/password & Google sign-in) with Room queries scoped by userId.

### Core Capabilities

* **Smart Inventory Tracking**: Monitor food items with comprehensive details including name, category, quantity, storage location, and expiry dates
* **Expiry Alerts**: Proactive notifications highlighting items nearing their expiration date, ensuring nothing goes to waste
* **Intelligent Shopping List**: Streamlined list management with quick-add functionality, mark-as-purchased tracking, and swipe-to-delete with UNDO capability
* **Secure User Experience**: Per-user data isolation through Firebase Authentication supporting both email/password and Google sign-in
* **Offline-First Design**: Full functionality without internet connectivity, with intelligent synchronization when online

## 🎨Design & Architecture Considerations
### Architecture & Code Organisation

#### Pattern: MVVM with Repositories

*    ui/activities + ui/viewmodels handle presentation & state.
*    data/entities, data/dao, data/repository handle persistence and data access.
*    data/api contains Retrofit services for recipes

#### Room database (FridgeFairyDatabase.kt) with DAOs:

* **FoodDao.kt** - User-scoped food inventory management
* **ShoppingListDao.kt** - Shopping list operations
* **RecipeDao.kt** - Cached recipe results and user preferences

**Entities**:
*    **FoodItem** - Pantry/fridge inventory items
*    **ShoppingListItem** - Shopping list entries
*    **Recipe** - Cached recipe data

#### Networking & API Integration

*    **Retrofit + OkHttp**: RESTful API communication with logging interceptors
*    **Spoonacular API**: Recipe search and detailed nutritional information

#### Authentication & Security

 **Firebase Authentication**
 *    Secure user management
 *    Email/password authentication
 *    Google Sign-In integration
 *    Biometric authentication (Release 3.0)
* **AuthActivity**: Centralized authentication flow with seamless routing

---

### Navigation Architecture

* `MainActivity` - Hub for inventory management
* `RecipeSearchActivity` - Recipe discovery
* `RecipeDetailActivity` - Detailed recipe view
* `ShoppingListActivity` - Shopping list management
* `SettingsActivity` - User preferences and configuration
* `AnalyticsActivity` - Spending and consumption insights (Release 3.0)

---

### Offline Sync

* **Primary flows** (inventory, shopping list) fully functional without connectivity
* **Intelligent sync** when connection is restored
* **Graceful degradation** of network-dependent features (recipes, cloud backup)

---

## Team Members
- Tayyib Dawood (ST10132915) Leader
- Darren Jade Stander (ST10209886)

## ✨ Features

### Authentication & Security
* 🔐 Firebase Authentication with email/password support
* 🔑 Google Sign-In integration
* 👆 Biometric login (fingerprint/face recognition)
* 🔒 Secure per-user data isolation

### Core Functionality

#### 🗃️ Pantry/Inventory Management
* Add, edit, and delete food items
* Categorize items for easy organization
* Track quantities and storage locations
* Set expiry dates with automatic alerts
* Add custom notes and details

#### 🍳 Recipe Discovery
* Search thousands of recipes via Spoonacular API
* View detailed cooking instructions
* Check nutritional information
* Save favorite recipes

#### 🛒 Smart Shopping List
* Quick-add items from inventory
* Mark items as purchased
* Swipe-to-delete with UNDO safety net
* One-tap "Clear Purchased Items"
* Sync across devices

#### 📊 Analytics Dashboard (New!)
* Track spending patterns
* Visualize consumption by category (Produce, Dairy, Snacks)
* Monthly expense summaries
* Food waste reduction metrics

### Advanced Features

#### 📴 Offline-First Data Persistence
* All core functionality available without internet
* Automatic background synchronization
* Conflict resolution for multi-device usage

#### 📸 Receipt Scanner (New!)
* OCR technology to scan grocery receipts
* Automatic item extraction and categorization
* Instant addition to inventory or shopping list
* Expense tracking integration

#### 🌍 Multi-Language Support (New!)
* English (default)
* Afrikaans
* isiZulu
* Automatic language detection based on device settings

---

## 📋 Release Notes

### Version 3.0 - Latest Release (November 2024)

**🚀 Major New Features**

* **Receipt Scanner** 📸
  * Intelligent OCR technology to scan and extract items from grocery receipts
  * Automatic categorization of scanned items
  * Direct import to inventory or shopping list
  * Integrated expense tracking for budget management

* **Biometric Authentication** 👆
  * Fingerprint recognition support
  * Face ID/Face unlock compatibility
  * Optional secondary authentication method
  * Enhanced security without sacrificing convenience

* **Multi-Language Support** 🌍
  * Full app translation into Afrikaans
  * Complete isiZulu language support
  * Automatic language detection
  * Easy language switching in settings

* **Offline Synchronization** 📴
  * Intelligent conflict resolution for multi-device usage
  * Background sync when connectivity is restored
  * Visual indicators for sync status
  * Data integrity protection

* **Analytics Dashboard** 📊
  * Comprehensive spending breakdown
  * Category-based consumption tracking (Produce, Dairy, Snacks, etc.)
  * Visual charts and graphs
  * Monthly and yearly expense summaries
  * Food waste reduction metrics

**🔧 Improvements & Optimizations**

* Enhanced UI responsiveness
* Improved database query performance
* Better error handling and user feedback
* Optimized battery consumption
* Reduced app size and memory footprint

---

### Version 2.0 - Feature Release

**🎯 Core Feature Implementation**

* Complete MVVM architecture implementation
* Firebase Authentication with Google Sign-In
* Room database for local storage
* Pantry/inventory management system
* Recipe search integration with Spoonacular API
* Shopping list with swipe-to-delete and UNDO
* Material Design UI components
* Offline-first functionality

---

### Version 1.0 - Initial Planning & Prototype

**📝 Planning Phase**

* Feature requirement analysis
* Architecture design decisions
* Technology stack selection
* UI/UX wireframing
* Database schema design
* API integration planning

---

## 🚀 Getting Started

### Prerequisites

* Android Studio Hedgehog (2023.1.1) or newer
* Android SDK 24 (Android 7.0) or higher
* JDK 11 or higher
* Git installed on your machine

### Installation

1. **Clone the repository**
```bash
   git clone https://github.com/MogamatTayyibDawood/FridgeFairy
   cd FridgeFairy
```

2. **Open in Android Studio**
   * Launch Android Studio
   * Select `File` → `Open`
   * Navigate to the cloned repository folder
   * Click `OK`
   * Android Studio will automatically sync Gradle and download dependencies

3. **Configure Firebase** (if setting up from scratch)
   * Download `google-services.json` from Firebase Console
   * Place it in the `app/` directory
   * Ensure Firebase Authentication and Firestore are enabled

### Running the App

1. **Select a Target Device**
   * **Physical Device**: Enable USB debugging in Developer Options
   * **Emulator**: Create an AVD via `Tools` → `Device Manager`
     * Recommended: Pixel 9 Pro with Android API 34

2. **Build and Run**
   * Click the green ▶️ **Run** button in the toolbar
   * Or use keyboard shortcut: `Shift + F10` (Windows/Linux) or `Control + R` (Mac)
   * Android Studio will build and deploy the app

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

## Signed APK Images
   

# References

## Darren Stander (ST10209886)

### Claude sonnet 4.5

*    https://claude.ai/share/8706b2ed-0b3c-422d-8e97-222168718c78
*    https://claude.ai/share/6f59ca9d-e0c8-443d-9814-a78534e41ba7
*    https://claude.ai/share/503bab8d-1c83-4003-a124-403f083624f5
*    https://claude.ai/share/13b256cf-6217-48cc-ae7d-f7bcb99eeaf4
  
### ChatGPT 5

*    https://chatgpt.com/share/68e41720-d208-800c-a64d-829c5e43f3ce
*    https://chatgpt.com/share/691a280f-c16c-800c-9421-0a747fe895ec
*    https://chatgpt.com/share/68e41b12-6d30-800c-aabc-936efb5f4873

  ### YT Videos 

*    https://www.youtube.com/watch?v=4xczNJVHW40
*    https://www.youtube.com/watch?v=QAY47cuMW9k
*    https://www.youtube.com/watch?v=P3B2opeHpV8
*    https://www.youtube.com/watch?v=QqQ83qK6_rk
*    https://www.youtube.com/watch?v=_dCRQ9wta-I
*    https://www.youtube.com/watch?v=tXHWyt8g5jc

  ### DeepSeek

<img width="596" height="880" alt="Screenshot 2025-10-06 211545" src="https://github.com/user-attachments/assets/e78b3f49-85e2-4311-accb-459bdcad16c3" />
<img width="530" height="892" alt="Screenshot 2025-10-06 211608" src="https://github.com/user-attachments/assets/7c6dc175-0b84-47cd-a62d-a09dda03bfad" />
<img width="542" height="879" alt="Screenshot 2025-10-06 211622" src="https://github.com/user-attachments/assets/cc072821-4324-4615-8ffb-352742c96ab9" />


## Tayyib Dawood(ST10132915)

### Claude sonnet 4.5

*    https://claude.ai/share/faf79f4a-662c-4604-85c4-5b183a067e1c
  
*    https://claude.ai/share/640f1606-6172-4c18-892e-f957e440c88f
  
*    https://claude.ai/share/d42f5b8f-4dc9-4c78-8a67-825686779ab4

### ChatGPT 5

* https://chatgpt.com/share/68e42724-4cc8-8012-a3dc-c90c740c48a9
  
* https://chatgpt.com/share/68e42823-08dc-8012-80cc-e403d8a5913d


 ### YT Videos 

 *  https://www.youtube.com/watch?v=M3gYcPF51QY
   
 *  https://www.youtube.com/watch?v=lwAvI3WDXBY&list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o
   
 *  https://www.youtube.com/watch?v=3USvr1Lz8g8&list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o&index=3
   
 *  https://www.youtube.com/watch?v=rn53Roy-HgE&list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o&index=8
   

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

## Copilot usage
* Copilot was used because we needed to change the location of our GitHub Repo, wew weren't able to push the code the repo due to the organisation blocking our attempts.
* Had an error with the biometrics because the error was stopping the gradle from building, we used it as a tool to debug using the "PLAN" mode.
* Used Copilot for the planning how the biometrics system would work and then went to do research on how we can fully enable it.
