buildscript {
    extra.apply {
        set("compileSdk", 32)
        set("minSdk", 24)
        set("targetSdk", 32)
        set("buildTools", "30.0.3")
    }
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.androidGradlePlugin)
        classpath(libs.safeArgsPlugin)
        classpath(libs.crashlyticsPlugin)
        classpath(libs.hiltGradlePlugin)
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean").configure {
    delete("build")
}