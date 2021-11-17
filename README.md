# SimpleRuntimePatcher

### Simple Bytecode Manipulation

### Credit

Thanks to the creator's of [RuntimePatcher](https://github.com/CraftoryStudios/RuntimePatcher)
and [RuntimeTransformer](https://github.com/Yamakaja/RuntimeTransformer)

###

**Warning!** We don't include Javassist

Example:

```java

        SimpleRuntimePatcher.patch(WorkAtComposter.class, (classLoader, originalBytecode) -> {
        ClassPool classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(classLoader));
        try {
        CtClass ctClass = classPool.get(WorkAtComposter.class.getName());
        CtMethod ctMethod = ctClass.getDeclaredMethod("doWork");
        ctMethod.insertBefore("System.out.println(\"Yes, it actually works!\");");
        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();
        return byteCode;
        } catch (Exception e) {
        e.printStackTrace();
        }
        return originalBytecode;
        });

        SimpleRuntimePatcher.create();
  ```