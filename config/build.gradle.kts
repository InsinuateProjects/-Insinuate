plugins {
    java
    kotlin("jvm")
    id("io.insinuate.gradle.build.publishing")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.spongepowered:configurate-yaml:4.0.0")
    testImplementation("junit", "junit", "4.12")
}
