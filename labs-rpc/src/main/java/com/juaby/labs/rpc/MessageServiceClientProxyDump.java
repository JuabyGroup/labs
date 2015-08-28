package com.juaby.labs.rpc;

import java.util.*;
import org.objectweb.asm.*;
public class MessageServiceClientProxyDump implements Opcodes {

    public static byte[] dump (Class service) throws Exception {

        ServiceClassInfo serviceClassInfo = ServiceClassInfoHelper.parser(service);

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "com/juaby/labs/rpc/" + serviceClassInfo.getSimpleName() + "$ClientProxy", null, "java/lang/Object", new String[] { serviceClassInfo.getName().replaceAll(".", "/") });

        {
            fv = cw.visitField(ACC_PRIVATE, "config", "Lcom/juaby/labs/rpc/ServiceConfig;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/juaby/labs/rpc/ServiceConfig;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "com/juaby/labs/rpc/" + serviceClassInfo.getSimpleName() + "$ClientProxy", "config", "Lcom/juaby/labs/rpc/ServiceConfig;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        for (int i = 0; i < serviceClassInfo.getMethods().length; i++) {
            String methodName = serviceClassInfo.getMethods()[i].getMethod().getName().replaceAll(".", "/");
            ServiceClassInfo.ParamInfo[] paramTypes = serviceClassInfo.getMethods()[i].getParamTypes();
            ServiceClassInfo.ReturnInfo returnType = serviceClassInfo.getMethods()[i].getReturnInfo();
            StringBuilder methodInfoBuilder = new StringBuilder();
            methodInfoBuilder.append("(");
            for (ServiceClassInfo.ParamInfo type : paramTypes) {
                methodInfoBuilder.append(ProxyHelper.javaType(type.getParamType().getTypeName()));
                int index = type.getParamType().getTypeName().indexOf("<");
                if (index == -1) {
                    index = type.getParamType().getTypeName().length();
                }
                methodInfoBuilder.append(type.getParamType().getTypeName().substring(0, index).replaceAll(".", "/"));
                methodInfoBuilder.append(";");
            }
            methodInfoBuilder.append(")");
            methodInfoBuilder.append(ProxyHelper.javaType(serviceClassInfo.getMethods()[i].getReturnInfo().getReturnType().getTypeName()));

            int index = returnType.getReturnType().getTypeName().indexOf("<");
            if (index == -1) {
                index = returnType.getReturnType().getTypeName().length();
            }
            methodInfoBuilder.append(returnType.getReturnType().getTypeName().substring(0, index));
            if (!ProxyHelper.isJavaType(returnType.getReturnType().getTypeName())) {
                methodInfoBuilder.append(";");
            }

            StringBuilder methodInfoBuilder2 = new StringBuilder();
            methodInfoBuilder2.append("(");
            for (ServiceClassInfo.ParamInfo type : paramTypes) {
                methodInfoBuilder2.append(ProxyHelper.javaType(type.getParamType().getTypeName()));
                int index2 = type.getParamType().getTypeName().indexOf("<");
                if (index2 == -1) {
                    index2 = type.getParamType().getTypeName().length();
                }
                methodInfoBuilder2.append(type.getParamType().getTypeName().substring(0, index2).replaceAll(".", "/"));
                if (type.getParameterizedTypes() != null && type.getParameterizedTypes().length > 0) {
                    methodInfoBuilder2.append("<");

                    methodInfoBuilder2.append(">");
                }
                methodInfoBuilder2.append(";");
            }
            methodInfoBuilder2.append(")");
            methodInfoBuilder2.append(ProxyHelper.javaType(serviceClassInfo.getMethods()[i].getReturnInfo().getReturnType().getTypeName()));
            methodInfoBuilder2.append(returnType.getReturnType().getTypeName().replaceAll(".", "/"));
            methodInfoBuilder2.append(";");
            String methodInfo = "(Lcom/juaby/labs/rpc/TestBean;Ljava/util/List<Ljava/lang/String;>;)Lcom/juaby/labs/rpc/TestResult;";
            {
                mv = cw.visitMethod(ACC_PUBLIC, methodName, methodInfoBuilder.toString(), "(Lcom/juaby/labs/rpc/TestBean;Ljava/util/List<Ljava/lang/String;>;)Lcom/juaby/labs/rpc/TestResult;", null);
                mv.visitCode();
                Label l0 = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(l0, l1, l2, "java/lang/InterruptedException");
                Label l3 = new Label();
                mv.visitTryCatchBlock(l0, l1, l3, "java/util/concurrent/ExecutionException");
                Label l4 = new Label();
                mv.visitTryCatchBlock(l0, l1, l4, "java/util/concurrent/TimeoutException");
                mv.visitInsn(ACONST_NULL);
                mv.visitVarInsn(ASTORE, 3);
                mv.visitTypeInsn(NEW, "com/juaby/labs/rpc/RequestMessageBody");
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", false);
                mv.visitInsn(ICONST_0);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKESPECIAL, "com/juaby/labs/rpc/RequestMessageBody", "<init>", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(ASTORE, 4);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitLdcInsn(methodName);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/RequestMessageBody", "setMethod", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitInsn(ICONST_2);
                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitInsn(AASTORE);
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(AASTORE);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/RequestMessageBody", "setParams", "([Ljava/lang/Object;)V", false);
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitMethodInsn(INVOKESTATIC, "org/glassfish/grizzly/samples/filterchain/GIOPClient", "sendMessage", "(Lcom/juaby/labs/rpc/RequestMessageBody;)Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, returnType.getReturnType().getTypeName().replaceAll(".", "/"));
                mv.visitVarInsn(ASTORE, 3);
                mv.visitLabel(l1);
                Label l5 = new Label();
                mv.visitJumpInsn(GOTO, l5);
                mv.visitLabel(l2);
                int nLocal = 3 + paramTypes.length;
                Object[] local = new Object[nLocal];
                local[0] = "com/juaby/labs/rpc/" + serviceClassInfo.getSimpleName() + "$ClientProxy";
                for (int n = 1; n <= paramTypes.length; n++) {
                    local[n] = paramTypes[n - 1].getParamType().getTypeName().replaceAll(".", "/");
                }
                local[paramTypes.length + 1] = returnType;
                local[nLocal - 1] = "com/juaby/labs/rpc/RequestMessageBody";
                mv.visitFrame(Opcodes.F_FULL, nLocal, local, 1, new Object[]{"java/lang/InterruptedException"});
                mv.visitVarInsn(ASTORE, 5);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V", false);
                mv.visitJumpInsn(GOTO, l5);
                mv.visitLabel(l3);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/util/concurrent/ExecutionException"});
                mv.visitVarInsn(ASTORE, 5);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/ExecutionException", "printStackTrace", "()V", false);
                mv.visitJumpInsn(GOTO, l5);
                mv.visitLabel(l4);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/util/concurrent/TimeoutException"});
                mv.visitVarInsn(ASTORE, 5);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/TimeoutException", "printStackTrace", "()V", false);
                mv.visitLabel(l5);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(5, 6);
                mv.visitEnd();
            }
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}