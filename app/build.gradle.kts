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
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))

    //Should use in Dagger Hilt
    ksp ("androidx.hilt:hilt-compiler:1.2.0")
    ksp ("com.google.dagger:hilt-android-compiler:2.49")
    implementation("com.google.dagger:hilt-android:2.49")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.firebase.storage)

    //Constraint Layout
    implementation(libs.androidx.constraintlayout.compose)

    // Coil for image loading
    implementation (libs.coil)
    implementation(libs.coil.compose)

    // Image Slider
    implementation(libs.coil.compose)

    //Material 3
    implementation(libs.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.compose.material3.material3)

    //Gson converter
    implementation(libs.retrofit2.converter.gson)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.coil.compose)

    // For Hilt testing
    kaptTest (libs.hilt.android.compiler)
    testImplementation (libs.dagger.hilt.android.testing)

    // For JUnit test Firebase
    testImplementation(libs.firebase.auth.ktx)
    testImplementation(libs.firebase.firestore.ktx)

    // For JUnit test Coroutines
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    // For JUnit test MockK
    testImplementation (libs.mockk)

    // For JUnit test Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation (libs.kotlinx.coroutines.test)

    // For Android Instrumentation test
    debugImplementation(libs.androidx.ui.tooling)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.ui.test.manifest)

    // For fetching online images
    implementation("io.coil-kt:coil-compose:2.4.0")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))

}

kapt {
    correctErrorTypes = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}