package io.insinuate.core.plugin.global

interface GlobalPluginDescription {

    val name: String
    val version: String
    val description: String
    val authors: Array<String>
}