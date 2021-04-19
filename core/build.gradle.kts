plugins {
    java
    kotlin("jvm")
    id("io.insinuate.insinuate.build.publishing")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
}
