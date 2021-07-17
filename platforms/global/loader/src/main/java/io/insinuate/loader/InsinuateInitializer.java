package io.insinuate.loader;

import io.insinuate.core.Insinuate;
import io.insinuate.core.InsinuateLoader;
import io.insinuate.utils.maven.MavenArtifact;
import me.lucko.jarrelocator.Relocation;

import java.io.File;
import java.util.Arrays;

public class InsinuateInitializer {

    private static InsinuateInitializer instance;
    public final ClassLoader bootLoader;

    private static final PluginLibs insinuateLibs = new PluginLibs(
            Arrays.asList(
                    new MavenArtifact(new File(MavenArtifact.libsFolder, "internal"),
                            "org.jetbrains.kotlin:kotlin-stdlib:1.5.21",
                            new Relocation("kotlin", "io.insinuate.share.kotlin")
                    ),
                    new MavenArtifact(new File(MavenArtifact.libsFolder, "internal"),
                            "commons-io:commons-io:2.8.0",
                            new Relocation("org.apache", "io.insinuate.share.org.apache")
                    ),
                    new MavenArtifact(new File(MavenArtifact.libsFolder, "internal"),
                            "org.spongepowered:configurate-yaml:4.1.1",
                            new Relocation("org.spongepowered.configurate", "io.insinuate.share.org.spongepowered.configurate")
                    )
            ));
    private static final PluginLibs insinuateCores = new PluginLibs(
            Arrays.asList(
                    new MavenArtifact(new File(MavenArtifact.libsFolder, "core"),
                            "io.insinuate:Insinuate-all:0.1.0",
                            "https://gitee.com/InsinuateProjects/InsinuateDownload/raw/master/"
                    )
            ));

    private InsinuateInitializer(PluginBase bootPlugin) {
        bootLoader = bootPlugin.getBootClassLoader();
        while (true) if (insinuateLibs.getFromNetwork()) {
            break;
        } else {
            System.out.println("[Insinuate | Inf] 再次尝试进行下载...");
        }
        if (!insinuateLibs.include(bootLoader)) {
            System.err.println("[Insinuate | Err] 内部依赖文件导入失败, 启动失败!");
        }


        while (true) if (insinuateCores.getFromNetwork()) {
            break;
        } else {
            System.out.println("[Insinuate | Inf] 再次尝试进行下载...");
        }
        if (!insinuateCores.include(bootLoader)) {
            System.err.println("[Insinuate | Err] Insinuate 本体导入失败, 启动失败!");
        }

        try {
            Insinuate.initialize(new InsinuateLoader(bootLoader, new File(".insinuate")));
        } catch (Throwable ignored) {
            System.err.println("[Insinuate | Err] Insinuate 已经由另一个插件引导启动!");
        }
    }

    public static InsinuateInitializer getInstance() {
        return instance;
    }

    public static void init(PluginBase bootPlugin) throws ExceptionInInitializerError {
        if (instance != null) {
            throw new ExceptionInInitializerError();
        }

        new InsinuateInitializer(bootPlugin);
    }

    public static boolean isCoreLoaded() {
        try {
            Class.forName("io.insinuate.core.Insinuate", false, instance.bootLoader);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
