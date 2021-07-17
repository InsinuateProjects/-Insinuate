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
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    compileOnly(project(":core"))
    implementation(project(":platforms:global:global-loader"))
    implementation(project(":utils"))
    compileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
}

tasks.shadowJar {
    relocate("io.insinuate.utils", "io.insinuate.loader.utils")
}