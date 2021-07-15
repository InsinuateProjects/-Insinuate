package io.insinuate.loader;

import io.insinuate.utils.maven.MavenArtifact;

import java.util.Arrays;

public class InsinuateInitializer {

    private static InsinuateInitializer instance;

    private static final PluginLibs insinuateLibs = new PluginLibs(
            Arrays.asList(
                    new MavenArtifact("org.jetbrains.kotlin:kotlin-stdlib:1.5.20"),
                    new MavenArtifact("commons-io:commons-io:2.8.0")
            ));

    private InsinuateInitializer() {
        while (true) if (insinuateLibs.process()) {
            break;
        } else {
            System.out.println("[Insinuate | Inf] 再次尝试进行下载...");
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
