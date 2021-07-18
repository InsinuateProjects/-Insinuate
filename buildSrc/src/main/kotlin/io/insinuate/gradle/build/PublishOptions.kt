package io.insinuate.gradle.build

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.io.File
import java.net.URI

class PublishOptions(rootProject: Project) {
    val url = "http://repo.insinuate.cn/repository"
    val uri by lazy { if ("${rootProject.version}".endsWith("-SNAPSHOT")) URI("${url}/maven-snapshots/") else URI("${url}/maven-releases/") }

    companion object {
        val accessFile = File("access.txt")
        val username: String
        val password: String
        val gitee_username: String
        val gitee_password: String
        val downloaderRepository: String = "https://gitee.com/InsinuateProjects/InsinuateDownload.git"
        val downloaderFolder: File = File("gitee/InsinuateProjects/InsinuateDownload")

        init {
            downloaderFolder.mkdirs()
            if (!accessFile.exists()) {
                accessFile.writeText("""
                your_username
                your_password
                your_gitee_username
                your_gitee_password
            """.trimIndent())
            }
            accessFile.readLines().let {
                username = it[0]
                password = it[1]
                println("[INSINUATE | NEXUS] From file 'access.txt' loaded access '$username'(username)!")
                gitee_username = it[0]
                gitee_password = it[1]
                println("[INSINUATE | NEXUS] From file 'access.txt' loaded access '$gitee_username'(gitee_username)!")
            }
        }
    }
}