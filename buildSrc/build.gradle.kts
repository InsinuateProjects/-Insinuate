plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io/")
}
dependencies {
    implementation(gradleApi())
    implementation("com.github.johnrengelman:shadow:7.0.0")
}
