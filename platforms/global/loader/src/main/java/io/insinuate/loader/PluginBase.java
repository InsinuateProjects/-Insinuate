package io.insinuate.loader;

public interface PluginBase {

    default PluginLibs getLibs() {
        return new PluginLibs();
    };

}
