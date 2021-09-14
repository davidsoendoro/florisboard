plugins {
    base // adds clean task to root project
}

subprojects {
    repositories {
        mavenCentral()
        google()
    }
}

buildscript {
    repositories {
        google()  // Google's Maven repository
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
    }
}

allprojects {
    repositories {
        google()  // Google's Maven repository
    }
}
