package com.juaby.labs.rpc.proxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Juaby on 2015/8/25.
 */
public class DynamicServiceClientGenerator extends ClassLoader implements Opcodes {

    public DynamicServiceClientGenerator() {
        super(DynamicServiceClientGenerator.class.getClassLoader());
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

    public Class<?> generator(String handlerClassFullName) throws Exception {

        if (handlerClassFullName == null || !handlerClassFullName.contains("/")
                || handlerClassFullName.lastIndexOf("Handler") == -1) {
            throw new Exception(
                    "HandlerClassFullName Specification Exception. f.e. com/mapbar/tas/service/handler/PoiByKeywordSearch[Handler]");
        }

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, handlerClassFullName, null,
                "com/mapbar/tas/handler/SearchHandler", null);

        {
            fv = cw.visitField(0, "logger", "Lorg/slf4j/Logger;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL,
                    "com/mapbar/tas/handler/SearchHandler", "<init>", "()V",
                    false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass",
                    "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName",
                    "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESTATIC, "org/slf4j/LoggerFactory",
                    "getLogger", "(Ljava/lang/String;)Lorg/slf4j/Logger;",
                    false);
            mv.visitFieldInsn(PUTFIELD, handlerClassFullName, "logger",
                    "Lorg/slf4j/Logger;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass",
                    "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName",
                    "()Ljava/lang/String;", false);
            mv.visitMethodInsn(
                    INVOKESTATIC,
                    "com/mapbar/tas/service/SearchServiceFactory",
                    "getSearchServiceInstance",
                    "(Ljava/lang/String;)Lcom/mapbar/tas/service/ISearchService;",
                    false);
            mv.visitMethodInsn(INVOKESPECIAL,
                    "com/mapbar/tas/handler/SearchHandler", "setIss",
                    "(Lcom/mapbar/tas/service/ISearchService;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(
                    ACC_PUBLIC,
                    "service",
                    "(Lorg/glassfish/grizzly/http/server/Request;Lorg/glassfish/grizzly/http/server/Response;)V",
                    null, new String[] { "java/lang/Exception" });
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(
                    INVOKESPECIAL,
                    "com/mapbar/tas/handler/SearchHandler",
                    "service",
                    "(Lorg/glassfish/grizzly/http/server/Request;Lorg/glassfish/grizzly/http/server/Response;)V",
                    false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        cw.visitSource(handlerClassFullName.substring(handlerClassFullName.lastIndexOf("/") + 1) + ".java", null);
        cw.visitEnd();
        byte[] code = cw.toByteArray();

        Class<?> handlerClass = defineClass(handlerClassFullName.replace("/", "."), code, 0, code.length);
        return handlerClass;
    }

    public static void main(String[] args) throws Exception {
        Rpcifier rpcifier = new Rpcifier();
        rpcifier.main(new String [] {"com.juaby.labs.rpc.proxy.RpcClientProxyExample"});
        //rpcifier.main(new String [] {"com.juaby.labs.rpc.MessageService"});
    }

}
