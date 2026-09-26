package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.insn.BranchInstruction;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableProvider;
import com.zelix.klassmaster.classfile.insn.StackFrameState;
import com.zelix.klassmaster.config.HiddenOptionFlags;

import java.util.List;
import java.util.Random;

public class OpaquePredicateField implements Comparable {
    public String getterName;
    public String setterName;
    public AbstractMethodInfo negatedGetter;
    public AbstractMethodInfo getter;
    public AbstractMethodInfo setter;
    public String negatedGetterName;
    public final AbstractFieldInfo field;
    public final String fieldName;
    public final String fieldType;
    public final String ownerClassName;
    public int accessFlags;
    public final boolean usesAccessors;
    public final boolean nonZeroValue;

    public OpaquePredicateField(AbstractFieldInfo abstractFieldInfo) {
        this.field = abstractFieldInfo;
        this.fieldName = abstractFieldInfo.getSourceName();
        this.fieldType = abstractFieldInfo.getDescriptor();
        this.ownerClassName = abstractFieldInfo.getClassName();
        this.accessFlags = abstractFieldInfo.getVisibilityRank();
        this.usesAccessors = false;
        this.nonZeroValue = false;
    }

    public boolean isBoolean() {
        return this.fieldType.equals("Z");
    }

    public boolean isString() {
        return this.fieldType.equals("Ljava/lang/String;");
    }

    public boolean isObjectArray() {
        return this.fieldType.charAt(0) == '[' && this.fieldType.length() > 2;
    }

    public OpaquePredicateField(
            AbstractFieldInfo abstractFieldInfo,
            AbstractMethodInfo abstractMethodInfo,
            AbstractMethodInfo abstractMethodInfo1,
            AbstractMethodInfo abstractMethodInfo2,
            Random random1
    ) {
        this.field = abstractFieldInfo;
        this.fieldName = abstractFieldInfo.getSourceName();
        this.fieldType = abstractFieldInfo.getDescriptor();
        this.ownerClassName = abstractFieldInfo.getClassName();
        this.accessFlags = abstractFieldInfo.getVisibilityRank();
        this.setter = abstractMethodInfo;
        this.getter = abstractMethodInfo1;
        this.negatedGetter = abstractMethodInfo2;
        this.setterName = this.setter.getSourceName();
        this.getterName = this.getter.getSourceName();
        if (this.negatedGetter != null) {
            this.negatedGetterName = this.negatedGetter.getSourceName();
        }

        this.usesAccessors = true;
        this.nonZeroValue = random1.nextBoolean() || HiddenOptionFlags.ALWAYS_TRUE_OPAQUE_PREDICATES;
    }

    public String getGetterName() {
        return this.getterName;
    }

    public BranchInstruction createBranchIfSet(LabelInstruction labelInstruction) {
        return !this.fieldType.equals("I") && !this.fieldType.equals("Z")
                ? new BranchInstruction(this.nonZeroValue ? 199 : 198, labelInstruction)
                : new BranchInstruction(this.nonZeroValue ? 154 : 153, labelInstruction);
    }

    public boolean isReferenceType() {
        return this.fieldType.charAt(0) == '[' || this.fieldType.charAt(0) == 'L';
    }

    public AbstractMethodInfo getGetter() {
        return this.getter;
    }

    public AbstractMethodInfo getNegatedGetter() {
        return this.negatedGetter;
    }

    public Instruction createStoreLocal(int ba, LocalVariableProvider localVariableProvider) {
        return !this.fieldType.equals("I") && !this.fieldType.equals("Z")
                ? Instruction.createObjectStore(ba, localVariableProvider, 2)
                : Instruction.createIntStore(ba, localVariableProvider, 2);
    }

    public OpaquePredicateField(
            AbstractFieldInfo abstractFieldInfo,
            AbstractMethodInfo abstractMethodInfo,
            AbstractMethodInfo abstractMethodInfo1,
            AbstractMethodInfo abstractMethodInfo2,
            Boolean boolean1
    ) {
        this.field = abstractFieldInfo;
        this.fieldName = abstractFieldInfo.getSourceName();
        this.fieldType = abstractFieldInfo.getDescriptor();
        this.ownerClassName = abstractFieldInfo.getClassName();
        this.accessFlags = abstractFieldInfo.getVisibilityRank();
        this.setter = abstractMethodInfo;
        this.getter = abstractMethodInfo1;
        this.negatedGetter = abstractMethodInfo2;
        this.setterName = this.setter.getSourceName();
        this.getterName = this.getter.getSourceName();
        if (this.negatedGetter != null) {
            this.negatedGetterName = this.negatedGetter.getSourceName();
        }

        this.usesAccessors = true;
        this.nonZeroValue = boolean1;
    }

