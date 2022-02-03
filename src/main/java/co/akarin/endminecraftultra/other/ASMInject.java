package co.akarin.endminecraftultra.other;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class ASMInject {
    public static boolean inject() {
        try {
            ClassPool classPool=ClassPool.getDefault();
            CtClass ctClass=classPool.getOrNull("org.spacehq.mc.protocol.data.MagicValues");
            if(ctClass==null) ctClass=classPool.get("org.spacehq.mc.protocol.data.game.MagicValues");
            CtMethod method1=ctClass.getDeclaredMethod("key");
            method1.addCatch("{return null;}", classPool.get("java.lang.Exception"));
            CtMethod method2=ctClass.getDeclaredMethod("value");
            method2.addCatch("{return null;}", classPool.get("java.lang.Exception"));
            ctClass.toClass();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
