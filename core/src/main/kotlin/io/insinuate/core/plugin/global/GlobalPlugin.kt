package io.insinuate.core.plugin.global

import java.io.File

interface GlobalPlugin {

    val name get() = description.name
    val version get() = description.version

    val pluginClassLoader: ClassLoader
    val description: GlobalPluginDescription
    val dataFolder: File
}