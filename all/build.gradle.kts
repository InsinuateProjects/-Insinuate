plugins {
    java
    kotlin("jvm")
    id("io.insinuate.gradle.build.shadowPublishing")
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public")
}

dependencies {
    implementation(project(":config"))
    implementation(project(":core"))
    implementation(project(":utils"))
    implementation(project(":platforms:bukkit"))
    implementation(project(":platforms:bungee"))
    implementation(project(":platforms:sponge"))
    implementation(project(":platforms:nukkit"))
    implementation(project(":platforms:velocity"))
}