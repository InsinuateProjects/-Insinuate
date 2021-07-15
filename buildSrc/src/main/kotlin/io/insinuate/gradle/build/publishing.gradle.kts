package io.insinuate.gradle.build

import org.gradle.kotlin.dsl.*

plugins {
    id("maven-publish")
}

val options = PublishOptions(rootProject)

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
            artifactId = options.archiveName
            version = "${rootProject.version}"
            from(components.getByName("java"))
            artifact(tasks.getByName("sourceJar"))
        }
    }
    repositories {
        maven {
            url = options.uri
            credentials {
                username = PublishOptions.username
                password = PublishOptions.password
            }
        }
    }
}