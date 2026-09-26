package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TableSwitchInstruction extends SwitchInstruction {
    public int caseCount = -1;
    public final int lowValue;
    public final int highValue;

    public TableSwitchInstruction(ClassFileInputStream classFileInputStream, int ba, ListMultimap listMultimap) throws IOException {
        super(170, classFileInputStream, ba);
        int bb = classFileInputStream.readInt();
        this.lowValue = classFileInputStream.readInt();
        this.highValue = classFileInputStream.readInt();
        this.caseCount = this.highValue - this.lowValue + 1;
        this.targetLabels = new LabelInstruction[this.caseCount + 1];
        this.targetOffsets = new ArrayList(this.targetLabels.length);
        int bc = 0;
        int be = 0;

        for (int i = this.caseCount; be < i; i = this.caseCount) {
            int bd = classFileInputStream.readInt();
            Integer integer = Instruction.integerCache.valueOf(super.switchOffset + bd);
            listMultimap.addValue(integer, this);
            this.targetOffsets.add(integer);
            be = ++bc;
        }

        Integer integer1 = Instruction.integerCache.valueOf(super.switchOffset + bb);
        listMultimap.addValue(integer1, this);
        this.targetOffsets.add(integer1);
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        dataOutputStream.writeInt(this.lowValue);
        dataOutputStream.writeInt(this.highValue);
        int ba = 0;
        int bb = 0;

        for (int i = this.caseCount; bb < i; i = this.caseCount) {
            LabelInstruction labelInstruction = this.targetLabels[ba];
            dataOutputStream.writeInt(labelInstruction.getOffset() - super.switchOffset);
            bb = ++ba;
        }
    }

    public TableSwitchInstruction(LabelInstruction labelInstruction, int highValue, LabelInstruction[] labelInstructions) {
        this.lowValue = 0;
        this.highValue = highValue;
        this.caseCount = this.highValue - this.lowValue + 1;
        this.targetLabels = new LabelInstruction[this.caseCount + 1];
        int bb = 0;
        int bc = 0;

        for (int i = this.caseCount; bc < i; i = this.caseCount) {
            this.targetLabels[bb] = labelInstructions[bb];
            bc = ++bb;
        }

        this.targetLabels[this.targetLabels.length - 1] = labelInstruction;
    }

    @Override
    public int getLength() {
        return 1 + super.paddingSize + 12 + this.caseCount * 4;
    }

    @Override
    public String toAssembly() {
        return this.getMnemonic();
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(this.getMnemonic());
        printWriter.println(stringBuilder.toString() + stringBuilder1.toString());
        Object object = this.targetLabels[this.targetLabels.length - 1];
        printWriter.println(stringBuilder.toString() + "\tdefault: goto " + (object != null ? ((LabelInstruction) object).getLabelName() : object));
        int ba = 0;
        int bc = 0;

        for (int i = this.caseCount; bc < i; i = this.caseCount) {
            Object object1 = this.targetLabels[ba];
            int bb = this.lowValue + ba;
            printWriter.println(stringBuilder.toString() + "\tcase " + bb + ": goto " + (object1 != null ? ((LabelInstruction) object1).getLabelName() : object1));
            bc = ++ba;
        }
    }
}
