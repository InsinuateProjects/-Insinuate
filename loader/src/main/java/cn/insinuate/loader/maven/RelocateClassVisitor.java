package cn.insinuate.loader.maven;

import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RelocateClassVisitor extends ClassVisitor {

    public final Map<String, String> relocates;

    public RelocateClassVisitor(ClassVisitor classVisitor, Map<String, String> relocates) {
        super(Opcodes.ASM7, classVisitor);
        this.relocates = relocates;
    }

    private String toDestination(String path) {
        String[] split = path.split("L");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            for (Map.Entry<String, String> relocate : relocates.entrySet()) {
                s = s.replaceFirst(relocate.getKey().replace(".", "/"),
                        relocate.getValue().replace(".", "/"));
            }
            builder.append(s);
            if (i != split.length - 1) {
                builder.append("L");
            }
        }
        return builder.toString();
    }

    private String toInternalName(String path) {
        for (Map.Entry<String, String> relocate : relocates.entrySet()) {
            path = path.replaceFirst(relocate.getKey().replace(".", "/"),
                    relocate.getValue().replace(".", "/"));
        }
        return path;
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return getRecordComponentVisitor(super.visitRecordComponent(name, descriptor, signature));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return getFieldVisitor(super.visitField(access, name, toDestination(descriptor), signature, value));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return getMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return getAnnotationVisitor(super.visitAnnotation(descriptor, visible));
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return getAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
    }


    public RecordComponentVisitor getRecordComponentVisitor(RecordComponentVisitor recordComponentVisitor) {
        return new RecordComponentVisitor(Opcodes.ASM9, recordComponentVisitor) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitAnnotation(descriptor, visible));
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
            }
        };
    }

    public MethodVisitor getMethodVisitor(MethodVisitor methodVisitor) {
        return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
            @Override
            public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible));
            }

            @Override
            public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                super.visitLocalVariable(name, toDestination(descriptor), signature, start, end, index);
            }

            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitParameterAnnotation(parameter, descriptor, visible));
            }

            @Override
            public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible));
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                super.visitFieldInsn(opcode, owner, name, toDestination(descriptor));
            }

            @Override
            public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                super.visitInvokeDynamicInsn(name, toDestination(descriptor), bootstrapMethodHandle, bootstrapMethodArguments);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                super.visitMethodInsn(opcode, owner, name, toDestination(descriptor), isInterface);
            }

            @Override
            public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
                super.visitMultiANewArrayInsn(toDestination(descriptor), numDimensions);
            }

            @Override
            public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitInsnAnnotation(typeRef, typePath, descriptor, visible));
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitAnnotation(descriptor, visible));
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
            }
        };
    }

    public FieldVisitor getFieldVisitor(FieldVisitor fieldVisitor) {
        return new FieldVisitor(Opcodes.ASM9, fieldVisitor) {
            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitAnnotation(descriptor, visible));
            }
        };
    }

    public AnnotationVisitor getAnnotationVisitor(AnnotationVisitor annotationVisitor) {
        return new AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {
            @Override
            public void visitEnum(String name, String descriptor, String value) {
                super.visitEnum(name, toDestination(descriptor), value);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                return new AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(name, toDestination(descriptor))) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                        return super.visitAnnotation(name, toDestination(descriptor));
                    }

                    @Override
                    public void visitEnum(String name, String descriptor, String value) {
                        super.visitEnum(name, toDestination(descriptor), value);
                    }
                };
            }
        };
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(owner, name, toDestination(descriptor));
    }
}
