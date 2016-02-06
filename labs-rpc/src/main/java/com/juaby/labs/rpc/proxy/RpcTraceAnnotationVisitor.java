package com.juaby.labs.rpc.proxy;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Juaby on 2015/8/30.
 */
public class RpcTraceAnnotationVisitor extends AnnotationVisitor {

    private final RpcProxyParser p;

    public RpcTraceAnnotationVisitor(final RpcProxyParser p) {
        this(null, p);
    }

    public RpcTraceAnnotationVisitor(final AnnotationVisitor av, final RpcProxyParser p) {
        super(Opcodes.ASM5, av);
        this.p = p;
    }

    @Override
    public void visit(final String name, final Object value) {
        p.visit(name, value);
        super.visit(name, value);
    }

    @Override
    public void visitEnum(final String name, final String desc,
                          final String value) {
        p.visitEnum(name, desc, value);
        super.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name,
                                             final String desc) {
        RpcProxyParser p = this.p.visitAnnotation(name, desc);
        AnnotationVisitor av = this.av == null ? null : this.av
                .visitAnnotation(name, desc);
        return new RpcTraceAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        RpcProxyParser p = this.p.visitArray(name);
        AnnotationVisitor av = this.av == null ? null : this.av
                .visitArray(name);
        return new RpcTraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitEnd() {
        p.visitAnnotationEnd();
        super.visitEnd();
    }

}
