package com.juaby.labs.rpc.proxy;

import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Juaby on 2015/8/25.
 */
public class RpcClientProxyGenerator extends ClassLoader implements Opcodes {

    public RpcClientProxyGenerator() {
        super(RpcClientProxyGenerator.class.getClassLoader());
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

    public <S> S newInstance(ServiceClassInfo classInfo, Class<S> service) throws IllegalAccessException,
            InstantiationException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class<?> c = getProxyClass(classInfo);
        /*以下调用带参的、私有构造函数*/
        Constructor<?> c1 = c.getDeclaredConstructor(String.class);
        c1.setAccessible(true);
        return (S)c1.newInstance(classInfo.getName());
    }

    public Class<?> getProxyClass(ServiceClassInfo classInfo) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        String clientProxyClassName = classInfo.getName() + "$ClientProxy";
        cw.visit(classInfo.getVersion(), ACC_PUBLIC + ACC_SUPER, clientProxyClassName, classInfo.getSignature(), "com/juaby/labs/rpc/proxy/RpcClientProxy", new String[] {classInfo.getName()});

        {
            mv = cw.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/juaby/labs/rpc/proxy/RpcClientProxy", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, clientProxyClassName, "setConfig", "(Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        for (String methodSignature : classInfo.getMethods().keySet()) {
            ServiceClassInfo.MethodInfo methodInfo = classInfo.getMethods().get(methodSignature);
            String returnTypeDesc = methodInfo.getReturnTypeDesc();
            boolean isReturnVoid = methodInfo.isReturnVoid();
            int paramsLength = methodInfo.getParamsLength();

            {
                mv = cw.visitMethod(ACC_PUBLIC, methodInfo.getName(), methodInfo.getDesc(), methodInfo.getSignature(), methodInfo.getExceptions());
                mv.visitCode();

                int maxStackSize = 0;
                int currStackSize = 0;
                int localStackPos = 2;

                mv.visitVarInsn(ALOAD, 0);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitVarInsn(ALOAD, 0);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitMethodInsn(INVOKEVIRTUAL, clientProxyClassName, "getConfig", "()Lcom/juaby/labs/rpc/config/ServiceConfig;", false);
                currStackSize = currStackSize - 1 + 1;

                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/config/ServiceConfig", "getName", "()Ljava/lang/String;", false);
                currStackSize = currStackSize - 1 + 1;

                mv.visitLdcInsn(methodInfo.getSignature());
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                //mv.visitInsn(ICONST_0 + paramsLength);
                mv.visitIntInsn(SIPUSH, paramsLength);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                currStackSize = currStackSize - 1 + 1;

                for (int p = 0; p < paramsLength; p++) {
                    mv.visitInsn(DUP);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }

                    //mv.visitInsn(ICONST_0 + p);
                    mv.visitIntInsn(SIPUSH, p);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }

                    mv.visitVarInsn(ALOAD, p + 1);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }

                    mv.visitInsn(AASTORE);
                    currStackSize = currStackSize - 3;
                }

                mv.visitMethodInsn(INVOKEVIRTUAL, clientProxyClassName, "sendMessage", "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", false);
                currStackSize = currStackSize - 3 - 1 + 1;

                if (!isReturnVoid) {
                    mv.visitTypeInsn(CHECKCAST, returnTypeDesc);
                    mv.visitInsn(ARETURN);
                } else {
                    mv.visitInsn(POP);
                    currStackSize = currStackSize - 1;
                    mv.visitInsn(RETURN);
                }

                mv.visitMaxs(maxStackSize, paramsLength + 1); //4 ==> paramsLength == 0 else 7

                mv.visitEnd();
            }

        }

        cw.visitEnd();

        byte[] code =  cw.toByteArray();
        Class<?> handlerClass = defineClass(clientProxyClassName.replace("/", "."), code, 0, code.length);
        return handlerClass;
    }

}
