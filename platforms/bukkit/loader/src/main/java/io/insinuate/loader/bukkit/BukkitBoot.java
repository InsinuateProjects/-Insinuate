package io.insinuate.loader.bukkit;

import io.insinuate.loader.InsinuateInitializer;
import io.insinuate.loader.PluginBase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitBoot extends JavaPlugin implements PluginBase {

    public BukkitBoot() {
        if (!InsinuateInitializer.isCoreLoaded()) {
            InsinuateInitializer.init(this);
        }
    }

    @Override
    public ClassLoader getBootClassLoader() {
        return Bukkit.class.getClassLoader();
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return getClassLoader();
    }
}
