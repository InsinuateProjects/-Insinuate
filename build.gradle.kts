plugins {
    java
    kotlin("jvm") version "1.4.32"
    `kotlin-dsl`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("maven")
    id("maven-publish")
}

group = "io.insinuate"
version = "0.1.1"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
}
