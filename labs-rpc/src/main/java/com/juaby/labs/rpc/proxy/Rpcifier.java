/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.juaby.labs.rpc.proxy;

import java.util.HashMap;
import java.util.Map;

import com.juaby.labs.rpc.util.RpcCallback;
import org.objectweb.asm.*;
import org.objectweb.asm.util.ASMifiable;

/**
 * A {@link RpcProxyParser} that prints the ASM code to generate the classes if visits.
 *
 * Created by Juaby on 2015/8/29.
 */
public class Rpcifier extends RpcProxyParser {

    /**
     * The name of the visitor variable in the produced code.
     */
    protected final String name;

    /**
     * Identifier of the annotation visitor variable in the produced code.
     */
    protected final int id;

    /**
     * The label names. This map associates String values to Label keys. It is
     * used only in ASMifierMethodVisitor.
     */
    protected Map<Label, String> labelNames;

    /**
     * Pseudo access flag used to distinguish class access flags.
     */
    private static final int ACCESS_CLASS = 262144;

    /**
     * Pseudo access flag used to distinguish field access flags.
     */
    private static final int ACCESS_FIELD = 524288;

    /**
     * Pseudo access flag used to distinguish inner class flags.
     */
    private static final int ACCESS_INNER = 1048576;

    private ServiceClassInfo classInfo;

