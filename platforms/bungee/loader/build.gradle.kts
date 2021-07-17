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
    maven("https://hub.spigotmc.org/nexus/content/repositories/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
}

dependencies {
    compileOnly(project(":core"))
    implementation(project(":platforms:global:global-loader"))
    implementation(project(":utils"))
    compileOnly("net.md-5:bungeecord-api:1.17-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    relocate("io.insinuate.utils", "io.insinuate.loader.utils")
}