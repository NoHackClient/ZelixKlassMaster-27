package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

public class ResolvedMethodHandleConstant extends MethodHandleConstantBase implements ConstantReferenceVisitable, MemberRefReplaceable {
    public final MethodHandleRefKind refKind;
    public ResolvedMemberRef memberRef;

    public boolean isInvokeStatic() {
        return this.refKind == MethodHandleRefKind.REF_INVOKE_STATIC;
    }

    public ResolvedMemberRef getMemberRef() {
        return this.memberRef;
    }

    public boolean matches(MethodHandleRefKind methodHandleRefKind, ResolvedMemberRef resolvedMemberRef) {
        return this.refKind == methodHandleRefKind
                && this.memberRef.getTag().equals(resolvedMemberRef.getTag())
                && this.memberRef.matches(resolvedMemberRef.getReferencedClassName(), resolvedMemberRef.getMemberName(), resolvedMemberRef.getDescriptor());
    }

    public String getMemberDescriptor() {
        return this.memberRef.getDescriptor();
    }

    public void collectReferencedClasses(Set set1, Set set2, Set set3, Set set4) {
        String string = this.memberRef.getReferencedClassName();
        ClassFileBase classFileBase = null;
        Set set5;
        if (this.isStaticKind()) {
            set5 = set2;
        } else {
            set5 = set1;
        }

        int[] ba;
        if (!string.startsWith("[")) {
            classFileBase = ClassHierarchyNode.findClassFile(string);
            if (classFileBase != null) {
                if (classFileBase.isMultiRelease()) {
                    set5.addAll(classFileBase.getAllVersions());
                    ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
                } else {
                    set5.add(classFileBase);
                    ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
                }
            } else {
                ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
            }
        } else {
            ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
        }

        switch (ba[this.refKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) this.memberRef.getResolvedMember();
                if (abstractFieldInfo != null) {
                    set3.add(abstractFieldInfo);
                    ClassFileBase classFileBase2 = abstractFieldInfo.getOwningClass();
                    if (classFileBase == null || classFileBase2 != classFileBase) {
                        if (classFileBase2.isMultiRelease()) {
                            set5.addAll(classFileBase2.getAllVersions());
                        } else {
                            set5.add(classFileBase2);
                        }
                    }
                }
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) this.memberRef.getResolvedMember();
                if (abstractMethodInfo != null) {
                    set4.add(abstractMethodInfo);
                    ClassFileBase classFileBase1 = abstractMethodInfo.getOwningClass();
                    if ((classFileBase == null || classFileBase1 != classFileBase) && !ClassHierarchyNode.isUnknownClass(classFileBase1.getClassName())) {
                        if (classFileBase1.isMultiRelease()) {
                            set5.addAll(classFileBase1.getAllVersions());
                        } else {
                            set5.add(classFileBase1);
                        }
                    }
                }
        }
    }

    public void collectProgramMethods(Set set1) {
        switch (MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE[this.refKind.ordinal()]) {
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) this.memberRef.getResolvedMember();
                if (abstractMethodInfo != null && abstractMethodInfo.isProgramMember()) {
                    set1.add((MethodInfo) abstractMethodInfo);
                }
            case 1:
            case 2:
            case 3:
            case 4:
        }
    }

    public String getMemberName() {
        return this.memberRef.getMemberName();
    }

    @Override
    public void replaceMemberRef(ResolvedMemberRef resolvedMemberRef, ResolvedMemberRef resolvedMemberRef1) {
        if (this.memberRef == resolvedMemberRef) {
            this.memberRef = resolvedMemberRef1;
        }
    }

    public MemberInfo getReferencedMember() {
        return this.memberRef.getResolvedMember();
    }

    public MethodHandleRefKind getRefKind() {
        return this.refKind;
    }

    public String getMemberClassName() {
        return this.memberRef.getReferencedClassName();
    }

    public boolean isStaticKind() {
        switch (MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE[this.refKind.ordinal()]) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 9:
                return false;
            case 2:
            case 4:
            case 6:
                return true;
            default:
                throw new ZkmRuntimeException("Unhandled reference kind : " + this.refKind + " : " + this.getOwnerLocationDescription());
        }
    }

    @Override
    public String getValueString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.refKind);
        stringBuilder.append(" : ");
        stringBuilder.append(this.memberRef.getValueString());
        return stringBuilder.toString();
    }

    public ResolvedMethodHandleConstant(AbstractConstantPool abstractConstantPool, MethodHandleRefKind methodHandleRefKind, ResolvedMemberRef resolvedMemberRef) {
        super(0, abstractConstantPool);
        this.refKind = methodHandleRefKind;
        this.memberRef = resolvedMemberRef;
    }

    public ResolvedMethodHandleConstant(
            int ba,
            AbstractConstantPool abstractConstantPool,
            MethodHandleRefKind methodHandleRefKind,
            ResolvedMemberRef resolvedMemberRef,
            ListMultimap listMultimap
    ) {
        super(ba, abstractConstantPool);
        this.refKind = methodHandleRefKind;
        this.memberRef = resolvedMemberRef;
        listMultimap.addValue(resolvedMemberRef, this);
    }

    public void collectReferencedProgramClasses(Set set1, Set set2, Set set3, Set set4) {
        String string = this.memberRef.getReferencedClassName();
        ProgramClass programClass1 = null;
        int[] ba;
        if (!string.startsWith("[")) {
            programClass1 = ClassHierarchyNode.findProgramClass(string);
            if (programClass1 != null) {
                programClass1 = this.selectVersionedProgramClass(programClass1);
                if (this.isStaticKind()) {
                    set2.add(programClass1);
                    ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
                } else {
                    set1.add(programClass1);
                    ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
                }
            } else {
                ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
            }
        } else {
            ba = MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE;
        }

        switch (ba[this.refKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) this.memberRef.getResolvedMember();
                if (abstractFieldInfo != null && abstractFieldInfo.isProgramMember()) {
                    set3.add((FieldInfo) abstractFieldInfo);
                    ProgramClass programClass3 = (ProgramClass) abstractFieldInfo.getOwningClass();
                    if (programClass1 == null || programClass3 != programClass1) {
                        programClass3 = this.selectVersionedProgramClass(programClass3);
                        if (this.isStaticKind()) {
                            set2.add(programClass3);
                        } else {
                            set1.add(programClass3);
                        }
                    }
                }
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) this.memberRef.getResolvedMember();
                if (abstractMethodInfo != null && abstractMethodInfo.isProgramMember()) {
                    set4.add((MethodInfo) abstractMethodInfo);
                    ProgramClass programClass2 = (ProgramClass) abstractMethodInfo.getOwningClass();
                    if (programClass1 == null || programClass2 != programClass1) {
                        this.selectVersionedProgramClass(programClass1);
                        if (this.isStaticKind()) {
                            set2.add(programClass2);
                        } else {
                            set1.add(programClass2);
                        }
                    }
                }
        }
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(MethodHandleConstantBase.TAG.getTagValue());
        dataOutputStream.writeByte(this.refKind.getKindValue());
        switch (MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE[this.refKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
                dataOutputStream.writeShort(this.memberRef.getIndex());
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                dataOutputStream.writeShort(this.memberRef.getIndex());
                break;
            case 9:
                dataOutputStream.writeShort(this.memberRef.getIndex());
        }
    }

    @Override
    public String getDisplayString() {
        return this.memberRef.getReferencedClassName() + '.' + this.memberRef.getMemberName();
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    public boolean isMethodKind() {
        boolean bl = false;
        switch (MethodHandleKindSwitchMap.REF_KIND_SWITCH_TABLE[this.refKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                bl = true;
                break;
            case 9:
                bl = true;
        }

        return bl;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
