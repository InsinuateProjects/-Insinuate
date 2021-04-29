plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.aliyun.com/repository/central/")
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.ow2.asm:asm:9.1")
}