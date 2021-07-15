package io.insinuate.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;

/**
 * @copyFrom io.izzel.taboolib.loader.util.IO
 * @author sky
 * @adapter Score2
 * @since 2020-04-12 22:36
 */
public class IO {

    public static String readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            stream.write(buf, 0, len);
        }
        return stream.toString("UTF-8");
    }

    public static String readFromURL(String in, String def) {
        try (InputStream inputStream = new URL(in).openStream(); BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            return readFully(bufferedInputStream);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return def;
    }

    public static boolean downloadFile(String in, File file) {
        try {
            URL url = new URL(in);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] bytes = readInputStream(inputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            inputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public static File toFile(InputStream inputStream, File file) {
        try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (Exception ignored) {
        }
        return file;
    }

    public static void copy(File file1, File file2) {
        try (FileInputStream fileIn = new FileInputStream(file1); FileOutputStream fileOut = new FileOutputStream(file2); FileChannel channelIn = fileIn.getChannel(); FileChannel channelOut = fileOut.getChannel()) {
            channelIn.transferTo(0, channelIn.size(), channelOut);
        } catch (IOException t) {
            t.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File file(File file) {
        if (!file.exists()) {
            folder(file);
            try {
                file.createNewFile();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return file;
    }

    @SuppressWarnings({"UnusedReturnValue", "ResultOfMethodCallIgnored"})
    public static File folder(File file) {
        if (!file.exists()) {
            String filePath = file.getPath();
            int index = filePath.lastIndexOf(File.separator);
            String folderPath;
            File folder;
            if ((index >= 0) && (!(folder = new File(filePath.substring(0, index))).exists())) {
                folder.mkdirs();
            }
        }
        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deepDelete(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        for (File file1 : Objects.requireNonNull(file.listFiles())) {
            deepDelete(file1);
        }
        file.delete();
    }

    public static String replaceWithOrder(String template, Object... args) {
        if (args.length == 0 || template.length() == 0) {
            return template;
        }
        char[] arr = template.toCharArray();
        StringBuilder stringBuilder = new StringBuilder(template.length());
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '{' && Character.isDigit(arr[Math.min(i + 1, arr.length - 1)])
                    && arr[Math.min(i + 1, arr.length - 1)] - '0' < args.length
                    && arr[Math.min(i + 2, arr.length - 1)] == '}') {
                stringBuilder.append(args[arr[i + 1] - '0']);
                i += 2;
            } else {
                stringBuilder.append(arr[i]);
            }
        }
        return stringBuilder.toString();
    }

    public static List<Class<?>> getClasses(Class<?> plugin) {
        return getClasses(plugin, new String[0]);
    }

    public static List<Class<?>> getClasses(Class<?> plugin, String[] ignore) {
        List<Class<?>> classes = new CopyOnWriteArrayList<>();
        URL url = plugin.getProtectionDomain().getCodeSource().getLocation();
        try {
            File src;
            try {
                src = new File(url.toURI());
            } catch (URISyntaxException e) {
                src = new File(url.getPath());
            }
            new JarFile(src).stream().filter(entry -> entry.getName().endsWith(".class")).forEach(entry -> {
                String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                try {
                    if (Arrays.stream(ignore).noneMatch(className::startsWith)) {
                        classes.add(Class.forName(className, false, plugin.getClassLoader()));
                    }
                } catch (Throwable ignored) {
                }
            });
        } catch (Throwable ignored) {
        }
        return classes;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
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
