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

        private ParamInfo[] paramTypes;

        private ReturnInfo returnInfo;

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

        public ParamInfo[] getParamTypes() {
            return paramTypes;
        }

        public void setParamTypes(ParamInfo[] paramTypes) {
            this.paramTypes = paramTypes;
        }

        public ReturnInfo getReturnInfo() {
            return returnInfo;
        }

        public void setReturnInfo(ReturnInfo returnInfo) {
            this.returnInfo = returnInfo;
        }

    }

    public final class ParamInfo {

        private Type paramType;

        private Type[] parameterizedTypes;

        public ParamInfo() {
        }

        public Type getParamType() {
            return paramType;
        }

        public void setParamType(Type paramType) {
            this.paramType = paramType;
        }

        public Type[] getParameterizedTypes() {
            return parameterizedTypes;
        }

        public void setParameterizedTypes(Type[] parameterizedTypes) {
            this.parameterizedTypes = parameterizedTypes;
        }

    }

    public final class ReturnInfo {

        private Type returnType;

        private Type[] parameterizedTypes;

        public ReturnInfo() {
        }

        public Type getReturnType() {
            return returnType;
        }

        public void setReturnType(Type returnType) {
            this.returnType = returnType;
        }

        public Type[] getParameterizedTypes() {
            return parameterizedTypes;
        }

        public void setParameterizedTypes(Type[] parameterizedTypes) {
            this.parameterizedTypes = parameterizedTypes;
        }

    }

}
