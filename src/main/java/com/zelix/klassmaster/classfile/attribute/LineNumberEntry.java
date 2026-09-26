package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.InstructionUsageFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LabelTargetHolder;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;

public class LineNumberEntry extends ClassFileComponent implements LabelTargetHolder, Comparable {
    private LabelInstruction label;
    private int lineNumber;
    private static final String ATTRIBUTE_NAME = "LineNumberTable";

    public LabelInstruction getLabel() {
        return this.label;
    }

    @Override
    public void registerLabelTargets(SetMultiMap setMultiMap) {
        setMultiMap.addValue(this.label, this);
    }

    public LineNumberEntry(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        listMultimap.addValue(integerCache.valueOf(ba), this);
        this.lineNumber = classFileInputStream.readUnsignedShort();
    }

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_LINE_NUMBER;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByOffset((LineNumberEntry) object);
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int compareByOffset(LineNumberEntry lineNumberEntry1) {
        if (this.label.getOffset() < lineNumberEntry1.label.getOffset()) {
            return -1;
        }

        if (this.label.getOffset() == lineNumberEntry1.label.getOffset()) {
            if (this.lineNumber < lineNumberEntry1.lineNumber) {
                return -1;
            } else {
                return this.lineNumber == lineNumberEntry1.lineNumber ? 0 : 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return ATTRIBUTE_NAME;
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.label.getOffset());
        dataOutputStream.writeShort(this.lineNumber);
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    @Override
    public void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        this.label = labelInstruction;
    }
}
