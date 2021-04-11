plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter {
        content {
            includeGroup("org.ajoberstar.grgit")
            includeGroup("org.ajoberstar.grgit.gradle")
        }
    }
    gradlePluginPortal()
}
dependencies {
    implementation(gradleApi())
}
