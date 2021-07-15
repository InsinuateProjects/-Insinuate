package io.insinuate.utils.maven;

import org.objectweb.asm.*;

import java.util.Map;

// 不稳定的 Relocate package 方案, 不建议使用.
public class RelocateClassVisitor extends ClassVisitor {

    public final Map<String, String> relocates;

    public RelocateClassVisitor(ClassVisitor classVisitor, Map<String, String> relocates) {
        super(Opcodes.ASM7, classVisitor);
        this.relocates = relocates;
    }

    private String toDestination(String path) {
        if (path == null) {
            return null;
        }
//        System.out.print("Destination: " + path + " -> ");
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
//        System.out.println(builder);
        return builder.toString();
    }

    private String toInternalName(String path) {
        if (path == null) {
            return null;
        }
//        System.out.print("InternalName: " + path + " -> ");
        for (Map.Entry<String, String> relocate : relocates.entrySet()) {
            path = path.replaceFirst(relocate.getKey().replace(".", "/"),
                    relocate.getValue().replace(".", "/"));
        }
//        System.out.println(path);
        return path;
    }

    private String toPackageName(String path) {
        if (path == null) {
            return null;
        }
//        System.out.print("PackageName: " + path + " -> ");
        for (Map.Entry<String, String> relocate : relocates.entrySet()) {
            path = path.replaceFirst(relocate.getKey().replace("/", "."),
                    relocate.getValue().replace("/", "."));
        }
//        System.out.println(path);
        return path;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String[] rawInterfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            rawInterfaces[i] = toDestination(interfaces[i]);
        }
        super.visit(version, access, toInternalName(name), toDestination(signature), toInternalName(superName), rawInterfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(toInternalName(name), toInternalName(outerName), innerName, access);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return getModuleVisitor(super.visitModule(name, access, version));
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return getRecordComponentVisitor(super.visitRecordComponent(toDestination(name), toDestination(descriptor), toDestination(signature)));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return getFieldVisitor(super.visitField(access, name, toDestination(descriptor), toDestination(signature), value));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return getMethodVisitor(super.visitMethod(access, name, toDestination(descriptor), toDestination(signature), exceptions));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return getAnnotationVisitor(super.visitAnnotation(toDestination(descriptor), visible));
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return getAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, toDestination(descriptor), visible));
    }


    public ModuleVisitor getModuleVisitor(ModuleVisitor moduleVisitor) {
        return new ModuleVisitor(Opcodes.ASM9, moduleVisitor) {
            @Override
            public void visitExport(String packaze, int access, String... modules) {
                System.out.println("visitExport: " + packaze);
                super.visitExport(toPackageName(packaze), access, modules);
            }
            @Override
            public void visitMainClass(String mainClass) {
                System.out.println("visitMainClass: " + mainClass);
                super.visitMainClass(toPackageName(mainClass));
            }
            @Override
            public void visitOpen(String packaze, int access, String... modules) {
                System.out.println("visitOpen: " + packaze);
                super.visitOpen(toPackageName(packaze), access, modules);
            }
            @Override
            public void visitPackage(String packaze) {
                System.out.println("visitPackage: " + packaze);
                super.visitPackage(toPackageName(packaze));
            }

        };
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
            public void visitLdcInsn(Object value) {
                Object result;
                if (value instanceof String) {
                    result = toPackageName(value.toString());
                } else if (value instanceof Type) {
                    result = toDestination(((Type) value).getDescriptor());
                } else  {
                    result = value;
                }

                super.visitLdcInsn(result);
            }

            @Override
            public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, toDestination(descriptor), visible));
            }

            @Override
            public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                super.visitLocalVariable(name, toDestination(descriptor), toDestination(signature), start, end, index);
            }

            @Override
            public void visitParameter(String name, int access) {
                super.visitParameter(toPackageName(name), access);
            }

            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitParameterAnnotation(parameter, toDestination(descriptor), visible));
            }

            @Override
            public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitTryCatchAnnotation(typeRef, typePath, toDestination(descriptor), visible));
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                super.visitFieldInsn(opcode, toInternalName(owner), name, toDestination(descriptor));
            }

            @Override
            public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                super.visitInvokeDynamicInsn(name, toDestination(descriptor), bootstrapMethodHandle, bootstrapMethodArguments);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                super.visitMethodInsn(opcode, toDestination(owner), toDestination(name), toDestination(descriptor), isInterface);
            }

            @Override
            public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
                super.visitMultiANewArrayInsn(toDestination(descriptor), numDimensions);
            }

            @Override
            public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitInsnAnnotation(typeRef, typePath, toDestination(descriptor), visible));
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitAnnotation(toDestination(descriptor), visible));
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, toDestination(descriptor), visible));
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                super.visitTypeInsn(opcode, toInternalName(type));
            }
        };
    }

    public FieldVisitor getFieldVisitor(FieldVisitor fieldVisitor) {
        return new FieldVisitor(Opcodes.ASM9, fieldVisitor) {
            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, toDestination(descriptor), visible));
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return getAnnotationVisitor(super.visitAnnotation(toDestination(descriptor), visible));
            }
        };
    }

    public AnnotationVisitor getAnnotationVisitor(AnnotationVisitor annotationVisitor) {
        return new AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {

            @Override
            public AnnotationVisitor visitArray(String name) {
//                if (!name.equals("d2"))
//                    return super.visitArray(name);

                return new AnnotationVisitor(Opcodes.ASM9, getAnnotationVisitor(super.visitArray(name))) {
                    @Override
                    public void visit(String name, Object value) {
                        if (!(value instanceof String)) {
                            return;
                        }
                        String string = value.toString();

                        super.visit(name, toDestination(string));
                    }
                };
            }

            @Override
            public void visitEnum(String name, String descriptor, String value) {
//                System.out.println("visitEnum: " + descriptor + " -> " + toDestination(descriptor));
                super.visitEnum(name, toInternalName(descriptor), value);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                return new AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(toDestination(name), toDestination(descriptor))) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                        return getAnnotationVisitor(super.visitAnnotation(toDestination(name), toDestination(descriptor)));
                    }

                    @Override
                    public void visitEnum(String name, String descriptor, String value) {
                        super.visitEnum(name, toDestination(descriptor), value);
                    }

                    @Override
                    public AnnotationVisitor visitArray(String name) {
                        /*if (!name.equals("d2"))
                            return super.visitArray(name);*/

                        return new AnnotationVisitor(Opcodes.ASM9, getAnnotationVisitor(super.visitArray(name))) {
                            @Override
                            public void visit(String name, Object value) {
                                if (!(value instanceof String)) {
                                    return;
                                }
                                String string = value.toString();

                                super.visit(name, toDestination(string));
                            }
                        };
                    }
                };
            }
        };
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(toDestination(owner), toDestination(name), toDestination(descriptor));
    }
}
