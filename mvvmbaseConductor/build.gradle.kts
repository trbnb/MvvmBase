plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    `maven-publish`
    signing
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

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1")

    implementation(project(":mvvmbase"))

    api("com.bluelinelabs:conductor:2.1.5")
    api("com.bluelinelabs:conductor-archlifecycle:2.1.5")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

val sourcesJar = task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

signing {
    sign(publishing.publications)
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                val releaseRepo = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotRepo = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                name = "Sonatype"
                setUrl(releaseRepo)
                credentials {
                    username = Publishing.getOssrhUsername(rootProject)
                    password = Publishing.getOssrhPassword(rootProject)
                }
            }
        }
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                artifact(sourcesJar)

                groupId = Publishing.groupId
                artifactId = Publishing.conductorArtifactId
                version = Publishing.versionName

                pom {
                    packaging = "aar"

                    name.set(Publishing.conductorArtifactId)
                    description.set(Publishing.conductorDescription)
                    url.set(Publishing.url)

                    developers {
                        developer {
                            id.set("trbnb")
                            name.set("Thorben Buchta")
                            email.set("thorbenbuchta@gmail.com")
                            url.set("https://www.trbnb.de")
                        }
                    }

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
