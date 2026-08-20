plugins {
    alias(libs.plugins.android.application)

    // Firebase / Google Services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.englishvocabulary"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.englishvocabulary"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // =====================================================
    // ANDROID
    // =====================================================

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)


    // =====================================================
    // FIREBASE BOM
    // =====================================================

    implementation(
        platform(
            "com.google.firebase:firebase-bom:34.16.0"
        )
    )


    // =====================================================
    // FIREBASE AUTHENTICATION
    // =====================================================

    implementation(
        "com.google.firebase:firebase-auth"
    )


    // =====================================================
    // FIREBASE AI LOGIC - GEMINI
    // =====================================================

    implementation(
        "com.google.firebase:firebase-ai"
    )


    // =====================================================
    // FIREBASE APP CHECK - DEBUG
    // Dùng khi chạy app bằng Android Studio
    // =====================================================

    implementation(
        "com.google.firebase:firebase-appcheck-debug"
    )


    // =====================================================
    // THƯ VIỆN CẦN CHO JAVA
    // =====================================================

    // ListenableFuture
    implementation(
        "com.google.guava:guava:31.0.1-android"
    )

    // Publisher / Streaming
    implementation(
        "org.reactivestreams:reactive-streams:1.0.4"
    )


    // =====================================================
    // TEST
    // =====================================================

    testImplementation(libs.junit)

    androidTestImplementation(
        libs.espresso.core
    )

    androidTestImplementation(
        libs.ext.junit
    )
}