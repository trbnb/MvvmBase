plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    `maven-publish`
    signing
}

android {
    compileSdk = Android.compileSdk

    defaultConfig {
        minSdk = Android.minSdk
        targetSdk = Android.compileSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("proguard-rules.pro")
    }

    buildTypes {
        named("release").configure {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    kotlinOptions {
        jvmTarget = Versions.java.toString()
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    // Support library
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.fragment:fragment-ktx:1.3.3")
    implementation("androidx.recyclerview:recyclerview:1.2.0")

    implementation("androidx.compose.runtime:runtime:${Versions.compose}")
    implementation("androidx.compose.ui:ui:${Versions.compose}")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")

    // Lifecycle architecture components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1")

    // Java inject API for dependency injection
    api("javax.inject:javax.inject:1")
}

repositories {
    mavenCentral()
    google()
}

val sourcesJar = task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from((android.sourceSets["main"].java as com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet).srcDirs)
}

signing {
    sign(publishing.publications)
}

afterEvaluate {
    publishing {
        repositories {
            mavenCentralUpload(project)
        }
        publications {
            create(Publication.CORE, this@afterEvaluate)
        }
    }
}
