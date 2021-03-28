plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Android.compileSdk)

    defaultConfig {
        applicationId = "de.trbnb.apptemplate"
        minSdkVersion(21)
        targetSdkVersion(Android.compileSdk)
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        register("releaseConfig") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storeFile = file("/Users/thorben/.android/debug.keystore")
            storePassword = "android"
        }
    }

    buildTypes {
        named("release").configure {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("releaseConfig")
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }

    packagingOptions {
        resources.excludes.add("META-INF/rxkotlin.kotlin_module")
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    implementation(project(":mvvmbase"))
    implementation(project(":mvvmbaseRxJava2"))
    implementation(project(":mvvmbaseRxJava3"))

    // Support library
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.fragment:fragment-ktx:1.3.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.4")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    // Dagger 2
    /*kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
    implementation("com.google.dagger:dagger:${Versions.dagger}")*/
    compileOnly("javax.annotation:jsr250-api:1.0")

    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")



    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.activity:activity-compose:1.3.0-alpha05")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    implementation("androidx.compose.ui:ui:1.0.0-beta03")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta03")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-beta03")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-beta03")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0-beta03")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta03")
    // Integration with activities
    implementation("androidx.activity:activity-compose:1.3.0-alpha05")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha03")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-beta03")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0-beta03")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-beta03")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
