package com.andrew121410.simpleruntimepatcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.function.BiFunction;

public class Transformer implements ClassFileTransformer {

    private Map<Class<?>, BiFunction<ClassPool, CtClass, byte[]>> map;

    public Transformer(Map<Class<?>, BiFunction<ClassPool, CtClass, byte[]>> map) {
        this.map = map;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;

//        if (classBeingRedefined.getName().equalsIgnoreCase("net.minecraft.world.entity.ai.behavior.BehaviorWorkComposter")) {
//            System.out.println("IT WAS RAN???!");
//            try {
//                ClassPool classPool = new ClassPool();
//                classPool.appendClassPath(new LoaderClassPath(loader));
//
//                CtClass ctClass = classPool.get("net.minecraft.world.entity.ai.behavior.BehaviorWorkComposter");
//
//                CtMethod ctMethod = ctClass.getDeclaredMethod("doWork");
//                ctMethod.insertBefore("System.out.println(\"HOLLY FUCK IT WORKS!!!!!!!!!!!!\");");
//                byteCode = ctClass.toBytecode();
//                ctClass.detach();
//            } catch (NotFoundException | CannotCompileException | IOException e) {
//                e.printStackTrace();
//            }
//        }

        if (this.map.containsKey(classBeingRedefined)) {
            try {
                ClassPool classPool = new ClassPool();
                classPool.appendClassPath(new LoaderClassPath(loader));
                CtClass ctClass = classPool.get(classBeingRedefined.getName());
                byteCode = this.map.get(classBeingRedefined).apply(classPool, ctClass);
                ctClass.detach();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return byteCode;
    }
}
