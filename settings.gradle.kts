rootProject.name = "Insinuate"

include("server", "utils", "config", "loader")

arrayOf("bukkit", "bungee", "sponge", "velocity", "nukkit").forEach {
    include(":platforms:$it")
    include(":platforms:$it:loader")
}
