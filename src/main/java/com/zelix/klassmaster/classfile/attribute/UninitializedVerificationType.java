package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.insn.InstructionUsageFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LabelTargetHolder;
import com.zelix.klassmaster.classfile.insn.TypeInstruction;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class UninitializedVerificationType extends VerificationTypeInfo implements LabelTargetHolder {
    public LabelInstruction offsetLabel;
    public TypeInstruction newInstruction;
    private static final String INVALID_ENTRY_MESSAGE = "Invalid StackMap Entry in ";

    @Override
    public void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        this.offsetLabel = labelInstruction;
    }

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_STACK_MAP;
    }

    public UninitializedVerificationType(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        listMultimap.addValue(integerCache.valueOf(bb), this);
    }

    @Override
    public boolean isSameType(VerificationTypeInfo verificationTypeInfo) {
        if (this.tag == verificationTypeInfo.tag) {
            UninitializedVerificationType uninitializedVerificationType1 = (UninitializedVerificationType) verificationTypeInfo;
            return this.newInstruction == uninitializedVerificationType1.newInstruction;
        } else {
            return false;
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        if (!this.valid) {
            ZkmAssert.assertTrue(false, new String[]{INVALID_ENTRY_MESSAGE + this.getDottedClassName()});
        }

        dataOutputStream.writeByte(this.tag);
        if (this.offsetLabel != null) {
            dataOutputStream.writeShort(this.offsetLabel.getOffset());
        } else {
            dataOutputStream.writeShort(this.newInstruction.getBytecodeOffset());
        }
    }

    public UninitializedVerificationType(ClassFileComponent classFileComponent, TypeInstruction typeInstruction) {
        super(classFileComponent, 8);
        this.newInstruction = typeInstruction;
    }

    @Override
    public void registerLabelTargets(SetMultiMap setMultiMap) {
        if (this.offsetLabel != null) {
            setMultiMap.addValue(this.offsetLabel, this);
        }
    }
}
