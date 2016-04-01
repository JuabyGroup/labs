package com.juaby.labs.rpc.proxy;

import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Juaby on 2015/8/25.
 */
public class RpcServerProxyGenerator extends ClassLoader implements Opcodes {

    public RpcServerProxyGenerator() {
        super(RpcServerProxyGenerator.class.getClassLoader());
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

    public <S> S newInstance(ServiceClassInfo classInfo, ServiceClassInfo.MethodInfo methodInfo) throws IllegalAccessException,
            InstantiationException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class<?> c = getProxyClass(classInfo, methodInfo);
        /*以下调用带参的、私有构造函数*/
        Constructor<?> constructor = c.getDeclaredConstructor(Class.forName(classInfo.getId()));
        constructor.setAccessible(true);
        return (S)constructor.newInstance(ProxyHelper.getServiceInstance(classInfo.getName()));
    }

    public Class<?> getProxyClass(ServiceClassInfo classInfo, ServiceClassInfo.MethodInfo methodInfo) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        String methodName = methodInfo.getName().substring(0, 1).toUpperCase() + methodInfo.getName().substring(1);
        String clientProxyClassName = classInfo.getName() + "$ServerProxy$" + methodName;

        cw.visit(classInfo.getVersion(), ACC_PUBLIC + ACC_SUPER, clientProxyClassName, null, "java/lang/Object", new String[] { "com/juaby/labs/rpc/server/RpcServiceHandler" });

