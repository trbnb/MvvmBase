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
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    implementation(project(":mvvmbase"))

    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
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
        name = Publishing.rxJava3ArtifactId
        desc = Publishing.rxJava3Description
        websiteUrl = Publishing.url
        vcsUrl = Publishing.gitUrl
        setLicenses("Apache-2.0")
        publicDownloadNumbers = true
        version.apply {
            name = Publishing.versionName
            desc = Publishing.rxJava3Description
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
                artifactId = Publishing.rxJava3ArtifactId
                version = Publishing.versionName

                pom {
                    packaging = "aar"

                    name.set(Publishing.rxJava3ArtifactId)
                    description.set(Publishing.rxJava3Description)
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
