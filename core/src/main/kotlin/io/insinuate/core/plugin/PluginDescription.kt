package io.insinuate.core.plugin

interface PluginDescription {

    val name: String
    val version: String
    val description: String
    val authors: Array<String>

    companion object {

    }
}