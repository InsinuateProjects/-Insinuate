package io.insinuate.core

import io.insinuate.core.plugin.insinuate.InsinuatePlugin
import io.insinuate.core.plugin.global.GlobalPluginDescription

object Insinuate: InsinuatePlugin {

    private lateinit var loader: InsinuateLoader

    override val pluginClassLoader by lazy { loader.bootClassLoader }
    override val description: GlobalPluginDescription
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