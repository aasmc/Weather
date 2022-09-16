buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Plugins.kotlinGradlePlugin)
        classpath(Plugins.gradleAndroid)
        classpath(Plugins.safeArgs)
        classpath(Plugins.crashlyticsPlugin)
        classpath(Dagger.hiltGradlePlugin)
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