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

    buildFeatures {
        dataBinding = true
    }

    dataBinding {
        // This is necessary to allow the data binding annotation processor to generate
        // the BR fields from Bindable annotations
        testOptions.unitTests.isIncludeAndroidResources = true

        isEnabledForTests = true
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    kotlin {
        explicitApi()
    }

    kotlinOptions {
        jvmTarget = Versions.java.toString()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    implementation(project(":databinding"))

    testAnnotationProcessor("androidx.databinding:databinding-compiler:${Versions.gradleTools}")
    kaptTest("androidx.databinding:databinding-compiler:${Versions.gradleTools}")

    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
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
            create(Publication.RX_JAVA_3, this@afterEvaluate)
        }
    }
}
