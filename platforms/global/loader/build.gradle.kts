plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":utils"))
    compileOnly("me.lucko:jar-relocator:1.5")
}