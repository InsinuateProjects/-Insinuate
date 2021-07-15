import org.jetbrains.kotlin.gradle.targets.js.npm.includedRange

plugins {
    java
    kotlin("jvm")
    id("io.insinuate.gradle.build.shadowPublishing")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
}

dependencies {
    compileOnly(project(":core"))
    implementation(project(":platforms:global:global-loader"))
    implementation(project(":utils"))
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.9")
}