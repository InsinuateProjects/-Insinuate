package io.insinuate.utils.loader;

import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * @copyFrom io.izzel.taboolib.loader.util.ILoader
 * @author sky
 * @adapter Score2
 * @since 2020-04-12 22:39
 */
public class CoreLoader extends URLClassLoader {

    private static MethodHandles.Lookup lookup;
    private static Unsafe unsafe;
    private static Method addUrlMethod;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object lookupBase = unsafe.staticFieldBase(lookupField);
            long lookupOffset = unsafe.staticFieldOffset(lookupField);
            lookup = (MethodHandles.Lookup) unsafe.getObject(lookupBase, lookupOffset);
        } catch (Throwable ignore) {
        }
    }

    private CoreLoader(URL[] urls) {
        super(urls);
    }

    private static void detourAddUrlMethod() {
        try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
        } catch (Throwable ignore) {
        }
    }

    /**
     * 将文件读取至内存中
     * 读取后不会随着插件的卸载而卸载
     * 请在执行前判断是否已经被读取
     * 防止出现未知错误
     */
    public static boolean addPath(File file, ClassLoader loader) {
        try {
            if (isForgeBase(loader)) {
                if (addUrlMethod == null) {
                    detourAddUrlMethod();
                }
                addUrlMethod.invoke(loader, file.toURI().toURL());
            } else if (loader.getClass().getSimpleName().equals("LaunchClassLoader")) {
                MethodHandle methodHandle = lookup.findVirtual(loader.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
                methodHandle.invoke(loader, file.toURI().toURL());
            } else {
                Field ucpField;
                try {
                    ucpField = loader.getClass().getDeclaredField("ucp");
                } catch (NoSuchFieldError | NoSuchFieldException e) {
                    ucpField = loader.getClass().getSuperclass().getDeclaredField("ucp");
                }
                long ucpOffset = unsafe.objectFieldOffset(ucpField);
                Object ucp = unsafe.getObject(loader, ucpOffset);
                MethodHandle methodHandle = lookup.findVirtual(ucp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
                methodHandle.invoke(ucp, file.toURI().toURL());
            }
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public static boolean isForgeBase(ClassLoader loader) {
        try {
            for (String clazz : Arrays.asList("net.minecraftforge.classloading.FMLForgePlugin", "net.minecraftforge.common.MinecraftForge")) {
                Class.forName(clazz, false, loader);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
