plugins {
    java
    kotlin("jvm")
    id("io.insinuate.gradle.build.shadowPublishing")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    compileOnly(project(":core"))
    implementation(project(":platforms:global:global-loader"))
    implementation(project(":utils"))
    compileOnly("org.spongepowered:spongeapi:7.4.0")
}

tasks.shadowJar {
    relocate("io.insinuate.utils", "io.insinuate.loader.utils")
}