# Lados
> Clothe shop application for mobile project

## Table of Content
- [Overview](#overview)
- [Members](#members)
- [Tech Stack](#tech-stack)
- [Project Setup](#project-setup)
- [Key Features](#key-features)
- [Screenshots](#screenshots)

## Overview
**Lados** is a market mobile application for clothes shops to sell their products, where customers can view and decide if they want to make a purchase. The app features multiple user roles including customers, staff, and administrators.

## Members 
|Student Id |Name|
|---|---|
| 22120363   | Phan Hồng Thức  |
| 22120364   | Nguyễn Hoài Thương |
| 22120371   | Lý Trọng Tín  |
| 22120410   | Dương Hữu Tường  |
| 22120415   | Trần Quang Tuyên | 

## Tech Stack
- **Languages**: Kotlin
- **Back-end**: Firebase (Authentication, Cloud Firestore, Storage)
- **Database**: SQLite
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
  
## Project Setup
1. Clone this repository
```bash
git clone https://github.com/ltt204/Lados.git
```
2. Open the project in Android Studio
3. Sync Gradle files
4. Add your own `google-services.json` following the setup instructions above
5. Build and run the application
### Firebase Configuration
#### For team members:
To set up Firebase for your local development:
1. Go to the Firebase Console.
2. Select the project associated with this repository.
3. Download the `google-services.json` file:
    > For Android: In the **Project Settings** section, under "**Your apps**", click on the Android app and download the 
    `google-services.json` file.
4. Place the `google-services.json` file in the root directory of your project (or wherever your Firebase SDK expects it).

#### For non-team members
- You need to follow [this tutorial](https://firebase.google.com/docs/android/setup) for configuring Firebase in **Android Studio**.
- Before adding the Firebase Android configuration file (`google-services.json`) to your app, please add these Firebase products after creating your Firebase project:
    - `Authentication`:  with two methods which are `Email/Password` and `Google`
    - `Cloud FireStore`
    - `FireStorage`
- For Google Sign-In functionality:
    1. Open terminal at project folder, run this command: `./gradlew signingreport` and note the SHA1 key
    2. Add the SHA1 key to your Firebase project under Project Settings > Your apps > SHA1 fingerprint

### Development Environment
1. Android Studio (Arctic Fox or newer recommended)
2. Minimum SDK: API 26 (Android 8.0)
3. Target SDK: API 34 (Android 14)
4. Kotlin version: 1.9.0 or higher
5. JDK 17

## Key Features
The application provides multiple features for three main roles:

### Admin
- Manage users, products, and coupons
- View total statistics of business activities
- Generate reports on sales and inventory

### Staff
- Track and process customer orders
- Support customers through in-app chat
- Update product information and stock status

### Customer
- Browse and purchase products
- Track order status in real-time
- Leave reviews for delivered products
- Request returns or cancel orders
- Apply discount coupons

## Screenshots
[Coming soon]
