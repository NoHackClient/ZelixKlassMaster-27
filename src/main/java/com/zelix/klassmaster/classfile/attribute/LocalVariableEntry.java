package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.classfile.insn.InstructionUsageFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LabelTargetHolder;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocalVariableEntry extends ClassFileComponent implements Utf8ConstantReplaceable, LabelTargetHolder, Comparable {
    private static final String ATTRIBUTE_NAME = "LocalVariableTable";
    private ConstantUtf8 nameConstant;
    private ConstantUtf8 descriptorConstant;
    public byte[] rawBytes;
    private LabelInstruction startLabel;
    private LabelInstruction endLabel;
    private boolean valid = true;
    private int startPc;
    private int endPc;
    public final LocalVariableIndex localIndex;

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeShort(this.startLabel.getOffset());
        dataOutputStream.writeShort(this.endLabel.getOffset() - this.startLabel.getOffset());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.getNameConstant());
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.getNameConstant().getIndex());
        }

        ConstantUtf8 constantUtf81 = (ConstantUtf8) map1.get(this.getDescriptorConstant());
        if (constantUtf81 != null) {
            dataOutputStream.writeShort(constantUtf81.getIndex());
        } else {
            dataOutputStream.writeShort(this.getDescriptorConstant().getIndex());
        }

        dataOutputStream.writeShort(this.localIndex.getIndex());
    }

    public void setNameConstant(ConstantUtf8 constantUtf8) {
        this.nameConstant = constantUtf8;
    }

    public ConstantUtf8 getDescriptorConstant() {
        return this.descriptorConstant;
    }

    @Override
    public void registerLabelTargets(SetMultiMap setMultiMap) {
        setMultiMap.addValue(this.startLabel, this);
        setMultiMap.addValue(this.endLabel, this);
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.getNameConstant() == constantUtf8) {
            this.setNameConstant(constantUtf81);
        } else if (this.getDescriptorConstant() == constantUtf8) {
            this.setDescriptorConstant(constantUtf81);
        }
    }

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_LOCAL_VARIABLE;
    }

    public int compareByLocalIndex(LocalVariableEntry localVariableEntry1) {
        if (this.localIndex.getIndex() < localVariableEntry1.localIndex.getIndex()) {
            return -1;
        } else {
            return this.localIndex.getIndex() == localVariableEntry1.localIndex.getIndex() ? 0 : 1;
        }
    }

    public LabelInstruction getStartLabel() {
        return this.startLabel;
    }

    public LocalVariableEntry(
            ClassFileComponent classFileComponent,
            int startPc,
            int endPc,
            ConstantUtf8 constantUtf8,
            ConstantUtf8 constantUtf81,
            LocalVariableIndex localVariableIndex1,
            ListMultimap listMultimap
    ) {
        super(classFileComponent);
        this.valid = true;
        this.startPc = startPc;
        listMultimap.addValue(integerCache.valueOf(startPc), this);
        this.endPc = endPc;
        listMultimap.addValue(integerCache.valueOf(endPc), this);
        this.localIndex = localVariableIndex1;
        this.setNameConstant(constantUtf8);
        this.setDescriptorConstant(constantUtf81);
    }

    @Override
    public void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        if (this.startLabel == null && integer == this.startPc) {
            this.startLabel = labelInstruction;
        } else if (integer == this.endPc) {
            this.endLabel = labelInstruction;
        }
    }

    public LabelInstruction getEndLabel() {
        return this.endLabel;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        if (this.valid) {
            dataOutputStream.writeShort(this.startLabel.getOffset());
            dataOutputStream.writeShort(this.endLabel.getOffset() - this.startLabel.getOffset());
            dataOutputStream.writeShort(this.getNameConstant().getIndex());
            dataOutputStream.writeShort(this.getDescriptorConstant().getIndex());
            dataOutputStream.writeShort(this.localIndex.getIndex());
        } else {
            dataOutputStream.write(this.rawBytes);
        }
    }

    public ConstantUtf8 getNameConstant() {
        return this.nameConstant;
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return ATTRIBUTE_NAME;
    }

    public void buildRawBytes(int ba, int bb, int bc) {
        this.rawBytes = new byte[10];
        this.rawBytes[0] = (byte) (this.startPc >>> 8 & 0xFF);
        this.rawBytes[1] = (byte) (this.startPc >>> 0 & 0xFF);
        this.rawBytes[2] = (byte) (this.endPc >>> 8 & 0xFF);
        this.rawBytes[3] = (byte) (this.endPc >>> 0 & 0xFF);
        this.rawBytes[4] = (byte) (ba >>> 8 & 0xFF);
        this.rawBytes[5] = (byte) (ba >>> 0 & 0xFF);
        this.rawBytes[6] = (byte) (bb >>> 8 & 0xFF);
        this.rawBytes[7] = (byte) (bb >>> 0 & 0xFF);
        this.rawBytes[8] = (byte) (bc >>> 8 & 0xFF);
        this.rawBytes[9] = (byte) (bc >>> 0 & 0xFF);
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByLocalIndex((LocalVariableEntry) object);
    }

    public void remapDescriptorClassNames(Object object, Object object1, HashMap hashMap) throws ZkmProcessingException {
        MutableInt mutableInt = new MutableInt(0);
        String string = ClassFileBase.extractClassName(this.getDescriptorConstant().getValue(), mutableInt);
        if (string != null) {
            String string1 = (String) ZkmUtils.mapOrSelf(string, hashMap);
            if (!string1.equals(string)) {
                String string2 = ClassFileBase.toTypeDescriptor(string1, mutableInt.getValue());
                this.getDescriptorConstant().setValue(string2);
            }
        }
    }

    public String getVariableName() {
        return this.valid ? this.getNameConstant().getValue() : "";
    }

    public void setDescriptorConstant(ConstantUtf8 constantUtf8) {
        this.descriptorConstant = constantUtf8;
    }

    public LocalVariableEntry(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            LocalVariableList localVariableList1,
            ListMultimap listMultimap,
            ListMultimap listMultimap1
    ) throws IOException {
        super(classFileComponent);
        this.startPc = classFileInputStream.readUnsignedShort();
        int ba = classFileInputStream.readUnsignedShort();
        this.endPc = this.startPc + ba;
        listMultimap1.addValue(integerCache.valueOf(this.startPc), this);
        listMultimap1.addValue(integerCache.valueOf(this.endPc), this);
        int bb = classFileInputStream.readUnsignedShort();
        int bc = classFileInputStream.readUnsignedShort();
        int bd = classFileInputStream.readUnsignedShort();
        this.localIndex = localVariableList1.getSlotAt(bd);
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        ConstantPoolEntry constantPoolEntry1 = this.getConstantPoolEntry(bc);
        if (constantPoolEntry instanceof ConstantUtf8 && constantPoolEntry1 instanceof ConstantUtf8 && this.localIndex != null) {
            this.setNameConstant((ConstantUtf8) constantPoolEntry);
            this.setDescriptorConstant((ConstantUtf8) constantPoolEntry1);
            listMultimap.addValue(this.getNameConstant(), this);
            listMultimap.addValue(this.getDescriptorConstant(), this);
        } else {
            this.valid = false;
            this.buildRawBytes(bb, bc, bd);
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        if (this.valid) {
            this.nameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
            this.descriptorConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        }
    }

    public int getLocalIndex() {
        return this.localIndex.getIndex();
    }

    public void obfuscateName() {
        if (HiddenOptionFlags.MARK_RENAMED_LOCALS) {
            String string = this.getNameConstant().getValue() + '_' + "a";
            this.getNameConstant().setValue(string);
        } else {
            this.getNameConstant().setValue("a");
        }
    }
}
