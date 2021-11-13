# SimpleRuntimePatcher

Not working at the moment :(

Example:
```java
        SimpleRuntimePatcher.patch(WorkAtComposter.class, (classPool, ctClass) -> {
            try {
                CtMethod ctMethod = ctClass.getDeclaredMethod("doWork");
                ctMethod.insertBefore("Bukkit.broadcastMessage(\"It works!!!\");");
                return ctClass.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        SimpleRuntimePatcher.create();
  ```
