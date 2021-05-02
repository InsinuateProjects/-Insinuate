plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repository.ow2.org/nexus/content/repositories/releases/")
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.ow2.asm:asm:9.1")
}