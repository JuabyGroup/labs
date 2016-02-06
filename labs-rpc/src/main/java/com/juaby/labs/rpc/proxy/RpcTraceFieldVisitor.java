package com.juaby.labs.rpc.proxy;

import org.objectweb.asm.*;

/**
 * Created by Juaby on 2015/8/30.
 */
public class RpcTraceFieldVisitor extends FieldVisitor {

    public final RpcProxyParser p;

    public RpcTraceFieldVisitor(final RpcProxyParser p) {
        this(null, p);
    }

    public RpcTraceFieldVisitor(final FieldVisitor fv, final RpcProxyParser p) {
        super(Opcodes.ASM5, fv);
        this.p = p;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        RpcProxyParser p = this.p.visitFieldAnnotation(desc, visible);
        AnnotationVisitor av = fv == null ? null : fv.visitAnnotation(desc,
                visible);
        return new RpcTraceAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        RpcProxyParser p = this.p.visitFieldTypeAnnotation(typeRef, typePath, desc,
                visible);
        AnnotationVisitor av = fv == null ? null : fv.visitTypeAnnotation(
                typeRef, typePath, desc, visible);
        return new RpcTraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        p.visitFieldAttribute(attr);
        super.visitAttribute(attr);
    }

    @Override
    public void visitEnd() {
        p.visitFieldEnd();
        super.visitEnd();
    }

}