
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.jfrog.bintray") version "1.7.2" // jCenter
    id("com.github.dcendents.android-maven") version "1.5" // maven
    kotlin("jvm") version "1.3.72"
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}

repositories {
    mavenCentral()
}