    public boolean hasSetter() {
        return this.setter != null;
    }

    public String getFieldDescriptor() {
        return this.field.getDescriptor();
    }

    public String getOwnerClassName() {
        return this.ownerClassName;
    }

    public int compareField(OpaquePredicateField opaquePredicateField1) {
        int ba = this.ownerClassName.compareTo(opaquePredicateField1.getOwnerClassName());
        return ba == 0 ? this.field.getSourceName().compareTo(opaquePredicateField1.getFieldName()) : ba;
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public boolean hasGetter() {
        return this.getter != null;
    }

    public String getNegatedGetterDescriptor() {
        return this.negatedGetter != null ? this.negatedGetter.getOriginalDescriptor() : null;
    }

    public BranchInstruction createBranchIfUnset(LabelInstruction labelInstruction) {
        return !this.fieldType.equals("I") && !this.fieldType.equals("Z")
                ? new BranchInstruction(this.nonZeroValue ? 198 : 199, labelInstruction)
                : new BranchInstruction(this.nonZeroValue ? 153 : 154, labelInstruction);
    }

    public String getSetterName() {
        return this.setterName;
    }

    public static String getterDescriptorFor(String string) {
        return "()" + string;
    }

    public ConstantRefInstruction createReadInstruction(ConstantPool constantPool1, List list1) {
        if (this.usesAccessors) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(this.getter, list1);
            return new ConstantRefInstruction(184, resolvedMethodRefConstant);
        } else {
            ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(this.field, list1);
            return new ConstantRefInstruction(178, resolvedFieldRef);
        }
    }

    public boolean hasNegatedGetter() {
        return this.negatedGetter != null;
    }

    public static String setterDescriptorFor(String string) {
        return '(' + string + ")V";
    }

    public String getNegatedGetterName() {
        return this.negatedGetterName;
    }

    public ConstantRefInstruction createWriteInstruction(ConstantPool constantPool1, List list1) {
        if (this.usesAccessors) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(this.setter, list1);
            return new ConstantRefInstruction(184, resolvedMethodRefConstant);
        } else {
            ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(
                    this.field.getClassName(), this.field.getSourceName(), this.field.getDescriptor(), list1, this.field
            );
            return new ConstantRefInstruction(179, resolvedFieldRef);
        }
    }

    public boolean isNonZeroValue() {
        return this.nonZeroValue;
    }

    @Override
    public int hashCode() {
        return this.ownerClassName.hashCode() ^ this.fieldName.hashCode();
    }

    public boolean isNegatedGetterPresent() {
        return this.negatedGetter != null;
    }

    public FieldSignature getFieldSignature() {
        return new FieldSignature(this.fieldName, this.fieldType);
    }

    public boolean isPrimitiveArray() {
        return this.fieldType.charAt(0) == '[' && this.fieldType.length() == 2;
    }

    public ConstantRefInstruction createNegatedGetterCall(ConstantPool constantPool1, List list1) {
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(this.negatedGetter, list1);
        return new ConstantRefInstruction(184, resolvedMethodRefConstant);
    }

    public ClassFileBase getOwnerClass() {
        return this.field.getOwningClass();
    }

    @Override
    public int compareTo(Object object) {
        return this.compareField((OpaquePredicateField) object);
    }

    public String getGetterDescriptor() {
        return this.getter != null ? this.getter.getOriginalDescriptor() : null;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public boolean isObjectType() {
        return this.fieldType.charAt(0) == 'L';
    }

    public AbstractMethodInfo getSetter() {
        return this.setter;
    }

    public String getSetterDescriptor() {
        return this.setter != null ? this.setter.getOriginalDescriptor() : null;
    }

    public Instruction createLoadLocal(int ba, LocalVariableProvider localVariableProvider) {
        return !this.fieldType.equals("I") && !this.fieldType.equals("Z")
                ? Instruction.createObjectLoad(ba, localVariableProvider, 2)
                : Instruction.createIntLoad(ba, localVariableProvider, 2);
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        boolean bl1 = object instanceof OpaquePredicateField;
        if (strictMergeEnabled) {
            if (bl1) {
                OpaquePredicateField opaquePredicateField1 = (OpaquePredicateField) object;
                String string = this.fieldName;
                if (strictMergeEnabled) {
                    if (!this.fieldName.equals(opaquePredicateField1.fieldName)) {
                        return false;
                    }

                    string = this.ownerClassName;
                }

                bl1 = string.equals(opaquePredicateField1.ownerClassName);
                if (!strictMergeEnabled) {
                    return bl1;
                }

                if (bl1) {
                    return true;
                }

                return false;
            }

            bl1 = false;
        }

        return bl1;
    }
}
