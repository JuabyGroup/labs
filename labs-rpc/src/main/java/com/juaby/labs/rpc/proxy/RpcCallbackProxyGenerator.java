package com.juaby.labs.rpc.proxy;

import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Juaby on 2015/8/25.
 */
public class RpcCallbackProxyGenerator extends ClassLoader implements Opcodes {

    public RpcCallbackProxyGenerator() {
        super(RpcCallbackProxyGenerator.class.getClassLoader());
    }

    public <S> S newInstance(Class[] constructorParamTypes, Object[] constructorParamValues) throws IllegalAccessException,
            InstantiationException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class<?> c = Class.forName("com.juaby.labs.rpc.proxy.RpcClientProxy");
        /*以下调用带参的、私有构造函数*/
        Constructor<?> c1 = c.getDeclaredConstructor(constructorParamTypes);
        c1.setAccessible(true);
        return (S)c1.newInstance(constructorParamValues);
    }

    public <S> S newInstance(ServiceClassInfo classInfo, String transportKey) throws IllegalAccessException,
            InstantiationException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class<?> c = getProxyClass(classInfo, transportKey);
        /*以下调用带参的、私有构造函数*/
        Constructor<?> c1 = c.getDeclaredConstructor(String.class);
        c1.setAccessible(true);
        return (S)c1.newInstance(transportKey);
    }

    public Class<?> getProxyClass(ServiceClassInfo classInfo, String transportKey) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;
        String[] transportKeyArray = transportKey.split(":");
        int index = classInfo.getMethods().get(transportKeyArray[3]).getIndex();
        String proxyClassName = "com/juaby/labs/rpc/proxy/" + classInfo.getSimpleName() + "$RpcCallbackProxy$" + index;
        cw.visit(52, ACC_PUBLIC + ACC_SUPER, proxyClassName, "<REQ:Ljava/lang/Object;RES:Ljava/lang/Object;>Lcom/juaby/labs/rpc/proxy/RpcCallbackProxy;Lcom/juaby/labs/rpc/util/RpcCallback<TREQ;TRES;>;", "com/juaby/labs/rpc/proxy/RpcCallbackProxy", new String[] { "com/juaby/labs/rpc/util/RpcCallback" });

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/juaby/labs/rpc/proxy/RpcCallbackProxy", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyClassName, "setTransportKey", "(Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "callback", "(Ljava/lang/Object;)Ljava/lang/Object;", "(TREQ;)TRES;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, proxyClassName, "transport", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        byte[] code = cw.toByteArray();
        Class<?> handlerClass = defineClass(proxyClassName.replace("/", "."), code, 0, code.length);
        return handlerClass;
    }

}
