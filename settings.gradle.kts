rootProject.name = "Insinuate"

include(":core", ":utils", ":config", ":global-loader", "loader-all")

arrayOf("bukkit", "bungee", "sponge", "velocity", "nukkit").forEach {
    include(":platforms:$it")
    include(":platforms:$it:loader")
}
