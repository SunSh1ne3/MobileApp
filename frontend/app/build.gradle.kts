plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.savelyev.MobileApp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.savelyev.MobileApp"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)

    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)

    implementation(libs.barcode.scanning.common)
    implementation (libs.barcode.scanning)
    implementation(libs.vision.common)
    implementation(libs.androidx.runner)
    implementation(libs.core.ktx)
    implementation(libs.androidx.core.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation (libs.retrofit2.kotlinx.serialization.converter)
    implementation (libs.kotlinx.serialization.json)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.converter.scalars)
    implementation (libs.converter.jackson)

    implementation (libs.logging.interceptor)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.kotlinx.coroutines.android)

    implementation (libs.java.jwt)
    implementation (libs.kotlinx.datetime)

    implementation(libs.androidx.recyclerview)
    implementation(libs.picasso)
    implementation(libs.okhttp)
    implementation(libs.moshi.kotlin)

    implementation (libs.gson)

    implementation (libs.jjwt.api)
    implementation (libs.jjwt.impl)
    implementation (libs.jjwt.jackson)


    implementation (libs.jackson.annotations)
    implementation (libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.retrofit)
    implementation(libs.circleimageview)

    implementation (libs.core)
}
