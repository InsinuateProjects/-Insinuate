package io.insinuate.core

import io.insinuate.core.plugin.InsinuatePlugin
import io.insinuate.core.plugin.PluginDescription
import java.io.File

object Insinuate: InsinuatePlugin {

    private lateinit var loader: InsinuateLoader

    override val pluginClassLoader by lazy { loader.bootClassLoader }
    override val description: PluginDescription
        get() = TODO("Not yet implemented")
    override val dataFolder by lazy { loader.insinuateFolder }

    @JvmStatic
    @Throws(ExceptionInInitializerError::class)
    fun initialize(loader: InsinuateLoader) {
        if (Insinuate::loader.isInitialized) {
            throw ExceptionInInitializerError()
        }
        this.loader = loader
    }

    init {

    }
}