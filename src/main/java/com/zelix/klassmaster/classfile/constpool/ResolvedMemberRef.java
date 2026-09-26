package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public abstract class ResolvedMemberRef extends ConstantPoolEntry implements NameAndTypeHolder, ClassConstantReplaceable, ConstantReferenceVisitable {
    public ResolvedClassConstant classConstant;
    public ResolvedNameAndType nameAndType;
    public MemberInfo resolvedMember;

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        if (this.classConstant == resolvedClassConstant) {
            this.classConstant = resolvedClassConstant1;
        }
    }

    public final void syncWithResolvedMember(ScriptEnvironment scriptEnvironment1, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        if (this.resolvedMember != null) {
            String string = this.resolvedMember.getClassName();
            String string1 = this.nameAndType.getDescriptor();
            String string2 = this.resolvedMember.getDescriptor();
            if (!ClassHierarchyNode.isUnknownClass(string)) {
                String string3 = this.nameAndType.getName();
                String string4 = this.resolvedMember.getJvmName();
                if (!string3.equals(string4)) {
                    this.nameAndType.setName(string4);
                }

                if (!string1.equals(string2)) {
                    this.nameAndType.setDescriptor(string2);
                }
            } else if (scriptEnvironment1 != null && !string1.equals(string2)) {
                boolean bl = false;

                try {
                    bl = MethodSignature.isSignaturePolymorphic(this.resolvedMember.getOriginalClassName(), this.resolvedMember.getSourceName(), classMemberLookup1);
                } catch (ClassFileLoadException classFileLoadException) {
                }

                if (!bl) {
                    ClassFileBase classFileBase = this.resolvedMember.getOwningClass();
                    String string5 = this.resolvedMember.isField() ? "field" : "method";
                    String string6 = "Class '"
                            + this.getOwnerJavaName()
                            + "' accesses a "
                            + string5
                            + " '"
                            + this.resolvedMember.toDisplayString()
                            + "' in class '"
                            + classFileBase.getOriginalDottedName()
                            + "'. However the descriptor of the "
                            + string5
                            + " needs to be changed to '"
                            + ConstantPoolEntry.formatMemberSignature(this.resolvedMember.getSourceName(), string1)
                            + "'.  However this change cannot be made because class '"
                            + classFileBase.getOriginalDottedName()
                            + "' has not been opened for obfuscation.";
                    scriptEnvironment1.logWarning(string6);
                }
            }
        }
    }

    @Override
    public final void writeRemappedTo(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(this.getTag().getTagValue());
        ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.classConstant);
        if (constantPoolEntry != null) {
            dataOutputStream.writeShort(constantPoolEntry.getIndex());
        } else {
            dataOutputStream.writeShort(this.classConstant.getIndex());
        }

        ResolvedNameAndType resolvedNameAndType = (ResolvedNameAndType) map1.get(this.nameAndType);
        if (resolvedNameAndType != null) {
            dataOutputStream.writeShort(resolvedNameAndType.getIndex());
        } else {
            dataOutputStream.writeShort(this.nameAndType.getIndex());
        }
    }

    public final boolean matches(String string, String string1, String string2) {
        return this.classConstant.getClassName().equals(string) && this.getMemberName().equals(string1) && this.getDescriptor().equals(string2);
    }

    public ResolvedClassConstant getClassConstant() {
        return this.classConstant;
    }

    public final MemberInfo getResolvedMember() {
        return this.resolvedMember;
    }

    public final String getMemberName() {
        return this.nameAndType.getName();
    }

    public ResolvedMemberRef(
            AbstractConstantPool abstractConstantPool, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, MemberInfo memberInfo1
    ) {
        super(0, abstractConstantPool);
        this.classConstant = resolvedClassConstant;
        this.nameAndType = resolvedNameAndType;
        this.resolvedMember = memberInfo1;
    }

    @Override
    public final void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.getTag().getTagValue());
        dataOutputStream.writeShort(this.classConstant.getIndex());
        dataOutputStream.writeShort(this.nameAndType.getIndex());
    }

    public final String getReferencedClassName() {
        return this.classConstant.getClassName();
    }

    public String getMemberRefKey() {
        return this.classConstant.getClassName() + '~' + this.nameAndType.getConstantKey();
    }

    public ResolvedMemberRef(
            AbstractConstantPool abstractConstantPool,
            ResolvedClassConstant resolvedClassConstant,
            ResolvedNameAndType resolvedNameAndType,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        super(0, abstractConstantPool);
        this.classConstant = resolvedClassConstant;
        this.nameAndType = resolvedNameAndType;
        if (bl) {
            this.resolveMember(classMemberLookup1, classResolver1, (IgnoreMissingReferencesSpec) null);
        }
    }

    public final ResolvedNameAndType getNameAndType() {
        return this.nameAndType;
    }

    @Override
    public final String getDisplayString() {
        return this.classConstant.getValueString() + "." + this.nameAndType.getDisplayString();
    }

    public ResolvedMemberRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    ) {
        super(constantMemberRef.index, constantMemberRef.constantPool);
        this.classConstant = resolvedClassConstant;
        this.nameAndType = resolvedNameAndType;
        listMultimap.addValue(resolvedClassConstant, this);
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    @Override
    public ResolvedNameAndType replaceNameAndType(ResolvedNameAndType resolvedNameAndType) {
        ResolvedNameAndType resolvedNameAndType1 = this.nameAndType;
        this.nameAndType = resolvedNameAndType;
        return resolvedNameAndType1;
    }

    public final String getDescriptor() {
        return this.nameAndType.getDescriptor();
    }

    @Override
    public final String getValueString() {
        return this.classConstant.getValueString() + " " + this.nameAndType.getValueString();
    }

    public abstract void resolveMember(
            ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException;

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
