import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

private val DEFAULT_API_BASE_URL = "https://foll-backend-iot-h5hkb3czhwedhph0.brazilsouth-01.azurewebsites.net/"

android {
    namespace = "pe.edu.upc.follmobileapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "pe.edu.upc.follmobileapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }

        val apiBaseUrl = localProperties.getProperty("api.base.url", DEFAULT_API_BASE_URL)
            .let { url -> if (url.endsWith("/")) url else "$url/" }

        val defaultHubUrl = when {
            apiBaseUrl.contains("10.0.2.2") -> "http://10.0.2.2:5237/"
            apiBaseUrl.contains("localhost") -> "http://localhost:5237/"
            else -> apiBaseUrl
        }
        val hubBaseUrl = localProperties.getProperty("hub.base.url", defaultHubUrl)
            .let { url -> if (url.endsWith("/")) url else "$url/" }

        buildConfigField("String", "BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "HUB_URL", "\"$hubBaseUrl\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // SignalR (WebSockets en tiempo real con el backend ASP.NET Core 8)
    implementation("com.microsoft.signalr:signalr:8.0.13")
    implementation("org.slf4j:slf4j-android:1.7.36")

    // Google Code Scanner (Play Services)
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    // ZXing for QR Generation
    implementation("com.google.zxing:core:3.5.3")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}