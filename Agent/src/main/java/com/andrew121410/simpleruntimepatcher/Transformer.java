package com.andrew121410.simpleruntimepatcher;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.function.BiFunction;

public class Transformer implements ClassFileTransformer {

    private Map<Class<?>, BiFunction<ClassLoader, byte[], byte[]>> map;

    public Transformer(Map<Class<?>, BiFunction<ClassLoader, byte[], byte[]>> map) {
        this.map = map;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;

        if (this.map.containsKey(classBeingRedefined)) {
            try {
                byteCode = this.map.get(classBeingRedefined).apply(loader, classfileBuffer);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return byteCode;
    }
}
