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
    testImplementation("junit", "junit", "4.12")
    implementation("commons-io:commons-io:2.8.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.ow2.asm:asm:9.1")
}
