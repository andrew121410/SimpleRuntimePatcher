package com.andrew121410.simpleruntimepatcher;

import com.andrew121410.simpleruntimepatcher.attacher.DirectAttacher;
import com.andrew121410.simpleruntimepatcher.attacher.RemoteAttacher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SimpleRuntimePatcher {

    private static final String ATTACH_MOD_PATH = "jmods/jdk.attach.jmod";

    private static final Map<Class<?>, BiFunction<ClassLoader, byte[], byte[]>> TO_PATCH = new HashMap<>();

    public static void patch(Class<?> theClass, BiFunction<ClassLoader, byte[], byte[]> biFunction) {
        TO_PATCH.put(theClass, biFunction);
    }

    public static void create() {
        new SimpleRuntimePatcher().hook();
    }

    private void hook() {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        File javaHome = new File(System.getProperty("java.home"));
        if (systemClassLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

            try {
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);

                File toolsJar = new File(javaHome, "lib/tools.jar");
                if (!toolsJar.exists())
                    throw new RuntimeException("Not running with JDK!");

                method.invoke(urlClassLoader, toolsJar.toURI().toURL());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            Path attachMod = javaHome.toPath().resolve(ATTACH_MOD_PATH);
            if (Files.notExists(attachMod)) {
                throw new RuntimeException("Not running with JDK!");
            }
        }

        String agentFilePath = getAgentJar();
        if (canSelfAttach()) {
            try {
                new DirectAttacher().attachAgent(agentFilePath, getPid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                new RemoteAttacher().attachAgent(agentFilePath, getPid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Process agent
        Agent.getInstance().process(TO_PATCH);
        System.out.println("Loaded");
    }

    private static String getAgentJar() {
        try (InputStream is = SimpleRuntimePatcher.class.getResourceAsStream("/agent.jar")) {
            File agentFile = File.createTempFile("agent", ".jar");
            agentFile.deleteOnExit();

            Files.copy(is, agentFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return agentFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean canSelfAttach() {
        String version = System.getProperty("java.version");
        return version.startsWith("1.");
    }

    private static String getPid() {
        String vmName = ManagementFactory.getRuntimeMXBean().getName();
        return vmName.substring(0, vmName.indexOf('@'));
    }
}