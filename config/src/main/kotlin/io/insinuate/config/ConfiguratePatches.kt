package io.insinuate.config

import org.spongepowered.configurate.ScopedConfigurationNode
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import java.io.InputStream

fun <T : AbstractConfigurationLoader.Builder<T, L>, L : AbstractConfigurationLoader<*>> AbstractConfigurationLoader.Builder<T, L>.inputStream(inputStream: InputStream): T {
    return this.source { inputStream.bufferedReader() }
}

fun <T : AbstractConfigurationLoader.Builder<T, L>, L : AbstractConfigurationLoader<*>> AbstractConfigurationLoader.Builder<T, L>.string(string: String): T {
    return this.source { string.reader().buffered() }
}

fun <N : ScopedConfigurationNode<N>> ScopedConfigurationNode<N>.ignoreNode(vararg path: Any): N {
    if (path.isEmpty()) return this.node(*path)
    val first = path[0]
    if (first !is String) return this.node(*path)

    childrenMap().keys.forEach {
        if (it !is String) return@forEach

        if (first.equals(it, ignoreCase = true)) {
            return this.node(it).ignoreNode(path.sliceArray(1 until path.size))
        }
    }

    return this.node(*path)

}