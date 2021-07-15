package io.insinuate.gradle.build

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
}

val options = PublishOptions(rootProject)

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
        create<MavenPublication>(project.name) {
            project.shadow.component(this)
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