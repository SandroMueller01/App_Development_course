plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.griptrainerapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.griptrainerapp"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1") // Keep this up-to-date
    implementation("com.google.android.material:material:1.11.0") // Check for updates
    implementation("com.polidea.rxandroidble2:rxandroidble:1.10.0") // Make sure it's the latest version
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Keep it updated
    testImplementation("junit:junit:4.13.2") // Consider migrating to JUnit 5
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Keep it updated
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Keep it updated
    implementation("androidx.viewpager2:viewpager2:1.0.0") // Keep it updated
    implementation("androidx.sqlite:sqlite:2.4.0") // Check for updates
    implementation("androidx.room:room-runtime:2.6.1") // Check for updates
    annotationProcessor("androidx.room:room-compiler:2.6.1") // Check for updates
}