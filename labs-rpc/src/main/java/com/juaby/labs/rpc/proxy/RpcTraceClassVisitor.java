package com.juaby.labs.rpc.proxy;

import org.objectweb.asm.*;
import org.objectweb.asm.util.*;

import java.io.PrintWriter;

/**
 * Created by Juaby on 2015/8/29.
 */
public class RpcTraceClassVisitor extends ClassVisitor {

    /**
     * The print writer to be used to print the class. May be null.
     */
    private final PrintWriter pw;

    /**
     * The object that actually converts visit events into text.
     */
    public final RpcProxyParser p;

    /**
     * Constructs a new {@link TraceClassVisitor}.
     *
     * @param pw
     *            the print writer to be used to print the class.
     */
    public RpcTraceClassVisitor(final PrintWriter pw) {
        this(null, pw);
    }

    /**
     * Constructs a new {@link TraceClassVisitor}.
     *
     * @param cv
     *            the {@link ClassVisitor} to which this visitor delegates
     *            calls. May be <tt>null</tt>.
     * @param pw
     *            the print writer to be used to print the class.
     */
    public RpcTraceClassVisitor(final ClassVisitor cv, final PrintWriter pw) {
        this(cv, new Rpcifier(), pw);
    }

    /**
     * Constructs a new {@link TraceClassVisitor}.
     *
     * @param cv
     *            the {@link ClassVisitor} to which this visitor delegates
     *            calls. May be <tt>null</tt>.
     * @param p
     *            the object that actually converts visit events into text.
     * @param pw
     *            the print writer to be used to print the class. May be null if
     *            you simply want to use the result via
     *            {@link Printer#getText()}, instead of printing it.
     */
    public RpcTraceClassVisitor(final ClassVisitor cv, final RpcProxyParser p,
                                final PrintWriter pw) {
        super(Opcodes.ASM5, cv);
        this.pw = pw;
        this.p = p;
    }

    @Override
    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {
        p.visit(version, access, name, signature, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(final String file, final String debug) {
        p.visitSource(file, debug);
        super.visitSource(file, debug);
    }

    @Override
    public void visitOuterClass(final String owner, final String name,
                                final String desc) {
        p.visitOuterClass(owner, name, desc);
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        RpcProxyParser p = this.p.visitClassAnnotation(desc, visible);
        AnnotationVisitor av = cv == null ? null : cv.visitAnnotation(desc,
                visible);
        return new RpcTraceAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        RpcProxyParser p = this.p.visitClassTypeAnnotation(typeRef, typePath, desc,
                visible);
        AnnotationVisitor av = cv == null ? null : cv.visitTypeAnnotation(
                typeRef, typePath, desc, visible);
        return new RpcTraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        p.visitClassAttribute(attr);
        super.visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(final String name, final String outerName,
                                final String innerName, final int access) {
        p.visitInnerClass(name, outerName, innerName, access);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(final int access, final String name,
                                   final String desc, final String signature, final Object value) {
        RpcProxyParser p = this.p.visitField(access, name, desc, signature, value);
        FieldVisitor fv = cv == null ? null : cv.visitField(access, name, desc,
                signature, value);
        return new RpcTraceFieldVisitor(fv, p);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        RpcProxyParser p = this.p.visitMethod(access, name, desc, signature,
                exceptions);
        MethodVisitor mv = cv == null ? null : cv.visitMethod(access, name,
                desc, signature, exceptions);
        return new RpcTraceMethodVisitor(mv, p);
    }

    @Override
    public void visitEnd() {
        p.visitClassEnd();
        if (pw != null) {
            p.print(pw);
            pw.flush();
        }
        super.visitEnd();
    }

}
