plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.xiaoyang.musicplayerapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xiaoyang.musicplayerapplication"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    sourceSets {
        android.sourceSets["main"].resources.srcDirs(
            "src/main/res",
            "src/main/libs",
            "src/main/jniLibs"
        )
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit2.adapter.rxjava3)
    implementation(libs.retrofit2.converter.gson)
    // Rxjava
    implementation(libs.rxjava3.rxjava)
    // Gson
    implementation(libs.com.google.code.gson.gson)
    //Glide
    implementation(libs.com.github.bumptech.glide.glide3)
    annotationProcessor(libs.compiler)
    implementation(libs.logging.interceptor)


}