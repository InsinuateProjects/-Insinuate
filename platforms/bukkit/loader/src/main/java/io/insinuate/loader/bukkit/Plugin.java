package io.insinuate.loader.bukkit;

import io.insinuate.loader.InsinuateInitializer;
import io.insinuate.loader.PluginBase;
import io.insinuate.loader.PluginLibs;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements PluginBase {

    public Plugin() {
        InsinuateInitializer.init();
    }
}
