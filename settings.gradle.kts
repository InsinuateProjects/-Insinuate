rootProject.name = "Insinuate"

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit = {}) {
    include(name)
    project(name).apply(block)
}

include(":core", ":utils", ":config")

arrayOf("bukkit", "bungee", "sponge", "velocity", "nukkit", "global").forEach {
    setupSubproject(":platforms:$it")
    setupSubproject(":platforms:$it:$it-loader") {
        projectDir = file("platforms/$it/loader")
    }
    setupSubproject(":platforms:$it:$it-chat") {
        projectDir = file("platforms/$it/chat")
    }
}
