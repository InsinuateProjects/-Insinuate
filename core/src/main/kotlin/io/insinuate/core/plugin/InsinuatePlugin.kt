package io.insinuate.core.plugin

import java.io.File

interface InsinuatePlugin {

    val name get() = description.name
    val version get() = description.version

    val pluginClassLoader: ClassLoader
    val description: PluginDescription
    val dataFolder: File

}