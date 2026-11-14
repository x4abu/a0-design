plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "design.a0.app"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    val runNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()
    val commitCount = runCatching {
        val p = ProcessBuilder("git", "rev-list", "--count", "HEAD").redirectErrorStream(true).start()
        p.inputStream.bufferedReader().readText().trim().toIntOrNull()
    }.getOrNull() ?: 20
    val computedVersionCode = 100 + (runNumber ?: commitCount)

    signingConfigs {
        create("appSigning") {
            storeFile = file("${project.rootDir}/keystore/debug.keystore")
            storePassword = "androiddebugkey"
            keyAlias = "androiddebugkey"
            keyPassword = "androiddebugkey"
        }
    }

    defaultConfig {
        applicationId = "design.a0.app"
        minSdk = 26
        targetSdk = 35
        versionCode = computedVersionCode
        versionName = "1.2.${runNumber ?: commitCount}"
        vectorDrawables { useSupportLibrary = true }
        // Point this at your deployed backend. Empty = app runs fully offline
        // (local design engine) and only uses the backend when reachable.
        buildConfigField("String", "A0_BACKEND_URL", "\"https://a0-design-api.onrender.com\"")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ""
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("appSigning")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("appSigning")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
