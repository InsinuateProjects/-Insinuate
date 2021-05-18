package cn.insinuate.utils.maven;

import cn.insinuate.utils.IO;
import cn.insinuate.utils.Pair;
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

    public List<String> repositories = Arrays.asList(
            "https://repo.maven.apache.org/maven2",
            "https://repo1.maven.org/maven2/",
            "https://maven.aliyun.com/nexus/content/groups/public/"
    );
    public Map<String, String> relocates;

    public File file;

    public MavenArtifact(String groupId, String artifactId, String version, Pair<String, String> relocate, String... repositories) {
        this(null, groupId, artifactId, version, relocate, repositories);
    }
    public MavenArtifact(String groupId, String artifactId, String version, List<Pair<String, String>> relocates, String... repositories) {
        this(null, groupId, artifactId, version, relocates, repositories);
    }
    public MavenArtifact(String groupId, String artifactId, String version, Map<String, String> relocates, String... repositories) {
        this(null, groupId, artifactId, version, relocates, repositories);
    }

    public MavenArtifact(File folder, String groupId, String artifactId, String version, Pair<String, String> relocate, String... repositories) {
        this(folder, groupId, artifactId, version, Pair.pairToMap(relocate), repositories);
    }
    public MavenArtifact(File folder, String groupId, String artifactId, String version, List<Pair<String, String>> relocates, String... repositories) {
        this(folder, groupId, artifactId, version, Pair.pairsToMap(relocates), repositories);
    }
    public MavenArtifact(File folder, String groupId, String artifactId, String version, Map<String, String> relocates, String... repositories) {
        this.relocates = relocates;
        if (folder == null) {
            this.folder = new File(".insinuate/libs");
        } else {
            this.folder = folder;
        }
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        file = new File(folder, artifactId + "-" + version + ".jar");

        this.repositories.addAll(0, Arrays.asList(repositories));
    }

    public MavenArtifact(String combined, Pair<String, String> relocate, String... repositories) {
        this(null, combined, relocate, repositories);
    }
    public MavenArtifact(String combined, List<Pair<String, String>> relocates, String... repositories) {
        this(null, combined, relocates, repositories);
    }
    public MavenArtifact(String combined, Map<String, String> relocates, String... repositories) {
        this(null, combined, relocates, repositories);
    }

    public MavenArtifact(File folder, String combined, Pair<String, String> relocate, String... repositories) {
        this(folder, combined, Pair.pairToMap(relocate), repositories);
    }
    public MavenArtifact(File folder, String combined, List<Pair<String, String>> relocates, String... repositories) {
        this(folder, combined, Pair.pairsToMap(relocates), repositories);
    }
    public MavenArtifact(File folder, String combined, Map<String, String> relocates, String... repositories) {
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

        this.repositories.addAll(0, Arrays.asList(repositories));
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

    private String toURL() {
        return groupId.replace(".", "/") + "/" +
                artifactId + "/" +
                version + "/" +
                artifactId + "-" + version + ".jar";
    }

    public boolean download(String repository) {
        return IO.downloadFile(toURL(repository), file);
    }

    private boolean startsWith(String name) {
        for (String key : relocates.keySet()) {
            if (name.startsWith(key)) return true;
        }
        return false;
    }

    public boolean relocate() {
        JarFile jarFile;
        FileOutputStream fileOutputStream;
        JarOutputStream jarOutputStream;
        File coped = new File(file.getName() + ".relocate");
        IO.deepDelete(coped);
        IO.copy(file, coped);
        IO.deepDelete(file);
        try {
            jarFile = new JarFile(coped);
            fileOutputStream = new FileOutputStream(file);
            jarOutputStream = new JarOutputStream(fileOutputStream);
            jarOutputStream.setMethod(JarOutputStream.DEFLATED);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            try {
                JarEntry jarEntry = entries.nextElement();

                String entryName = jarEntry.getName().replace("/", ".");
                entryName = entryName.substring(0, entryName.lastIndexOf("."));
                boolean startsWith = startsWith(entryName);

                String finallyName = jarEntry.getName();

                for (Map.Entry<String, String> relocate : relocates.entrySet()) {
                    String string = finallyName.replaceFirst(relocate.getKey(), relocate.getValue());
                    if (!finallyName.equals(string)) {
                        finallyName = string;
                        break;
                    }
                }

                if (jarEntry.getName().startsWith("META-INF/") || !jarEntry.getName().endsWith(".class")) {
                    if (startsWith) {
                        jarOutputStream.putNextEntry(new JarEntry(finallyName));
                    } else {
                        jarOutputStream.putNextEntry(jarEntry);
                    }
                    jarOutputStream.write(IO.readInputStream(jarFile.getInputStream(jarEntry)));
                    continue;
                }

                ClassReader classReader = new ClassReader(jarFile.getInputStream(jarEntry));
                ClassWriter classWriter = new ClassWriter(classReader, 0);

                ClassVisitor classVisitor = new RelocateClassVisitor(classWriter, relocates);
                classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);

                byte[] bytes = classWriter.toByteArray();

                if (startsWith) {
                    jarOutputStream.putNextEntry(new JarEntry(finallyName));
                } else {
                    jarOutputStream.putNextEntry(jarEntry);
                }
                jarOutputStream.write(bytes);

//                jarEntry.setExtra(bytes);
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            jarFile.close();
            IO.deepDelete(coped);
            jarOutputStream.flush();
            jarOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
