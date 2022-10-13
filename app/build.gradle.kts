import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 32
    buildToolsVersion = "30.0.3"
    if (project.hasProperty("keystore.properties")) {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))

        signingConfigs {
            getByName("debug") {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(rootDir.absolutePath + keystoreProperties["storeFile"])
                storePassword = keystoreProperties["sorePassword"].toString()
            }
            create("release") {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(rootDir.absolutePath + keystoreProperties["storeFile"])
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    defaultConfig {
        applicationId = "ru.aasmc.weather"
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val API_KEY: String = gradleLocalProperties(rootDir).getProperty("API_KEY")
        val ALGOLIA_API_KEY: String = gradleLocalProperties(rootDir).getProperty("ALGOLIA_API_KEY")
        val ALGOLIA_APP_ID: String = gradleLocalProperties(rootDir).getProperty("ALGOLIA_APP_ID")
        val ALGOLIA_INDEX_NAME: String = gradleLocalProperties(rootDir).getProperty("ALGOLIA_INDEX_NAME")

        buildConfigField("String", "API_KEY", API_KEY)
        buildConfigField("String", "ALGOLIA_API_KEY", ALGOLIA_API_KEY)
        buildConfigField("String", "ALGOLIA_APP_ID", ALGOLIA_APP_ID)
        buildConfigField("String", "ALGOLIA_INDEX_NAME", ALGOLIA_INDEX_NAME)
        buildConfigField("String", "BASE_URL", "\"http://api.openweathermap.org/\"")

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
            correctErrorTypes = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (project.hasProperty("keystore.properties")) {
                signingConfig = signingConfigs.getByName("release")
            }
            isDebuggable = false
        }

        getByName("debug") {
            if (project.hasProperty("keystore.properties")) {
                signingConfig = signingConfigs.getByName("debug")
            }
            isDebuggable = true
        }
    }

    android {
        sourceSets {
            getByName("test").java.srcDir("src/sharedTest/java")
            getByName("androidTest").java.srcDir("src/sharedTest/java")
        }
    }

    hilt {
        enableAggregatingTask = true
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }

    tasks.withType().all {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    packagingOptions {
        resources.excludes.add("**/attach_hotspot_windows.dll")
        resources.excludes.add("META-INF/licenses/**")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.stdlib)
    implementation(libs.appCompat)
    implementation(libs.coreKts)
    implementation(libs.constraintLayout)
    implementation(libs.legacySupport)

    // Material Design
    implementation(libs.material)

    // Room
    implementation(libs.roomRuntime)
    kapt(libs.roomCompiler)
    implementation(libs.roomKtx)

    // Kotlin Coroutines
    implementation(libs.coroutinesAndroid)

    // Navigation Components
    implementation(libs.navigationFragment)
    implementation(libs.navigationUi)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshiConverter)
    kapt(libs.moshiCodeGen)

    // Preferences
    implementation(libs.preferences)

    // Timber
    implementation(libs.timber)

    // Weather Image
    implementation(libs.weatherImage)

    // CalenderView
    implementation(libs.calendarView)

    // Google Play Services
    implementation(libs.googlePlayGms)

    // Algolia Search
    implementation(libs.algoliaSearch)

    // Lifecycle KTX
    implementation(libs.viewModel)
    implementation(libs.liveData)
    implementation(libs.lifecycleCompiler)

    // Paging Library
    implementation(libs.paging)

    // Elastic view
    implementation(libs.elasticViews)

    // WorkManager
    implementation(libs.workManager)

    // Dagger-Hilt
    implementation(libs.daggerHilt)
    implementation(libs.hiltWorker)
    kapt(libs.hiltCompiler)
    kapt(libs.hiltWorkerCompiler)

    // OKHttp Logging Interceptor
    implementation(libs.okhttpInterceptor)

    // Firebase BoM, Crashlytics, Analytics
    implementation(platform(libs.firebaseBom))
    implementation(libs.crashlytics)
    implementation(libs.analytics)

    // AndroidX Test - JVM testing
    testImplementation(libs.bundles.testBundle)
//    testImplementation(libs.coreKtxTest)
//    testImplementation(libs.archCoreTesting)
//    testImplementation(libs.junit)
//    testImplementation(libs.robolectric)
//    testImplementation(libs.hamcrest)
//    testImplementation(libs.coroutinesTest)
//    testImplementation(libs.mockitoCore)

    // AndroidX Test - Instrumented testing
    testImplementation(libs.bundles.androidTestBundle)
//    androidTestImplementation(libs.mockitoCore)
//    androidTestImplementation(libs.testExt)
//    androidTestImplementation(libs.espresso)
//    androidTestImplementation(libs.espressoContrib)
//    androidTestImplementation(libs.espressoIntent)
//    androidTestImplementation(libs.archCoreTesting)
//    androidTestImplementation(libs.coreKtxTest)
//    androidTestImplementation(libs.testRules)
//    androidTestImplementation(libs.coroutineTest)

    // Until the bug at https://issuetracker.google.com/128612536 is fixed
    debugImplementation(libs.fragmentTesting)
    implementation(libs.idlingResource)
}