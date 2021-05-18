package cn.insinuate.loader;

import cn.insinuate.utils.Pair;
import cn.insinuate.utils.maven.MavenArtifact;

import java.util.Arrays;
import java.util.List;

public class PluginLibs {
    public final List<MavenArtifact> artifacts;

    public PluginLibs(List<MavenArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    public boolean process() {
        for (MavenArtifact artifact : artifacts) {
            if (artifact.file.exists()) {
                continue;
            }
            System.out.println("[Insinuate | Inf] 正在下载 " + artifact.artifactId + "-" + artifact.version);
            if (!artifact.download()) {
                System.err.println("[Insinuate | Err] 下载 " + artifact.artifactId + "-" + artifact.version + " 失败, 请检查您的网络.");
                return false;
            }

            System.out.println("[Insinuate | Inf] 正在转移 " + artifact.artifactId + "-" + artifact.version + " 的 packages.");
            if (!artifact.relocate()) {
                System.err.println("[Insinuate | Err] 转移 " + artifact.artifactId + "-" + artifact.version + " 的 packages 发生错误, 请反馈以上错误信息.");
                return false;
            }
        }
        return true;
    }


    public final PluginLibs insinuateLibs = new PluginLibs(
            Arrays.asList(
                new MavenArtifact("org.jetbrains.kotlin:kotlin-stdlib:1.5.0", Arrays.asList(
                        new Pair<>("kotlin", "cn.insinuate.libs.kotlin")
                )),
                new MavenArtifact("commons-io:commons-io:2.8.0", Arrays.asList(
                        new Pair<>("org.apache", "cn.insinuate.libs.org.apache")
                ))
    ));

    public static boolean isCoreLoaded() {
        try {
            Class.forName("cn.insinuate.core.Insinuate");
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
