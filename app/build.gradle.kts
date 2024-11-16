plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")


}

android {
    namespace = "org.nullgroup.lados"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.nullgroup.lados"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //Should use in Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.49")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp ("com.google.dagger:hilt-android-compiler:2.49")
    ksp ("androidx.hilt:hilt-compiler:1.2.0")

    //Constraint Layout
    implementation(libs.androidx.constraintlayout.compose)

    //Material 3
    implementation(libs.material3)

    // For Hilt testing
    testImplementation (libs.dagger.hilt.android.testing)
    kaptTest (libs.hilt.android.compiler)

    // For JUnit test Firebase
    testImplementation(libs.firebase.auth.ktx)
    testImplementation(libs.firebase.firestore.ktx)

    // For JUnit test Coroutines
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    // For JUnit test Mockito
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.kotlinx.coroutines.test)

    // For Android Instrumentation test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}