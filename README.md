# Lados
> Clothe shop application for mobile project

## Table of Content
- [Members](#Members)
- [Project Setup](#Project-Setup)
- [Folder Structure](#Folder-Structure)
- [Naming convention](#Naming-convention)


## Members 
|Student Id |Name|
|---|---|
| 22120363   | Phan Hồng Thức  |
| 22120364   |  Nguyễn Hoài Thương |
| 22120371   | Lý Trọng Tín  |
| 22120410   | Dương Hữu Tường  |
| 22120415   |  Trần Quang Tuyên | 

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
- You need to look at this [tutorial](https://firebase.google.com/docs/android/setup) of `Firebase` to configure Firebase in **Android Studio**.
- Before you the Firebase Android configuration file (`google-services.json`) to your app, please add these Firease products after you done create Firebase project:
    - `Authentication`:  with two methods which are `Email/Password` and `Google`
    - `Cloud FireStore`
    - `FireStorage`


## Naming convention
We strictly follow [Kotlin style guide](https://developer.android.com/kotlin/style-guide) provided by `Google`

## Folder Strcuture
```
...
src
├───androidTest
│   └───java
│       └───org
│           └───nullgroup
│               └───lados
├───main
│   ├───java
│   │   └───org
│   │       └───nullgroup
│   │           └───lados
│   │               ├───compose
│   │               │   ├───cart
│   │               │   ├───common
│   │               │   ├───order
│   │               │   ├───product
│   │               │   ├───profile
│   │               │   └───SignIn
│   │               ├───data
│   │               │   ├───local
│   │               │   ├───models
│   │               │   ├───remote
│   │               │   │   ├───apiService
│   │               │   │   └───firebase
│   │               │   └───repositories
│   │               │       ├───implementations
│   │               │       └───interfaces
│   │               ├───di
│   │               ├───navigations
│   │               ├───screens
│   │               │   ├───admin
│   │               │   ├───common
│   │               │   ├───customer
│   │               │   │   ├───cart
│   │               │   │   ├───checkout
│   │               │   │   ├───order
│   │               │   │   ├───product
│   │               │   │   └───profile
│   │               │   └───staff
│   │               ├───ui
│   │               │   └───theme
│   │               ├───utilities
│   │               └───viewmodels
│   │                   ├───common
│   │                   │   ├───events
│   │                   │   └───states
│   │                   └───customer
│   ├───res
│   │   ├───drawable
│   │   ├───font
│   │   ├───layout
│   │   ├───mipmap-anydpi-v26
│   │   ├───mipmap-hdpi
│   │   ├───mipmap-mdpi
│   │   ├───mipmap-xhdpi
│   │   ├───mipmap-xxhdpi
│   │   ├───mipmap-xxxhdpi
│   │   ├───values
│   │   ├───values-v31
│   │   └───xml
│   └───resources
└───test
    └───java
        └───org
            └───nullgroup
                └───lados
```