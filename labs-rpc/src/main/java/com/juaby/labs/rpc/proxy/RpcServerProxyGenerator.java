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
                mv = cw.visitMethod(ACC_PUBLIC, "handler", "([Ljava/lang/Object;)Lcom/juaby/labs/rpc/message/ResponseMessageBody;", null, null);
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

                    String paramType = methodInfo.getParamsTypes()[i];
                    String basicType = ProxyHelper.javaBasic2LTypes(paramType);
                    String finalType;
                    if (basicType != null) {
                        finalType = basicType.substring(paramType.length(), basicType.length() - 1);
                    } else {
                        finalType = paramType.substring(1);
                    }

                    mv.visitInsn(AALOAD);

                    if (basicType != null) {
                        int storeType = storeType(paramType);
                        if (storeType == DSTORE || storeType == LSTORE) {
                            currStackSize = currStackSize - 2 + 2;
                            if (maxStackSize < currStackSize) {
                                maxStackSize = currStackSize;
                            }
                        } else {
                            currStackSize = currStackSize - 2 + 1;
                            if (maxStackSize < currStackSize) {
                                maxStackSize = currStackSize;
                            }
                        }
                    } else {
                        currStackSize = currStackSize - 2 + 1;
                        if (maxStackSize < currStackSize) {
                            maxStackSize = currStackSize;
                        }
                    }

                    mv.visitTypeInsn(CHECKCAST, finalType);

                    if (basicType != null) {
                        String valueMethod = valueMethod(paramType);
                        int storeType = storeType(paramType);
                        mv.visitMethodInsn(INVOKEVIRTUAL, finalType, valueMethod, "()" + paramType, false);

                        if (storeType == DSTORE || storeType == LSTORE) {
                            currStackSize = currStackSize - 0 - 2;
                            currStackSize = currStackSize + 2;
                        } else {
                            currStackSize = currStackSize - 0 - 1;
                            currStackSize = currStackSize + 1;
                        }

                        if (maxStackSize < currStackSize) {
                            maxStackSize = currStackSize;
                        }
                        mv.visitVarInsn(storeType, localStackPos);

                        if (storeType == DSTORE || storeType == LSTORE) {
                            currStackSize = currStackSize - 2;
                            localStackPos = localStackPos + 2;
                        } else {
                            currStackSize = currStackSize - 1;
                            localStackPos = localStackPos + 1;
                        }
                    } else {
                        mv.visitVarInsn(ASTORE, localStackPos);
                        currStackSize = currStackSize - 1;
                        localStackPos = localStackPos + 1;
                    }
                }

                mv.visitVarInsn(ALOAD, 0);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitFieldInsn(GETFIELD, clientProxyClassName, "service", "L" + classInfo.getName() + ";");
                currStackSize = currStackSize - 1 + 1;

                int DF = 2;
                int DFC = 0;
                for (int i = 0; i < methodInfo.getParamsLength(); i++) {
                    String paramType = methodInfo.getParamsTypes()[i];
                    String basicType = ProxyHelper.javaBasic2LTypes(paramType);

                    if (basicType != null) {
                        int loadType = loadType(paramType);
                        mv.visitVarInsn(loadType, DF + i);
                        if (loadType == DLOAD || loadType == LLOAD) {
                            DF = DF + 1;
                            DFC = DFC + 1;
                            currStackSize = currStackSize + 2;
                        } else {
                            currStackSize = currStackSize + 1;
                        }
                    } else {
                        mv.visitVarInsn(ALOAD, DF + i);
                        currStackSize = currStackSize + 1;
                    }

                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                }

                mv.visitMethodInsn(INVOKEINTERFACE, classInfo.getName(), methodInfo.getName(), methodInfo.getDesc(), true);
                currStackSize = currStackSize - methodInfo.getParamsLength() - DFC - 1;

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
                mv = cw.visitMethod(ACC_PUBLIC, "handler", "([Ljava/lang/Object;)Lcom/juaby/labs/rpc/message/ResponseMessageBody;", null, null);
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

                    String paramType = methodInfo.getParamsTypes()[i];
                    String basicType = ProxyHelper.javaBasic2LTypes(paramType);
                    String finalType;
                    if (basicType != null) {
                        finalType = basicType.substring(paramType.length(), basicType.length() - 1);
                    } else {
                        finalType = paramType.substring(1);
                    }

                    mv.visitInsn(AALOAD);

                    if (basicType != null) {
                        int storeType = storeType(paramType);
                        if (storeType == DSTORE || storeType == LSTORE) {
                            currStackSize = currStackSize - 2 + 2;
                            if (maxStackSize < currStackSize) {
                                maxStackSize = currStackSize;
                            }
                        } else {
                            currStackSize = currStackSize - 2 + 1;
                            if (maxStackSize < currStackSize) {
                                maxStackSize = currStackSize;
                            }
                        }
                    } else {
                        currStackSize = currStackSize - 2 + 1;
                        if (maxStackSize < currStackSize) {
                            maxStackSize = currStackSize;
                        }
                    }

                    mv.visitTypeInsn(CHECKCAST, finalType);

                    if (basicType != null) {
                        String valueMethod = valueMethod(paramType);
                        int storeType = storeType(paramType);
                        mv.visitMethodInsn(INVOKEVIRTUAL, finalType, valueMethod, "()" + paramType, false);

                        if (storeType == DSTORE || storeType == LSTORE) {
                            currStackSize = currStackSize - 0 - 2;
                            currStackSize = currStackSize + 2;
                        } else {
                            currStackSize = currStackSize - 0 - 1;
                            currStackSize = currStackSize + 1;
                        }

                        if (maxStackSize < currStackSize) {
                            maxStackSize = currStackSize;
                        }
                        mv.visitVarInsn(storeType, localStackPos);

                        if (storeType == DSTORE || storeType == LSTORE) {
                            currStackSize = currStackSize - 2;
                            localStackPos = localStackPos + 2;
                        } else {
                            currStackSize = currStackSize - 1;
                            localStackPos = localStackPos + 1;
                        }
                    } else {
                        mv.visitVarInsn(ASTORE, localStackPos);
                        currStackSize = currStackSize - 1;
                        localStackPos = localStackPos + 1;
                    }
                }

                mv.visitVarInsn(ALOAD, 0);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitFieldInsn(GETFIELD, clientProxyClassName, "service", "L" + classInfo.getName() + ";");
                currStackSize = currStackSize - 1 + 1;

                int DF = 2;
                int DFC = 0;
                for (int i = 0; i < methodInfo.getParamsLength(); i++) {
                    String paramType = methodInfo.getParamsTypes()[i];
                    String basicType = ProxyHelper.javaBasic2LTypes(paramType);

                    if (basicType != null) {
                        int loadType = loadType(paramType);
                        mv.visitVarInsn(loadType, DF + i);
                        if (loadType == DLOAD || loadType == LLOAD) {
                            DF = DF + 1;
                            DFC = DFC + 1;
                            currStackSize = currStackSize + 2;
                        } else {
                            currStackSize = currStackSize + 1;
                        }
                    } else {
                        mv.visitVarInsn(ALOAD, DF + i);
                        currStackSize = currStackSize + 1;
                    }

                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                }

                mv.visitMethodInsn(INVOKEINTERFACE, classInfo.getName(), methodInfo.getName(), methodInfo.getDesc(), true);
                currStackSize = currStackSize - methodInfo.getParamsLength() - DFC - 1;

                String returnType = methodInfo.getReturnTypeDesc();
                String basicType = ProxyHelper.javaBasic2LTypes(returnType);
                String finalType;
                if (basicType != null) {
                    finalType = basicType.substring(returnType.length(), basicType.length() - 1);
                } else {
                    finalType = returnType.substring(1, returnType.length() - 1);
                }

                if (basicType != null) {
                    int storeType = storeType(returnType);
                    if (storeType == DSTORE || storeType == LSTORE) {
                        currStackSize = currStackSize + 2;
                        if (maxStackSize < currStackSize) {
                            maxStackSize = currStackSize;
                        }
                    } else {
                        currStackSize = currStackSize + 1;
                        if (maxStackSize < currStackSize) {
                            maxStackSize = currStackSize;
                        }
                    }
                } else {
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                }

                if (basicType != null) {
                    int storeType = storeType(returnType);
                    mv.visitVarInsn(storeType, localStackPos);
                    if (storeType == DSTORE || storeType == LSTORE) {
                        localStackPos = localStackPos + 2;
                        currStackSize = currStackSize - 2;
                    } else {
                        localStackPos = localStackPos + 1;
                        currStackSize = currStackSize - 1;
                    }
                } else {
                    mv.visitVarInsn(ASTORE, localStackPos);
                    localStackPos = localStackPos + 1;
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

                if (basicType != null) {
                    int loadType = loadType(returnType);
                    if (loadType == DLOAD || loadType == LLOAD) {
                        mv.visitVarInsn(loadType, localStackPos - 2);
                        currStackSize = currStackSize + 2;
                    } else {
                        mv.visitVarInsn(loadType, localStackPos - 1);
                        currStackSize = currStackSize + 1;
                    }

                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                    mv.visitMethodInsn(INVOKESTATIC, finalType, "valueOf", "(" + returnType + ")L" + finalType + ";", false);
                    if (loadType == DLOAD || loadType == LLOAD) {
                        currStackSize = currStackSize - 2;
                        currStackSize = currStackSize + 2;
                    } else {
                        currStackSize = currStackSize - 1;
                        currStackSize = currStackSize + 1;
                    }

                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                } else {
                    mv.visitVarInsn(ALOAD, localStackPos - 1);
                    currStackSize = currStackSize + 1;
                    if (maxStackSize < currStackSize) {
                        maxStackSize = currStackSize;
                    }
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/juaby/labs/rpc/message/ResponseMessageBody", "setBody", "(Ljava/lang/Object;)V", false);
                currStackSize = currStackSize - 2;

                mv.visitVarInsn(ALOAD, localStackPos);
                currStackSize = currStackSize + 1;
                if (maxStackSize < currStackSize) {
                    maxStackSize = currStackSize;
                }

                mv.visitLdcInsn(finalType);
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

    public static int loadType(String type) {
        int loadType = ILOAD;
        switch (type) {
            case "Z":
                loadType = ILOAD;
                break;
            case "C":
                loadType = ILOAD;
                break;
            case "I":
                loadType = ILOAD;
                break;
            case "B":
                loadType = ILOAD;
                break;
            case "S":
                loadType = ILOAD;
                break;
            case "J":
                loadType = LLOAD;
                break;
            case "F":
                loadType = FLOAD;
                break;
            case "D":
                loadType = DLOAD;
                break;
            default:
                //TODO
        }
        return loadType;
    }

    public static int storeType(String type) {
        int storeType = ISTORE;
        switch (type) {
            case "Z":
                storeType = ISTORE;
                break;
            case "C":
                storeType = ISTORE;
                break;
            case "I":
                storeType = ISTORE;
                break;
            case "B":
                storeType = ISTORE;
                break;
            case "S":
                storeType = ISTORE;
                break;
            case "J":
                storeType = LSTORE;
                break;
            case "F":
                storeType = FSTORE;
                break;
            case "D":
                storeType = DSTORE;
                break;
            default:
                //TODO
        }
        return storeType;
    }

    public static String valueMethod(String type) {
        String valueMethod = "";
        switch (type) {
            case "Z":
                valueMethod = "booleanValue";
                break;
            case "C":
                valueMethod = "charValue";
                break;
            case "I":
                valueMethod = "intValue";
                break;
            case "B":
                valueMethod = "byteValue";
                break;
            case "S":
                valueMethod = "shortValue";
                break;
            case "J":
                valueMethod = "longValue";
                break;
            case "F":
                valueMethod = "floatValue";
                break;
            case "D":
                valueMethod = "doubleValue";
                break;
            default:
                //TODO
        }
        return valueMethod;
    }


}
