package cn.insinuate.loader;

public class InsinuateInitializer {

    private static InsinuateInitializer instance;

    private InsinuateInitializer() {
        if (!PluginLibs.isCoreLoaded()) {

        }
    }

    public static InsinuateInitializer getInstance() {
        return instance;
    }

    public static InsinuateInitializer init() throws ExceptionInInitializerError {
        if (instance != null) {
            throw new ExceptionInInitializerError();
        }

        return new InsinuateInitializer();
    }
}
