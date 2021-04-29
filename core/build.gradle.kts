plugins {
    java
    kotlin("jvm")
    id("cn.insinuate.gradle.build.publishing")
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public")
}

dependencies {
    testImplementation("junit", "junit", "4.12")
}
