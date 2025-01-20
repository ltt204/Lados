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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        // Required for API desugaring
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
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
    implementation(libs.firebase.database.ktx)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))

    //Should use in Dagger Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.googleid)
    implementation(libs.androidx.hilt.common)
    kapt(libs.hilt.android.compiler.v2511)
    ksp(libs.androidx.hilt.compiler)

    //Constraint Layout
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.java.jwt.v440)

    // Coil for image loading
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.kt.coil.compose)

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
    testImplementation(libs.dagger.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)
    kaptTest(libs.hilt.android.compiler)
    testImplementation(libs.dagger.hilt.android.testing)

    // For JUnit test Firebase
    testImplementation(libs.firebase.auth.ktx)
    testImplementation(libs.firebase.firestore.ktx)

    // For JUnit test Coroutines
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    implementation(libs.kotlinx.coroutines.play.services)

    // For JUnit test MockK
    testImplementation(libs.mockk)

    // For JUnit test Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)

    // For Android Instrumentation test
    debugImplementation(libs.androidx.ui.tooling)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.ui.test.manifest)

    // For fetching online images
    implementation(libs.coil.compose)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.security.crypto)

    // For pager
    implementation(libs.accompanist.pager)
    implementation(libs.androidx.foundation)

    // For async works when viewmodel is destroyed
    implementation(libs.androidx.work.runtime.ktx)
    // DI for WorkManager
    implementation(libs.androidx.hilt.work)

    // Enable Java 8+ features supposed to be for SDK 26, to be used with MIN SDK 24
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}

kapt {
    correctErrorTypes = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}