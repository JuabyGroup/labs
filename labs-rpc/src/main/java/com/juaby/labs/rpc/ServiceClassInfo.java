package com.juaby.labs.rpc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by Juaby on 2015/8/26.
 */
public class ServiceClassInfo {

    private String name;

    private String simpleName;

    private Field[] fields;

    private MethodInfo[] methods;

    public ServiceClassInfo() {
    }

    public ServiceClassInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public MethodInfo[] getMethods() {
        return methods;
    }

    public void setMethods(MethodInfo[] methods) {
        this.methods = methods;
    }

    public final class MethodInfo {

        private Method method;

        private Type[] paramTypes;

        private Type returnType;

        public MethodInfo() {
        }

        public MethodInfo(Method method) {
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Type[] getParamTypes() {
            return paramTypes;
        }

        public void setParamTypes(Type[] paramTypes) {
            this.paramTypes = paramTypes;
        }

        public Type getReturnType() {
            return returnType;
        }

        public void setReturnType(Type returnType) {
            this.returnType = returnType;
        }

    }

}
