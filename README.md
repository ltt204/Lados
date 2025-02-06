# Lados
> Clothe shop application for mobile project

## Table of Content
- [Overview](#Overview)
- [Members](#Members)
- [Tech Stack](#Tech-stack)
- [Project Setup](#Project-Setup)
- [Key Feature](#Key-Feature)
- [Usage](#Usage)
- 

## Overview
**Lados** is a market mobile application for clothes shop to sell their products, where customers can view and decide if they want to make a purchase.
## Members 
|Student Id |Name|
|---|---|
| 22120363   | Phan Hồng Thức  |
| 22120364   |  Nguyễn Hoài Thương |
| 22120371   | Lý Trọng Tín  |
| 22120410   | Dương Hữu Tường  |
| 22120415   |  Trần Quang Tuyên | 

## Tech Stack
- **Languages**: Kotlin
- **Back-end**: Firebase
- **Database**: SQLite
- **Tool kit**: Jetpack compose
  
## Project Setup
### Firebase Configuration
#### For team members:
To set up Firebase for your local development:
1. Go to the Firebase Console.
2. Select the project associated with this repository.
3. Download the google-services.json file:
    > For Android: In the **Project Settings** section, under "**Your apps**", click on the Android app and download the 
    `google-services.json` file.
4. Place the `google-services.json` file in the root directory of your project (or wherever your Firebase SDK expects it).

#### For someone who is not apart of our team
- You need to look at [this tutorial](https://firebase.google.com/docs/android/setup) of `Firebase` to configure Firebase in **Android Studio**.
- Before you the Firebase Android configuration file (`google-services.json`) to your app, please add these Firease products after you done create Firebase project:
    - `Authentication`:  with two methods which are `Email/Password` and `Google`
    - `Cloud FireStore`
    - `FireStorage`
- After those steps above, if you want to use Signin With Google. You need to:
    1. Open terminal at project folder, run this command: `./gradlew signingreport` and this is what you get:
        ```cli
        > Task :app:signingReport
        Variant: debug
        Config: debug
        Store: C:\Users\trong\.android\debug.keystore
        Alias: AndroidDebugKey
        MD5: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
        Alias: AndroidDebugKey
        MD5: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
        SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX // Copy this line
        SHA-256:        XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
        Valid until: Sunday, March 29, 2054
        ```
    2. After you got the SHA1 key, you will need to go to Firebase project. At project setting, scroll down and you will see **Your apps** part (make sure you have enable the Google provider), add SHA1 to SHA1 finger print.

## Key Feature
The application provides multiple features for three main roles which are _Customer_, _Staff_ and _Admin_. Here is some key features:
1. Admin: Have the ability to
   - Manage user, product and coupon
   - View total statistics of business activities.
3. Staff: Have the ability to
   - Track orders
   - Support customers through chat
4. Customer:  Have the ability to
   - View, Buy products
   - View their orders status
   - Leave review when the product is dilivered
   - Return or cancel the order
## Usage
_later update_
