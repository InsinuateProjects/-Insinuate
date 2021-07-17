plugins {
    java
    kotlin("jvm")
    id("io.insinuate.gradle.build.shadowPublishing")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://nexus.velocitypowered.com/repository/velocity-artifacts-snapshots/")
}

dependencies {
    compileOnly(project(":core"))
    implementation(project(":platforms:global:global-loader"))
    implementation(project(":utils"))
    compileOnly("com.velocitypowered:velocity-api:4.0.0-SNAPSHOT")
}

tasks.shadowJar {
    relocate("io.insinuate.utils", "io.insinuate.loader.utils")
}