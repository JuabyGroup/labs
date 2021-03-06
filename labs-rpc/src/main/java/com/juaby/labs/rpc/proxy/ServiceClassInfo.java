package com.juaby.labs.rpc.proxy;

import java.util.*;

/**
 * Created by Juaby on 2015/8/26.
 */
public class ServiceClassInfo {

    private String id;

    private int version;

    private String packageName;

    private String name;

    private String simpleName;

    private String superName;

    private String[] interfaces;

    private int access;

    private String signature;

    private String source;

    private String debug;

    private Set<FieldInfo> fields = new HashSet<FieldInfo>();

    private Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();

    public ServiceClassInfo() {
    }

    public ServiceClassInfo(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public String getSuperName() {
        return superName;
    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String[] interfaces) {
        this.interfaces = interfaces;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public Set<FieldInfo> getFields() {
        return fields;
    }

    public void setFields(Set<FieldInfo> fields) {
        this.fields = fields;
    }

    public Map<String, MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, MethodInfo> methods) {
        this.methods = methods;
    }

    public final static class FieldInfo {

        private int access;

        private String name;

        private String desc;

        private String signature;

        private Object value;

        public FieldInfo() {}

        public int getAccess() {
            return access;
        }

        public void setAccess(int access) {
            this.access = access;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

    }

    public final static class MethodInfo {

        private int id;

        private int access;

        private String name;

        private String desc;

        private String signature;

        private String[] exceptions;

        private int maxStack;

        private int maxLocals;

        private int paramsLength;

        private boolean isReturnVoid = false;

        private String returnTypeDesc;

        private String [] paramsTypes;

        private boolean isCallback = false;

        private int callbackIndex;

        private int index;

        public MethodInfo() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAccess() {
            return access;
        }

        public void setAccess(int access) {
            this.access = access;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String[] getExceptions() {
            return exceptions;
        }

        public void setExceptions(String[] exceptions) {
            this.exceptions = exceptions;
        }

        public int getMaxStack() {
            return maxStack;
        }

        public void setMaxStack(int maxStack) {
            this.maxStack = maxStack;
        }

        public int getMaxLocals() {
            return maxLocals;
        }

        public void setMaxLocals(int maxLocals) {
            this.maxLocals = maxLocals;
        }

        public int getParamsLength() {
            return paramsLength;
        }

        public void setParamsLength(int paramsLength) {
            this.paramsLength = paramsLength;
        }

        public boolean isReturnVoid() {
            return isReturnVoid;
        }

        public void setReturnVoid(boolean returnVoid) {
            isReturnVoid = returnVoid;
        }

        public String getReturnTypeDesc() {
            return returnTypeDesc;
        }

        public void setReturnTypeDesc(String returnTypeDesc) {
            this.returnTypeDesc = returnTypeDesc;
        }

        public String[] getParamsTypes() {
            return paramsTypes;
        }

        public void setParamsTypes(String[] paramsTypes) {
            this.paramsTypes = paramsTypes;
        }

        public int getCallbackIndex() {
            return callbackIndex;
        }

        public void setCallbackIndex(int callbackIndex) {
            this.callbackIndex = callbackIndex;
        }

        public boolean isCallback() {
            return isCallback;
        }

        public void setCallback(boolean callback) {
            isCallback = callback;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "MethodInfo{" +
                    "id=" + id +
                    ", access=" + access +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", signature='" + signature + '\'' +
                    ", exceptions=" + Arrays.toString(exceptions) +
                    ", maxStack=" + maxStack +
                    ", maxLocals=" + maxLocals +
                    ", paramsLength=" + paramsLength +
                    ", isReturnVoid=" + isReturnVoid +
                    ", returnTypeDesc='" + returnTypeDesc + '\'' +
                    ", paramsTypes=" + Arrays.toString(paramsTypes) +
                    ", isCallback=" + isCallback +
                    ", index=" + index +
                    '}';
        }

    }

}
