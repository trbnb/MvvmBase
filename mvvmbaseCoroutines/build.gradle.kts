plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
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
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    implementation(project(":mvvmbase"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    testAnnotationProcessor("androidx.databinding:databinding-compiler:$gradleToolsVersion")
    kaptTest("androidx.databinding:databinding-compiler:$gradleToolsVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}

val sourcesJar = task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

val javadoc = task<Javadoc>("javadoc") {
    isFailOnError = false
    source = android.sourceSets["main"].java.sourceFiles
    classpath += project.files(android.bootClasspath.joinToString(separator = File.pathSeparator))
}

val javadocJar = task<Jar>("javadocJar") {
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.destinationDir)
}

// Bintray
bintray.apply {
    user = Publishing.getBintrayUser(rootProject)
    key = Publishing.getBintrayApiKey(rootProject)
    publish = true
    setPublications("release")

    pkg.apply {
        repo = Publishing.groupId
        name = Publishing.coroutinesArtifactId
        desc = Publishing.coroutinesDescription
        websiteUrl = Publishing.url
        vcsUrl = Publishing.gitUrl
        setLicenses("Apache-2.0")
        publicDownloadNumbers = true
        version.apply {
            name = Publishing.versionName
            desc = Publishing.coroutinesDescription
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
                artifact(javadocJar)

                groupId = Publishing.groupId
                artifactId = Publishing.coroutinesArtifactId
                version = Publishing.versionName

                pom {
                    packaging = "aar"

                    name.set(Publishing.coroutinesArtifactId)
                    description.set(Publishing.coroutinesDescription)
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
