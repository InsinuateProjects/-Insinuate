plugins {
    java
    kotlin("jvm") version "1.4.32"
    `kotlin-dsl`
}

allprojects {
    group = "io.insinuate"
    version = "0.1.0".let {
        if (rootProject.tasks.names.contains("release")) {
            return@let it
        } else {
            return@let "${it}-SNAPSHOT"
        }
    }
}

extra.apply {
    val folder = File(rootDir, ".gradle/nexus")
    val userFile = File(folder, "username.txt")
    val passFile = File(folder, "password.txt")

    if (!folder.exists()) {
        folder.mkdirs()
    }
    if (!userFile.exists()) {
        userFile.writeText("your username")
    }
    if (!passFile.exists()) {
        passFile.writeText("your password")
    }

    set("nexus_username", userFile.readText())
    set("nexus_password", passFile.readText())

    println("[INSINUATE | NEXUS] Your nexus username is: ${get("nexus_username")}, password is: ${get("nexus_password")}")
}

repositories {
    jcenter()
    mavenCentral()
}

task("release") {

}

dependencies {
    testCompile("junit", "junit", "4.12")
}
