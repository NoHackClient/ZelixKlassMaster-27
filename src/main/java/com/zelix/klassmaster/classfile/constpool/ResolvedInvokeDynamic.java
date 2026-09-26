package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.classfile.hierarchy.LibraryMethod;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResolvedInvokeDynamic extends ResolvedDynamicRef implements ConstantKeyProvider {
    public static final ConstantPoolTag TAG = ConstantPoolTag.INVOKE_DYNAMIC;
    public AbstractMethodInfo targetMethod;
    public AbstractMethodInfo lambdaImplMethod;

    public ResolvedInvokeDynamic(int ba, AbstractConstantPool abstractConstantPool, int bb, ResolvedNameAndType resolvedNameAndType) {
        super(ba, abstractConstantPool, bb, resolvedNameAndType);
    }

    public AbstractMethodInfo getLambdaImplMethod() {
        return this.lambdaImplMethod;
    }

    public void addReturnTypeIfSupertype(ClassFileBase classFileBase, Set set1) {
        String string = MethodSignature.getReturnPart(super.nameAndType.getDescriptor());
        if (string.length() > 2 && string.charAt(string.length() - 1) == ';') {
            String string1 = string.substring(1, string.length() - 1);
            if (!string1.equals(classFileBase.getClassName())) {
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string1);
                if (classHierarchyNode != null && classHierarchyNode.isProgramClass()) {
                    ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(classFileBase.getClassName());
                    if (classHierarchyNode1 != null) {
                        HashMap hashMap = ZkmUtils.createHashMap(13);
                        classHierarchyNode1.collectAllSubtypes(hashMap);
                        if (hashMap.containsKey(classHierarchyNode)) {
                            set1.add(classHierarchyNode.getProgramClass());
                        }
                    }
                }
            }
        }
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public String getConstantKey() {
        return super.nameAndType.getConstantKey() + '~' + this.bootstrapMethod.getUniqueKey();
    }

    public void setParameterDescriptor(String string) {
        String string1 = super.nameAndType.getDescriptor();
        String string2 = '(' + string + string1.substring(string1.indexOf(41));
        super.nameAndType.setDescriptor(string2);
    }

    public AbstractMethodInfo findMethodInHierarchy(
            ClassFileBase classFileBase,
            MethodSignature methodSignature1,
            Integer integer,
            String string,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        classFileBase.getClassName();
        ClassFileBase classFileBase1 = classFileBase;
        AbstractMethodInfo abstractMethodInfo = null;

        while (abstractMethodInfo == null && classFileBase1 != null) {
            Integer integer1 = classFileBase1.hasReleaseVersion() ? classFileBase1.getReleaseVersion() : integer;
            abstractMethodInfo = ResolvedMethodRef.findMethodInClass(classFileBase1, methodSignature1, classMemberLookup1);
            hashSet.add(classFileBase1);
            String string1 = classFileBase1.getSuperclassName();
            if (string1 == null) {
                break;
            }

            classFileBase1 = classResolver1.getVersionedClass(string1, integer1, string, ignoreMissingReferencesSpec1);
        }

        if (abstractMethodInfo == null) {
            classFileBase1 = classFileBase;

            while (abstractMethodInfo == null && classFileBase1 != null) {
                abstractMethodInfo = ResolvedMethodRef.findMethodInSuperInterfaces(
                        classFileBase1, methodSignature1, integer, classMemberLookup1, classResolver1, hashSet, string, ignoreMissingReferencesSpec1
                );
                if (abstractMethodInfo == null) {
                    String string2 = classFileBase1.getSuperclassName();
                    if (string2 == null) {
                        break;
                    }

                    Integer integer2 = classFileBase1.hasReleaseVersion() ? classFileBase1.getReleaseVersion() : integer;
                    classFileBase1 = classResolver1.getVersionedClass(string2, integer2, string, ignoreMissingReferencesSpec1);
                }
            }
        }

        return abstractMethodInfo;
    }

    public AbstractMethodInfo getTargetMethod() {
        return this.targetMethod;
    }

    public void resolveTargetMethod(
            Set set1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        String string = super.nameAndType.getName();
        String string1 = MethodSignature.getReturnPart(super.nameAndType.getDescriptor());
        if (this.bootstrapMethod.getBootstrapClassName().equals("java/lang/runtime/ObjectMethods")) {
            List list1 = MethodSignature.splitParameterDescriptors(super.nameAndType.getDescriptor());
            if (list1.size() >= 1) {
                String string3 = (String) list1.get(0);
                if (string3.startsWith("L") && string3.endsWith(";")) {
                    String string2 = string3.substring(1, string3.length() - 1);
                    ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string2);
                    if (programClass1 != null) {
                        MethodSignature methodSignature1;
                        if (string.equals("toString")) {
                            methodSignature1 = new MethodSignature(string, "()Ljava/lang/String;");
                        } else if (string.equals("equals")) {
                            methodSignature1 = new MethodSignature(string, "(Ljava/lang/Object;)Z");
                        } else if (string.equals("hashCode")) {
                            methodSignature1 = new MethodSignature(string, "()I");
                        } else {
                            methodSignature1 = null;
                        }

                        this.targetMethod = programClass1.findMethodBySignature(methodSignature1);
                    }
                }
            }
        } else if (this.bootstrapMethod.getBootstrapClassName().equals("java/lang/runtime/SwitchBootstraps")) {
            MemberInfo memberInfo2 = this.bootstrapMethod.getMethodHandle().getMemberRef().getResolvedMember();
            if (memberInfo2 != null && memberInfo2.isMethod()) {
                this.targetMethod = (AbstractMethodInfo) memberInfo2;
            }
        } else if (this.bootstrapMethod.getBootstrapClassName().equals("java/lang/runtime/TemplateRuntime")) {
            MemberInfo memberInfo3 = this.bootstrapMethod.getMethodHandle().getMemberRef().getResolvedMember();
            if (memberInfo3 != null && memberInfo3.isMethod()) {
                this.targetMethod = (AbstractMethodInfo) memberInfo3;
            }
        } else if (this.bootstrapMethod.getBootstrapClassName().equals("java/lang/invoke/StringConcatFactory")) {
            MemberInfo memberInfo4 = this.bootstrapMethod.getMethodHandle().getMemberRef().getResolvedMember();
            if (memberInfo4 != null && memberInfo4.isMethod()) {
                this.targetMethod = (AbstractMethodInfo) memberInfo4;
            }
        } else {
            ConstantPoolEntry[] constantPoolEntrys = this.bootstrapMethod.copyArguments();
            if (constantPoolEntrys.length == 3
                    && constantPoolEntrys[0] instanceof ResolvedMethodType
                    && constantPoolEntrys[1] instanceof ResolvedMethodHandleConstant
                    && constantPoolEntrys[2] instanceof ResolvedMethodType
                    && HiddenOptionFlags.FOLLOW_LAMBDA_METHOD_HANDLES) {
                ResolvedMethodHandleConstant resolvedMethodHandleConstant = (ResolvedMethodHandleConstant) constantPoolEntrys[1];
                ResolvedMemberRef resolvedMemberRef = resolvedMethodHandleConstant.getMemberRef();
                MemberInfo memberInfo5 = resolvedMemberRef.getResolvedMember();
                if (memberInfo5 != null && memberInfo5.isMethod()) {
                    AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) memberInfo5;
                    if (abstractMethodInfo.isProgramMember()) {
                        set1.add((MethodInfo) abstractMethodInfo);
                        List list2 = MethodSignature.splitParameterDescriptors(abstractMethodInfo.getDescriptor());
                        MemberInfo memberInfo1 = this.bootstrapMethod.getMethodHandle().getMemberRef().getResolvedMember();
                        if (memberInfo1 != null) {
                            ClassFileBase classFileBase = memberInfo1.getOwningClass();
                            if (classFileBase != null
                                    && classFileBase.getClassName().equals("java/lang/invoke/LambdaMetafactory")
                                    && memberInfo1.getSourceName().equals("metafactory")) {
                                abstractMethodInfo.markLambdaImplementation();
                                List list3 = MethodSignature.splitParameterDescriptors(super.nameAndType.getDescriptor());
                                ResolvedMethodType resolvedMethodType = (ResolvedMethodType) constantPoolEntrys[2];
                                String string4 = resolvedMethodType.getDescriptor();
                                List list4 = MethodSignature.splitParameterDescriptors(string4);
                                if (!abstractMethodInfo.isInstanceNonConstructor() || !list3.isEmpty()) {
                                    if (abstractMethodInfo.isInstanceNonConstructor()) {
                                        String string8 = (String) list3.remove(0);
                                    }

                                    if (list2.size() > list3.size()) {
                                    }

                                    int ba = list2.size() - list4.size();
                                    int bb = list2.size() - ba;
                                    abstractMethodInfo.setLambdaArgCount(bb);
                                    this.lambdaImplMethod = abstractMethodInfo;
                                }
                            }
                        }
                    }
                }
            }

            if (string1.charAt(0) == '[' || ConstantPoolEntry.isPrimitiveDescriptor(string1)) {
                return;
            }

            String string5 = string1.substring(1, string1.length() - 1);
            ConstantPoolEntry[] constantPoolEntrys1 = this.bootstrapMethod.copyArguments();
            HashSet hashSet = ZkmUtils.createHashSet();

            for (int i = 0; i < constantPoolEntrys1.length; i++) {
                if (constantPoolEntrys1[i] instanceof ResolvedMethodType) {
                    hashSet.add(((ResolvedMethodType) constantPoolEntrys1[i]).getDescriptor());
                } else if (constantPoolEntrys1[i] instanceof ResolvedMethodHandleConstant) {
                    ResolvedMethodHandleConstant resolvedMethodHandleConstant1 = (ResolvedMethodHandleConstant) constantPoolEntrys1[i];
                    if (resolvedMethodHandleConstant1 != null && resolvedMethodHandleConstant1.isMethodKind()) {
                        hashSet.add(resolvedMethodHandleConstant1.getMemberRef().getDescriptor());
                    }
                }
            }

            ClassFileBase classFileBase1 = this.getOwnerClass();
            Integer integer = classFileBase1.hasReleaseVersion() ? classFileBase1.getReleaseVersion() : null;
            ClassFileBase classFileBase2 = classResolver1.getVersionedClass(
                    string5, integer, "looking for class '" + ZkmUtils.slashesToDots(string5) + "' which is referenced in class '" + this.getOwnerFilePath() + "'"
            );
            if (!hashSet.isEmpty()) {
                Iterator iterator = hashSet.iterator();

                while (iterator.hasNext()) {
                    String string6 = (String) iterator.next();
                    MethodSignature methodSignature2 = new MethodSignature(string, string6);
                    if (MethodSignature.isSignaturePolymorphic(string5, string, classMemberLookup1) && this.getOwnerClass().supportsJava8()) {
                        ClasspathClassFile classpathClassFile = (ClasspathClassFile) classResolver1.getVersionedClass(
                                "java/lang/invoke/MethodHandle",
                                integer,
                                "looking for class 'java/lang/invoke/MethodHandle' which is referenced in class '" + this.getOwnerFilePath() + "'"
                        );
                        LibraryMethod libraryMethod = classpathClassFile.getOrCreateMethod(methodSignature2);
                        this.targetMethod = libraryMethod;
                        return;
                    }

                    String string7 = "looking for method '"
                            + methodSignature2.getNameWithParameters((Map) null)
                            + "' in class '"
                            + ZkmUtils.slashesToDots(string5)
                            + "' which is referenced in class '"
                            + this.getOwnerFilePath()
                            + "'";
                    this.targetMethod = this.findMethodInHierarchy(
                            classFileBase2, methodSignature2, integer, string7, classMemberLookup1, classResolver1, ignoreMissingReferencesSpec1
                    );
                    if (this.targetMethod != null) {
                        if (this.targetMethod.isProgramMember()) {
                            set1.add((MethodInfo) this.targetMethod);
                        }
                        break;
                    }
                }
            }
        }
    }

    public ResolvedInvokeDynamic(AbstractConstantPool abstractConstantPool, ResolvedNameAndType resolvedNameAndType, BootstrapMethodEntry bootstrapMethodEntry) {
        super(abstractConstantPool, resolvedNameAndType, bootstrapMethodEntry);
    }

    public ResolvedInvokeDynamic(int ba, ResolvedNameAndType resolvedNameAndType, ResolvedInvokeDynamic resolvedInvokeDynamic1) {
        super(ba, resolvedNameAndType, resolvedInvokeDynamic1);
        this.targetMethod = resolvedInvokeDynamic1.targetMethod;
        this.lambdaImplMethod = resolvedInvokeDynamic1.lambdaImplMethod;
    }

    @Override
    public void collectReferencedClasses(Set set1, Set set2, Set set3, Set set4) {
        super.collectReferencedClasses(set1, set2, set3, set4);
        if (this.targetMethod != null) {
            set4.add(this.targetMethod);
            ClassFileBase classFileBase = this.targetMethod.getOwningClass();
            if (!ClassHierarchyNode.isUnknownClass(classFileBase.getClassName())) {
                if (this.targetMethod.isStatic()) {
                    set2.add((ProgramClass) classFileBase);
                } else {
                    set1.add(classFileBase);
                    this.addReturnTypeIfSupertype(classFileBase, set1);
                }
            }
        }
    }

    @Override
    public void collectReferencedProgramClasses(Set set1, Set set2, Set set3, Set set4) {
        super.collectReferencedProgramClasses(set1, set2, set3, set4);
        if (this.targetMethod != null && this.targetMethod.isProgramMember()) {
            set4.add((MethodInfo) this.targetMethod);
            ClassFileBase classFileBase = this.targetMethod.getOwningClass();
            if (this.targetMethod.isStatic()) {
                set2.add((ProgramClass) classFileBase);
            } else {
                set1.add((ProgramClass) classFileBase);
                this.addReturnTypeIfSupertype(classFileBase, set1);
            }
        }
    }

    public final void updateNameFromTarget() {
        if (this.targetMethod != null && !ClassHierarchyNode.isUnknownClass(this.targetMethod.getClassName())) {
            String string = super.nameAndType.getName();
            String string1 = this.targetMethod.getJvmName();
            if (!string.equals(string1)) {
                super.nameAndType.setName(string1);
            }
        }
    }
}
