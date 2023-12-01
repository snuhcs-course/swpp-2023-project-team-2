import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // hilt
    kotlin("kapt")
    id("com.google.dagger.hilt.android")

    // Add the Google services Gradle plugin
    //id("com.google.gms.google-services")
}

android {
    namespace = "com.goliath.emojihub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.goliath.emojihub"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // testFunctionalTest = true

        // get properties from `local.properties`
        buildConfigField("String", "API_BASE_URL", getProperty("API_BASE_URL"))
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
        animationsDisabled = true
    }

    // hilt
    hilt {
        enableAggregatingTask = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // jetpack compose
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material:1.5.3")
    implementation("androidx.paging:paging-compose:3.2.1")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    // jetpack compose extended icons
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // test tools
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.paging:paging-testing:3.2.1")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("io.mockk:mockk-android:1.13.5")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.44")

    // navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // navigation with hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Media3
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")

    // pytorch mobile
    implementation("org.pytorch:pytorch_android:1.8.0")
    implementation("org.pytorch:pytorch_android_torchvision:1.8.0")
}

// Allow references to generated code (hilt)
kapt {
    correctErrorTypes = true
}

// get property from `local.properties` with key value
fun getProperty(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}