plugins {
    kotlin("jvm")
    id("io.insinuate.gradle.build.publishing")
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
    implementation(project(":utils"))
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:${Versions.placeholderapiVersion}")
}