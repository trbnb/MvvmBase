// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath(group = "com.android.tools.build", name = "gradle", version = "7.0.0-alpha12")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.jfrog.bintray") version "1.8.5" // jCenter
    kotlin("jvm") version kotlinVersion
    id("io.gitlab.arturbosch.detekt") version "1.9.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        android.set(true)
        // disable lexicographic import ordering rule
        disabledRules.set(listOf("import-ordering"))
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        buildUponDefaultConfig = true
        config = files(
            (when (name) {
                "app" -> projectDir.canonicalPath
                else -> project.rootDir.canonicalPath
            } + File.separator + "detekt-config.yml")
        )
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}