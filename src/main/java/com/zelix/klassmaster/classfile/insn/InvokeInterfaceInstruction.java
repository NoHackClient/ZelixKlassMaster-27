package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolProvider;
import com.zelix.klassmaster.classfile.constpool.ResolvedInterfaceMethodRef;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class InvokeInterfaceInstruction extends ConstantRefInstruction {
    public int argSlotCount;

    @Override
    public String toAssembly() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMnemonic());
        stringBuilder.append(' ');
        stringBuilder.append(this.constantEntry.getValueString());
        return stringBuilder.toString();
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        dataOutputStream.writeByte(this.argSlotCount);
        dataOutputStream.writeByte(0);
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        super.writeRemapped(dataOutputStream, map1);
        dataOutputStream.writeByte(this.argSlotCount);
        dataOutputStream.writeByte(0);
    }

    public void setArgSlotCount(int argSlotCount) {
        this.argSlotCount = argSlotCount;
    }

    public InvokeInterfaceInstruction(ResolvedInterfaceMethodRef resolvedInterfaceMethodRef) {
        super(185, resolvedInterfaceMethodRef);
        this.argSlotCount = resolvedInterfaceMethodRef.getArgumentSlotCount() + 1;
    }

    public InvokeInterfaceInstruction(
            ClassFileInputStream classFileInputStream,
            ConstantPoolProvider constantPoolProvider,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4
    ) throws IOException {
        super(185, classFileInputStream, constantPoolProvider, listMultimap, listMultimap1, listMultimap2, listMultimap3, listMultimap4);
        this.argSlotCount = classFileInputStream.read();
        classFileInputStream.skip(1L);
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        String string = this.getMnemonic();
        stringBuilder1.append(string + " " + this.constantEntry.getIndex() + " " + this.argSlotCount + " " + 0);
        String string1 = Instruction.getDescriptionComment(this.opcode);
        string1 = Instruction.formatDescription(string1, this.constantEntry.getValueString());
        string1 = Instruction.formatDescription(string1, String.valueOf(this.argSlotCount));
        if (string1.length() > 0) {
            stringBuilder1.append("\t" + string1);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    @Override
    public int getLength() {
        return 5;
    }
}
