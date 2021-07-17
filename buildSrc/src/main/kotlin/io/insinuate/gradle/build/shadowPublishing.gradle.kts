package io.insinuate.gradle.build

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
}

val options = PublishOptions(rootProject)
val archiveName = "insinuate-${name.toLowerCase()}"

tasks {
    val sourceJar by registering(Jar::class) {
        from("/src/main/kotlin")
        from("/src/main/java")
        archiveClassifier.set("sources")
    }
}

tasks.withType<ShadowJar> {
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common"))
    }

    archiveClassifier.set(null)
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            artifactId = archiveName
            version = "${rootProject.version}"
            project.shadow.component(this)
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