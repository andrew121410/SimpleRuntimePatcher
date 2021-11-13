package com.andrew121410.simpleruntimepatcher;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Map;
import java.util.function.Function;

public class Agent {

    private static Agent instance;

    private Instrumentation instrumentation;
    private Map<Class<?>, Function<ClassLoader, byte[]>> map;

    private Agent(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public static void agentmain(String agentArgument, Instrumentation instrumentation) {
        instance = new Agent(instrumentation);
    }

    public void process(Map<Class<?>, Function<ClassLoader, byte[]>> map) {
        this.map = map;

        this.instrumentation.addTransformer(new Transformer(map), true);
        try {
            this.instrumentation.retransformClasses(this.map.keySet().toArray(new Class[0]));
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }

    public static Agent getInstance() {
        return instance;
    }
}
