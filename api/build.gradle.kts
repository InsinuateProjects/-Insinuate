plugins {
    java
    kotlin("jvm")
    id("io.insinuate.gradle.build.publishing")
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public")
}

dependencies {
    implementation(project(":config"))
    implementation(project(":core"))
    implementation(project(":utils"))
}