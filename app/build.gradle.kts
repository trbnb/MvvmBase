plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
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
        jvmTarget = javaVersion.toString()
    }

    buildFeatures {
        dataBinding = true
    }

    packagingOptions {
        exclude("META-INF/rxkotlin.kotlin_module")
    }
}

val daggerVersion = "2.27"
val ankoVersion = "0.10.4"

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))
    testImplementation("junit:junit:4.13")

    implementation(project(":mvvmbase"))
    implementation(project(":mvvmbaseConductor"))
    implementation(project(":mvvmbaseRxJava2"))
    implementation(project(":mvvmbaseRxJava3"))

    // Support library
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.fragment:fragment-ktx:1.2.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.2.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.2.2")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    // Dagger 2
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
    compileOnly("javax.annotation:jsr250-api:1.0")
    compileOnly("com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2")
    kapt("com.squareup.inject:assisted-inject-processor-dagger2:0.5.2")

    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    implementation("com.bluelinelabs:conductor:2.1.5")
    implementation("com.bluelinelabs:conductor-archlifecycle:2.1.5")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
