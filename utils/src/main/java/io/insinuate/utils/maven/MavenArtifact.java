package io.insinuate.utils.maven;

import io.insinuate.utils.IO;
import io.insinuate.utils.Pair;
import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class MavenArtifact {

    public File folder;
    public final String groupId;
    public final String artifactId;
    public final String version;

    public List<String> repositories = new ArrayList<>();
    public List<Relocation> relocates;

    public File file;

    private final List<String> defaultRepositories = Arrays.asList(
            "https://repo.maven.apache.org/maven2",
            "https://repo1.maven.org/maven2/",
            "https://maven.aliyun.com/nexus/content/groups/public/"
    );

    public MavenArtifact(String groupId, String artifactId, String version, String... repositories) {
        this(null, groupId, artifactId, version, new ArrayList<>(), repositories);
    }
    public MavenArtifact(String groupId, String artifactId, String version, Relocation relocate, String... repositories) {
        this(null, groupId, artifactId, version, relocate, repositories);
    }
    public MavenArtifact(String groupId, String artifactId, String version, List<Relocation> relocates, String... repositories) {
        this(null, groupId, artifactId, version, relocates, repositories);
    }

    public MavenArtifact(File folder, String groupId, String artifactId, String version, Relocation relocate, String... repositories) {
        this(folder, groupId, artifactId, version, Arrays.asList(relocate), repositories);
    }
    public MavenArtifact(File folder, String groupId, String artifactId, String version, List<Relocation> relocates, String... repositories) {
        this.relocates = relocates;
        if (folder == null) {
            this.folder = new File(".insinuate/libs/other");
        } else {
            this.folder = folder;
        }
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        file = new File(folder, artifactId + "-" + version + ".jar");

        this.repositories.addAll(Arrays.asList(repositories));
        this.repositories.addAll(defaultRepositories);
    }

    public static File customFolder(String name) {
        return new File(".insinuate/libs/" + name);
    }

    public MavenArtifact(String combined, String... repositories) {
        this(null, combined, new ArrayList<>(), repositories);
    }
    public MavenArtifact(String combined, Relocation relocate, String... repositories) {
        this(null, combined, relocate, repositories);
    }
    public MavenArtifact(String combined, List<Relocation> relocates, String... repositories) {
        this(null, combined, relocates, repositories);
    }

    public MavenArtifact(File folder, String combined, String... repositories) {
        this(folder, combined, new ArrayList<>(), repositories);
    }
    public MavenArtifact(File folder, String combined, Relocation relocate, String... repositories) {
        this(folder, combined, Arrays.asList(relocate), repositories);
    }
    public MavenArtifact(File folder, String combined, List<Relocation> relocates, String... repositories) {
        this.relocates = relocates;
        if (folder == null) {
            this.folder = new File(".insinuate/libs");
        } else {
            this.folder = folder;
        }
        String[] split = combined.split(":");
        this.groupId = split[0];
        this.artifactId = split[1];
        this.version = split[2];
        file = new File(folder, artifactId + "-" + version + ".jar");

        this.repositories.addAll(Arrays.asList(repositories));
        this.repositories.addAll(defaultRepositories);
    }

    public String toURL(String repository) {
        StringBuilder builder = new StringBuilder(repository);
        if (!repository.endsWith("/")) builder.append("/");

        builder.append(groupId.replace(".", "/")).append("/");
        builder.append(artifactId).append("/");
        builder.append(version).append("/");
        builder.append(artifactId).append("-").append(version).append(".jar").append("/");

        return builder.toString();
    }

    public boolean download() {
        for (String repository : repositories) {
            if (download(repository)) {
                return true;
            }
        }
        return false;
    }

    private String toURLFormat() {
        return groupId.replace(".", "/") + "/" +
                artifactId + "/" +
                version + "/" +
                artifactId + "-" + version + ".jar";
    }



    public boolean download(String repository) {
        try {
            URL url = new URL(toURL(repository));

            if (file.exists()) {
                return false;
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] bytes = IO.readInputStream(inputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            inputStream.close();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public boolean relocate() {
        File input = new File(file.getName() + ".relocate");
        IO.deepDelete(input);
        IO.copy(file, input);
        IO.deepDelete(file);

        JarRelocator jarRelocator = new JarRelocator(input, file, relocates);

        boolean result;
        try {
            jarRelocator.run();
            IO.deepDelete(input);
            result = true;
        } catch (Throwable t) {
            result = false;
        }

        return result;
    }

    public String getFullName() {
        return artifactId + "-" + version;
    }


}
