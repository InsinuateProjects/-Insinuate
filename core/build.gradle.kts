plugins {
    kotlin("jvm")
    id("io.insinuate.gradle.build.publishing")
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public")
}

dependencies {
    implementation(project(":config"))
    compileOnly("org.spongepowered:configurate-yaml:4.0.0")
}
