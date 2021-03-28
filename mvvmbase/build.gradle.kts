plugins {
    id("com.android.library")
    kotlin("android")
    id("com.jfrog.bintray")
    `maven-publish`
}

android {
    compileSdkVersion(Android.compileSdk)

    defaultConfig {
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.compileSdk)
        versionCode = Publishing.versionCode
        versionName = Publishing.versionName

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
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
        useIR = true
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
    implementation("androidx.fragment:fragment-ktx:1.3.2")
    implementation("androidx.recyclerview:recyclerview:1.1.0")

    implementation("androidx.compose.runtime:runtime:${Versions.compose}")
    implementation("androidx.compose.ui:ui:${Versions.compose}")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

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
    from(android.sourceSets["main"].java.srcDirs)
}

// Bintray
bintray.apply {
    user = Publishing.getBintrayUser(rootProject)
    key = Publishing.getBintrayApiKey(rootProject)
    publish = true
    setPublications("release")

    pkg.apply {
        repo = Publishing.groupId
        name = Publishing.mainArtifactId
        desc = Publishing.mainDescription
        websiteUrl = Publishing.url
        vcsUrl = Publishing.gitUrl
        setLicenses("Apache-2.0")
        publicDownloadNumbers = true
        version.apply {
            name = Publishing.versionName
            desc = Publishing.mainDescription
            gpg.apply {
                sign = true
                passphrase = Publishing.getBintrayGpgPassphrase(rootProject)
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                artifact(sourcesJar)

                groupId = Publishing.groupId
                artifactId = Publishing.mainArtifactId
                version = Publishing.versionName

                pom {
                    packaging = "aar"

                    name.set(Publishing.mainArtifactId)
                    description.set(Publishing.mainDescription)
                    url.set(Publishing.url)

                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set(Publishing.licenseUrl)
                        }
                    }

                    scm {
                        connection.set(Publishing.gitUrl)
                        developerConnection.set(Publishing.gitUrl)
                        url.set(Publishing.url)
                    }
                }
            }
        }
    }
}
