package cn.insinuate.loader.maven;

import cn.insinuate.loader.utils.Pair;
import org.objectweb.asm.*;

import java.io.*;
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

        this.repositories.addAll(Arrays.asList(repositories));
        this.repositories.add("https://maven.aliyun.com/nexus/content/groups/public/");
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

        this.repositories.addAll(Arrays.asList(repositories));
        this.repositories.add("https://maven.aliyun.com/nexus/content/groups/public/");
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

    public boolean download(String repository) {
        try {
            URL url = new URL(repository);

            if (file.exists()) {
                return false;
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] bytes = readInputStream(inputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            inputStream.close();
            return true;
        } catch (Throwable ignored) {

        }
        return false;
    }

    private boolean startsWith(String name) {
        for (Map.Entry<String, String> entry : relocates.entrySet()) {
            if (name.startsWith(entry.getKey())) return true;
        }
        return false;
    }

    public boolean relocate() {
        JarFile jarFile;
        FileOutputStream fileOutputStream;
        JarOutputStream jarOutputStream;
        try {
            jarFile = new JarFile(file);
            fileOutputStream = new FileOutputStream(jarFile.getName(), true);
            jarOutputStream = new JarOutputStream(fileOutputStream);
        } catch (IOException ignored) {
            return false;
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            try {

                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.getName().endsWith(".class"))
                    continue;

                ClassReader classReader = new ClassReader(jarFile.getInputStream(jarEntry));
                ClassWriter classWriter = new ClassWriter(classReader, 0);

                ClassVisitor classVisitor = new RelocateClassVisitor(classWriter, relocates);
                classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);

                byte[] bytes = classWriter.toByteArray();

                String entryName = jarEntry.getName().replace("/", ".");
                entryName = entryName.substring(0, entryName.lastIndexOf("."));

                if (startsWith(entryName)) {
                    String finallyName = jarEntry.getName();

                    for (Map.Entry<String, String> relocate : relocates.entrySet()) {
                        String string = finallyName.replaceFirst(relocate.getKey(), relocate.getValue());
                        if (!finallyName.equals(string)) {
                            finallyName = string;
                            break;
                        }
                    }

                    jarOutputStream.putNextEntry(new JarEntry(finallyName));
                } else {
                    jarOutputStream.putNextEntry(jarEntry);
                }
                jarOutputStream.write(bytes);

                jarEntry.setExtra(bytes);
            } catch (Throwable ignored) {
                return false;
            }
        }

        try {
            jarOutputStream.close();
        } catch (IOException ignored) {
            return false;
        }
        try {
            fileOutputStream.close();
        } catch (IOException ignored) {
            return false;
        }
        try {
            jarFile.close();
        } catch (IOException ignored) {
            return false;
        }

        return true;
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