        {
            fv = cw.visitField(ACC_PRIVATE, "service", "L" + classInfo.getName() + ";", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, "<init>", "(L" + classInfo.getName() + ";)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, clientProxyClassName, "service", "L" + classInfo.getName() + ";");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        if (methodInfo.isReturnVoid()) {
            {
                mv = cw.visitMethod(ACC_PUBLIC, "handler", "([Ljava/lang/Object;)Lcom/juaby/labs/rpc/message/ResponseMessageBody;", "([Ljava/lang/Object;)Lcom/juaby/labs/rpc/message/ResponseMessageBody<Ljava/lang/Object;>;", null);
                mv.visitCode();

                int maxStackSize = 0;
                int currStackSize = 0;
                int localStackPos = 2;
                for (int i = 0; i < methodInfo.getParamsLength(); i++) {
                    mv.visitVarInsn(ALOAD, 1);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                    //mv.visitInsn(ICONST_0 + i);
                    mv.visitIntInsn(SIPUSH, i);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                    mv.visitInsn(AALOAD);
                    currStackSize = currStackSize - 2 + 1;

                    mv.visitTypeInsn(CHECKCAST, methodInfo.getParamsTypes()[i].substring(1));
                    mv.visitVarInsn(ASTORE, localStackPos);
                    currStackSize = currStackSize - 1;
                    localStackPos = localStackPos + 1;
                }

                mv.visitVarInsn(ALOAD, 0);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitFieldInsn(GETFIELD, clientProxyClassName, "service", "L" + classInfo.getName() + ";");
                currStackSize = currStackSize - 1 + 1;

                for (int i = 0; i < methodInfo.getParamsLength(); i++) {
                    mv.visitVarInsn(ALOAD, 2 + i);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                }

                mv.visitMethodInsn(INVOKEINTERFACE, classInfo.getName(), methodInfo.getName(), methodInfo.getDesc(), true);
                currStackSize = currStackSize - methodInfo.getParamsLength() - 1;

                if (currStackSize != 0) {
                    mv.visitInsn(POP);
                    currStackSize = currStackSize - 1;
                }

                mv.visitTypeInsn(NEW, "com/juaby/labs/rpc/message/ResponseMessageBody");
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitInsn(DUP);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitMethodInsn(INVOKESPECIAL, "com/juaby/labs/rpc/message/ResponseMessageBody", "<init>", "()V", false);
                currStackSize = currStackSize - 1;

                mv.visitVarInsn(ASTORE, localStackPos);
                currStackSize = currStackSize - 1;

                mv.visitVarInsn(ALOAD, localStackPos);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitInsn(ACONST_NULL);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/message/ResponseMessageBody", "setBody", "(Ljava/lang/Object;)V", false);
                currStackSize = currStackSize - 2;
                mv.visitVarInsn(ALOAD, localStackPos);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }
                mv.visitLdcInsn(methodInfo.getReturnTypeDesc());
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/message/ResponseMessageBody", "setReturnClass", "(Ljava/lang/String;)V", false);
                currStackSize = currStackSize - 2;
                mv.visitVarInsn(ALOAD, localStackPos);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }
                mv.visitInsn(ARETURN);
                mv.visitMaxs(maxStackSize, localStackPos + 1);
                mv.visitEnd();
            }
        } else {
            {
                mv = cw.visitMethod(ACC_PUBLIC, "handler", "([Ljava/lang/Object;)Lcom/juaby/labs/rpc/message/ResponseMessageBody;", "([Ljava/lang/Object;)Lcom/juaby/labs/rpc/message/ResponseMessageBody<" + methodInfo.getReturnTypeDesc() + ">;", null);
                mv.visitCode();
                int maxStackSize = 0;
                int currStackSize = 0;
                int localStackPos = 2;
                for (int i = 0; i < methodInfo.getParamsLength(); i++) {
                    mv.visitVarInsn(ALOAD, 1);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }

                    //mv.visitInsn(ICONST_0 + i);
                    mv.visitIntInsn(SIPUSH, i);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }

                    mv.visitInsn(AALOAD);
                    currStackSize = currStackSize - 2 + 1;

                    mv.visitTypeInsn(CHECKCAST, methodInfo.getParamsTypes()[i].substring(1));
                    mv.visitVarInsn(ASTORE, localStackPos);
                    currStackSize = currStackSize - 1;
                    localStackPos = localStackPos + 1;
                }

                mv.visitVarInsn(ALOAD, 0);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitFieldInsn(GETFIELD, clientProxyClassName, "service", "L" + classInfo.getName() + ";");
                currStackSize = currStackSize - 1 + 1;

                for (int i = 0; i < methodInfo.getParamsLength(); i++) {
                    mv.visitVarInsn(ALOAD, 2 + i);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                }

                mv.visitMethodInsn(INVOKEINTERFACE, classInfo.getName(), methodInfo.getName(), methodInfo.getDesc(), true);
                currStackSize = currStackSize - methodInfo.getParamsLength() - 1;
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitVarInsn(ASTORE, localStackPos);
                currStackSize = currStackSize - 1;
                localStackPos = localStackPos + 1;

                mv.visitTypeInsn(NEW, "com/juaby/labs/rpc/message/ResponseMessageBody");
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitInsn(DUP);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitMethodInsn(INVOKESPECIAL, "com/juaby/labs/rpc/message/ResponseMessageBody", "<init>", "()V", false);
                currStackSize = currStackSize - 1;

                mv.visitVarInsn(ASTORE, localStackPos);
                currStackSize = currStackSize - 1;

                mv.visitVarInsn(ALOAD, localStackPos);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitVarInsn(ALOAD, localStackPos - 1);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/message/ResponseMessageBody", "setBody", "(Ljava/lang/Object;)V", false);
                currStackSize = currStackSize - 2;

                mv.visitVarInsn(ALOAD, localStackPos);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitLdcInsn(methodInfo.getReturnTypeDesc().substring(1, methodInfo.getReturnTypeDesc().length() - 1));
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/message/ResponseMessageBody", "setReturnClass", "(Ljava/lang/String;)V", false);
                currStackSize = currStackSize - 2;

                mv.visitVarInsn(ALOAD, localStackPos);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitInsn(ARETURN);
                mv.visitMaxs(maxStackSize, localStackPos + 1);
                mv.visitEnd();
            }
        }

        cw.visitEnd();

        byte[] code =  cw.toByteArray();
        Class<?> handlerClass = defineClass(clientProxyClassName.replace("/", "."), code, 0, code.length);
        return handlerClass;
    }

}
