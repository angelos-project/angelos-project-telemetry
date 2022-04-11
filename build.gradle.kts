plugins {
    base
}

repositories {
    mavenCentral()
}

group = ProjectInfo.name
version = ProjectInfo.version

allprojects {
    repositories {
        mavenCentral()
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
    }
}