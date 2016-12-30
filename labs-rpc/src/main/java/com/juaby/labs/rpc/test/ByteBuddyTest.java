package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.proxy.RpcClientProxy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.*;
import net.bytebuddy.implementation.bind.annotation.Pipe;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Created by juaby on 16-5-19.
 */
public class ByteBuddyTest {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        TypeDescription.ForLoadedType t = new TypeDescription.ForLoadedType(MessageService.class);
        System.out.println(t.getPackage().getName());
        System.out.println(t.getName());
        System.out.println(t.getGenericSignature());
        System.out.println(t.getSimpleName());
        for (FieldDescription.InDefinedShape f : t.getDeclaredFields()) {
            System.out.println(f.getName());
            System.out.println(f.getDescriptor());
            System.out.println(f.getGenericSignature());
        }
        for (MethodDescription.InDefinedShape m : t.getDeclaredMethods()) {
            System.out.println(m.getGenericSignature());
            //System.out.println(m.getName());
        }
        System.out.println("----------------------------------------------------");
        for (TypeDescription.Generic g : t.getInterfaces()) {
            System.out.println(g.getTypeName());
            for (MethodDescription.InGenericShape m : g.getDeclaredMethods()) {
                System.out.println(m.getGenericSignature());
                //System.out.println(m.getName());
            }
        }
        System.out.println("----------------------------------------------------");

        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(ByteBuddyTest.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        System.out.println(dynamicType.newInstance().toString());

        Class<? extends java.util.function.Function> dynamicType2 = new ByteBuddy()
                .subclass(java.util.function.Function.class)
                .method(ElementMatchers.named("apply"))
                .intercept(MethodDelegation.to(new GreetingInterceptor()))
                .make()
                .load(ByteBuddyTest.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        System.out.println((String) dynamicType2.newInstance().apply("Byte Buddy"));

        System.out.println(new ByteBuddy()
                .subclass(SumExample.class)
                .method(named("calculate")).intercept(SumImplementation.INSTANCE)
                .make()
                .load(ByteBuddyTest.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance()
                .calculate());

        Class<?> c = new ByteBuddy()
                /**
                .with(new NamingStrategy.AbstractBase() {
                    @Override
                    protected String name(TypeDescription superClass) {
                        return "com.juaby.labs.rpc.proxy." + superClass. + "$ClientProxy";
                    }
                })
                 */
                .subclass(RpcClientProxy.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                //.name("com.juaby.labs.rpc.proxy.MessageService$Proxy")
                .implement(MessageService.class)
                //.method(isDeclaredBy(MessageService.class))
                .intercept(MethodDelegation.to(Agent.class))
                .defineConstructor(Visibility.PRIVATE).withParameter(String.class)
                .intercept(SuperMethodCall.INSTANCE)
                .make()
                .load(ByteBuddyTest.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Constructor<?> c1 = c.getDeclaredConstructor(String.class);
        c1.setAccessible(true);
        MessageService<?, ?> messageService = (MessageService<?, ?>)c1.newInstance(MessageService.class.getName());
        TestBean testBean = new TestBean();
        testBean.setId("007");
        List<String> params = new ArrayList<String>();
        params.add("hello");
        TestResult result = messageService.message(testBean, params);
        messageService.vmethod();
        System.out.println(result);
    }

    public enum SumMethod implements ByteCodeAppender {

        INSTANCE;

        @Override
        public Size apply(MethodVisitor methodVisitor,
                          Implementation.Context implementationContext,
                          MethodDescription instrumentedMethod) {
            if (!instrumentedMethod.getReturnType().asErasure().represents(int.class)) {
                throw new IllegalArgumentException(instrumentedMethod + " must return int");
            }
            StackManipulation.Size operandStackSize = new StackManipulation.Compound(
                    IntegerConstant.forValue(10),
                    IntegerConstant.forValue(50),
                    IntegerSum.INSTANCE,
                    MethodReturn.INTEGER
            ).apply(methodVisitor, implementationContext);
            return new Size(operandStackSize.getMaximalSize(), instrumentedMethod.getStackSize());
        }
    }

    public enum IntegerSum implements StackManipulation {

        INSTANCE;

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitInsn(Opcodes.IADD);
            return new Size(-1, 0);
        }
    }

    public static abstract class SumExample {

        public abstract int calculate();
    }

    public enum SumImplementation implements Implementation {
        INSTANCE;

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        @Override
        public ByteCodeAppender appender(Target implementationTarget) {
            return SumMethod.INSTANCE;
        }
    }

    public interface Forwarder<T, S> {
        T to(S target);
    }

    static class ForwardingInterceptor {

        private final RpcClientProxy rpcClientProxy; // constructor omitted

        public ForwardingInterceptor(RpcClientProxy rpcClientProxy) {
            this.rpcClientProxy = rpcClientProxy;
        }

        public <R> R interceptor(@Pipe Forwarder<R, RpcClientProxy> pipe) {
            System.out.println("Calling database");
            try {
                return pipe.to(rpcClientProxy);
            } finally {
                System.out.println("Returned from database");
            }
        }
    }

}
