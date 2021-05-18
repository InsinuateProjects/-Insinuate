package cn.insinuate.core.plugin

import java.io.File

interface GlobalPlugin {
    val server: GlobalServer
    val dataFolder: File
    val pluginClassLoader: ClassLoader
    val logger: GlobalLogger
    val description: PluginDescription
    val name: String get() = description.name
}
