plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(project(":utils"))
}