    /**
     * Constructs a new {@link Rpcifier}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     * {@link #Rpcifier(int, String, int)} version.
     *
     * @throws IllegalStateException
     *             If a subclass calls this constructor.
     */
    public Rpcifier() {
        this(Opcodes.ASM5, "cw", 0);
        if (getClass() != Rpcifier.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs a new {@link Rpcifier}.
     *
     * @param api
     *            the ASM API version implemented by this class. Must be one of
     *            {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param name
     *            the name of the visitor variable in the produced code.
     * @param id
     *            identifier of the annotation visitor variable in the produced
     *            code.
     */
    protected Rpcifier(final int api, final String name, final int id) {
        super(api);
        this.name = name;
        this.id = id;
    }

    public ServiceClassInfo parser(final String fullyQualifiedClassName, ServiceClassInfo classInfo) throws Exception {
        this.classInfo = classInfo;
        ClassReader cr = new ClassReader(fullyQualifiedClassName);
        cr.accept(new RpcTraceClassVisitor(null, this), ClassReader.SKIP_DEBUG);
        return this.classInfo;
    }

    // ------------------------------------------------------------------------
    // Classes
    // ------------------------------------------------------------------------

    @Override
    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {
        classInfo.setVersion(version);
        String simpleName;
        int n = name.lastIndexOf('/');
        if (n == -1) {
            simpleName = name;
        } else {
            String packageName = name.substring(0, n).replace('/', '.');
            classInfo.setPackageName(packageName);
            simpleName = name.substring(n + 1);
        }
        classInfo.setSimpleName(simpleName);

        classInfo.setAccess(access | ACCESS_CLASS);
        classInfo.setName(name);
        classInfo.setSignature(signature);
        classInfo.setSuperName(superName);
        if (interfaces != null && interfaces.length > 0) {
            classInfo.setInterfaces(interfaces);
        } else {
        }
    }

    @Override
    public void visitSource(final String file, final String debug) {
        classInfo.setSource(file);
        classInfo.setDebug(debug);
    }

    @Override
    public void visitOuterClass(final String owner, final String name,
                                final String desc) {
    }

    @Override
    public Rpcifier visitClassAnnotation(final String desc,
                                         final boolean visible) {
        return visitAnnotation(desc, visible);
    }

    @Override
    public Rpcifier visitClassTypeAnnotation(final int typeRef,
                                             final TypePath typePath, final String desc, final boolean visible) {
        return visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitClassAttribute(final Attribute attr) {
        visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(final String name, final String outerName,
                                final String innerName, final int access) {
    }

    @Override
    public Rpcifier visitField(final int access, final String name,
                               final String desc, final String signature, final Object value) {
        ServiceClassInfo.FieldInfo fieldInfo = new ServiceClassInfo.FieldInfo();
        fieldInfo.setAccess(access | ACCESS_FIELD);
        fieldInfo.setName(name);
        fieldInfo.setDesc(desc);
        fieldInfo.setSignature(signature);
        fieldInfo.setValue(value);
        Rpcifier a = createASMifier("fv", 0);
        classInfo.getFields().add(fieldInfo);
        return a;
    }

    @Override
    public Rpcifier visitMethod(final int access, final String name,
                                final String desc, final String signature, final String[] exceptions) {
        ServiceClassInfo.MethodInfo methodInfo = new ServiceClassInfo.MethodInfo();
        methodInfo.setAccess(access);
        methodInfo.setName(name);
        methodInfo.setDesc(desc);
        methodInfo.setSignature(signature);
        if (exceptions != null && exceptions.length > 0) {
            methodInfo.setExceptions(exceptions);
        } else {
        }
        Rpcifier a = createASMifier("mv", 0);
        if (methodInfo.getSignature() != null) {
            boolean isReturnVoid = false;
            String returnTypeDesc = methodInfo.getDesc().substring(methodInfo.getDesc().lastIndexOf(")") + 1);
            if (returnTypeDesc != null && returnTypeDesc.length() == 1) {
                isReturnVoid = true;
            } else {
                returnTypeDesc = returnTypeDesc.substring(1, returnTypeDesc.lastIndexOf(";"));
            }

            final String paramsType = methodInfo.getDesc().substring(methodInfo.getDesc().indexOf("(") + 1, methodInfo.getDesc().lastIndexOf(")"));
            int paramsLength = 0;
            if (paramsType != null && paramsType.length() > 2) {
                String [] paramsTypes = paramsType.split(";");
                methodInfo.setParamsTypes(paramsTypes);
                paramsLength = paramsTypes.length;

                for (int p = 0; p < paramsTypes.length; p++) {
                    if (RpcCallback.class.getName().equals(paramsTypes[p].substring(1).replaceAll("/", "."))) {
                        methodInfo.setCallback(true);
                        methodInfo.setCallbackIndex(p);
                        break;
                    }
                }
            }
            methodInfo.setReturnVoid(isReturnVoid);
            methodInfo.setParamsLength(paramsLength);
            methodInfo.setReturnTypeDesc(returnTypeDesc);
            classInfo.getMethods().put(methodInfo.getSignature(), methodInfo);
        }
        return a;
    }

    @Override
    public void visitClassEnd() {
    }

    // ------------------------------------------------------------------------
    // Annotations
    // ------------------------------------------------------------------------

    @Override
    public void visit(final String name, final Object value) {
    }

    @Override
    public void visitEnum(final String name, final String desc,
                          final String value) {
    }

    @Override
    public Rpcifier visitAnnotation(final String name, final String desc) {
        Rpcifier a = createASMifier("av", id + 1);
        return a;
    }

    @Override
    public Rpcifier visitArray(final String name) {
        Rpcifier a = createASMifier("av", id + 1);
        return a;
    }

    @Override
    public void visitAnnotationEnd() {
    }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    @Override
    public Rpcifier visitFieldAnnotation(final String desc,
                                         final boolean visible) {
        return visitAnnotation(desc, visible);
    }

    @Override
    public Rpcifier visitFieldTypeAnnotation(final int typeRef,
                                             final TypePath typePath, final String desc, final boolean visible) {
        return visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitFieldAttribute(final Attribute attr) {
        visitAttribute(attr);
    }

    @Override
    public void visitFieldEnd() {
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    @Override
    public void visitParameter(String parameterName, int access) {
    }

    @Override
    public Rpcifier visitAnnotationDefault() {
        Rpcifier a = createASMifier("av", 0);
        return a;
    }

    @Override
    public Rpcifier visitMethodAnnotation(final String desc,
                                          final boolean visible) {
        return visitAnnotation(desc, visible);
    }

    @Override
    public Rpcifier visitMethodTypeAnnotation(final int typeRef,
                                              final TypePath typePath, final String desc, final boolean visible) {
        return visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public Rpcifier visitParameterAnnotation(final int parameter,
                                             final String desc, final boolean visible) {
        Rpcifier a = createASMifier("av", 0);
        return a;
    }

    @Override
    public void visitMethodAttribute(final Attribute attr) {
        visitAttribute(attr);
    }

    @Override
    public void visitCode() {
    }

    @Override
    public void visitFrame(final int type, final int nLocal,
                           final Object[] local, final int nStack, final Object[] stack) {
        switch (type) {
            case Opcodes.F_NEW:
            case Opcodes.F_FULL:
                declareFrameTypes(nLocal, local);
                declareFrameTypes(nStack, stack);
                if (type == Opcodes.F_NEW) {
                } else {
                }
                break;
            case Opcodes.F_APPEND:
                declareFrameTypes(nLocal, local);
                break;
            case Opcodes.F_CHOP:
                break;
            case Opcodes.F_SAME:
                break;
            case Opcodes.F_SAME1:
                declareFrameTypes(1, stack);
                break;
        }
    }

    @Override
    public void visitInsn(final int opcode) {
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner,
                               final String name, final String desc) {
    }

    @Deprecated
    @Override
    public void visitMethodInsn(final int opcode, final String owner,
                                final String name, final String desc) {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        doVisitMethodInsn(opcode, owner, name, desc,
                opcode == Opcodes.INVOKEINTERFACE);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
                                final String name, final String desc, final boolean itf) {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        doVisitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void doVisitMethodInsn(final int opcode, final String owner,
                                   final String name, final String desc, final boolean itf) {
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
                                       Object... bsmArgs) {
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        declareLabel(label);
    }

    @Override
    public void visitLabel(final Label label) {
        declareLabel(label);
    }

    @Override
    public void visitLdcInsn(final Object cst) {
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max,
                                     final Label dflt, final Label... labels) {
        for (int i = 0; i < labels.length; ++i) {
            declareLabel(labels[i]);
        }
        declareLabel(dflt);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
                                      final Label[] labels) {
        for (int i = 0; i < labels.length; ++i) {
            declareLabel(labels[i]);
        }
        declareLabel(dflt);
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
    }

    @Override
    public Rpcifier visitInsnAnnotation(final int typeRef,
                                        final TypePath typePath, final String desc, final boolean visible) {
        return visitTypeAnnotation("visitInsnAnnotation", typeRef, typePath,
                desc, visible);
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end,
                                   final Label handler, final String type) {
        declareLabel(start);
        declareLabel(end);
        declareLabel(handler);
    }

    @Override
    public Rpcifier visitTryCatchAnnotation(final int typeRef,
                                            final TypePath typePath, final String desc, final boolean visible) {
        return visitTypeAnnotation("visitTryCatchAnnotation", typeRef,
                typePath, desc, visible);
    }

    @Override
    public void visitLocalVariable(final String name, final String desc,
                                   final String signature, final Label start, final Label end,
                                   final int index) {
    }

    @Override
    public RpcProxyParser visitLocalVariableAnnotation(int typeRef, TypePath typePath,
                                                       Label[] start, Label[] end, int[] index, String desc,
                                                       boolean visible) {
        Rpcifier a = createASMifier("av", 0);
        return a;
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
    }

    @Override
    public void visitMethodEnd() {
    }

    // ------------------------------------------------------------------------
    // Common methods
    // ------------------------------------------------------------------------

    public Rpcifier visitAnnotation(final String desc, final boolean visible) {
        Rpcifier a = createASMifier("av", 0);
        return a;
    }

    public Rpcifier visitTypeAnnotation(final int typeRef,
                                        final TypePath typePath, final String desc, final boolean visible) {
        return visitTypeAnnotation("visitTypeAnnotation", typeRef, typePath,
                desc, visible);
    }

    public Rpcifier visitTypeAnnotation(final String method, final int typeRef,
                                        final TypePath typePath, final String desc, final boolean visible) {
        Rpcifier a = createASMifier("av", 0);
        return a;
    }

    public void visitAttribute(final Attribute attr) {
        if (attr instanceof ASMifiable) {
            if (labelNames == null) {
                labelNames = new HashMap<Label, String>();
            }
            ((ASMifiable) attr).asmify(null, "attr", labelNames); //TODO null
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    protected Rpcifier createASMifier(final String name, final int id) {
        return new Rpcifier(Opcodes.ASM5, name, id);
    }

    private void declareFrameTypes(final int n, final Object[] o) {
        for (int i = 0; i < n; ++i) {
            if (o[i] instanceof Label) {
                declareLabel((Label) o[i]);
            }
        }
    }

    /**
     * Appends a declaration of the given label to {@link buf}. This
     * declaration is of the form "Label lXXX = new Label();". Does nothing if
     * the given label has already been declared.
     *
     * @param l
     *            a label.
     */
    protected void declareLabel(final Label l) {
        if (labelNames == null) {
            labelNames = new HashMap<Label, String>();
        }
        String name = labelNames.get(l);
        if (name == null) {
            name = "l" + labelNames.size();
            labelNames.put(l, name);
        }
    }

}
