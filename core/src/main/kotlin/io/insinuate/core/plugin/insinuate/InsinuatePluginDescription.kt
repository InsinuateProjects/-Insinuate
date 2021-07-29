package io.insinuate.core.plugin.insinuate

import io.insinuate.core.plugin.global.GlobalPluginDescription

interface InsinuatePluginDescription : GlobalPluginDescription {

    val bukkitSupport: Boolean
    val bungeeSupport: Boolean
    val nukkitSupport: Boolean
    val spongeSupport: Boolean
    val velocitySupport: Boolean

    companion object {
/*
        @JvmStatic
        fun readFromResource(classLoader: ClassLoader): GlobalPluginDescription {
            String(classLoader.getResourceAsStream("insinuate-plugin.yml").readBytes())



        }*/

    }
}