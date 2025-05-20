buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.serialization)
    }
}

plugins {
    id("com.android.application") version "8.6.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.1.0" apply false
}