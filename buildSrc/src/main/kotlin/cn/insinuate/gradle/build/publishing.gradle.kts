package cn.insinuate.gradle.build

import org.gradle.kotlin.dsl.*
import java.net.URI

plugins {
    id("maven-publish")
}

val archiveName = "insinuate-${name.toLowerCase()}"

val nexusUrl = "http://mc3.roselle.vip:606/repository"

tasks {
    val sourceJar by registering(Jar::class) {
        from("/src/main/kotlin")
        from("/src/main/java")
        archiveClassifier.set("sources")
    }
}

tasks.withType<Jar> {
    dependencies {
        exclude("org.jetbrains.kotlin:kotlin-stdlib")
        exclude("org.jetbrains.kotlin:kotlin-stdlib-common")
    }

    archiveClassifier.set(null)
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenPublish") {
            artifactId = archiveName
            version = "${rootProject.version}"
            from(components.getByName("java"))
            artifact(tasks.getByName("sourceJar"))
        }
    }
    repositories {
        maven {
            url = if ("${rootProject.version}".endsWith("-SNAPSHOT")) {
                URI("$nexusUrl/maven-snapshots/")
            } else {
                URI("$nexusUrl/maven-releases/")
            }
            credentials {
                username = "${rootProject.extra.get("nexus_username")}"
                password = "${rootProject.extra.get("nexus_password")}"
            }
        }
    }
}

