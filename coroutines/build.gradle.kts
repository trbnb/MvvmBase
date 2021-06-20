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
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    implementation(project(":databinding"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
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
            create(Publication.COROUTINES, this@afterEvaluate)
        }
    }
}
