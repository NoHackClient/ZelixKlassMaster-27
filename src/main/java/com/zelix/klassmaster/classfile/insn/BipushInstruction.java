package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class BipushInstruction extends Instruction {
    public int byteValue;

    @Override
    public boolean pushesWithoutPopping() {
        return true;
    }

    @Override
    public final boolean isExit() {
        return false;
    }

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    @Override
    public String toAssembly() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMnemonic());
        stringBuilder.append(' ');
        stringBuilder.append(this.byteValue);
        return stringBuilder.toString();
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getStack();
        VerifierType[] verifierTypes1 = stackFrameState.getLocals();
        int ba = verifierTypes.length;
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba + 1);
        System.arraycopy(verifierTypes, 0, verifierTypes2, 0, ba);
        verifierTypes2[ba] = VerifierType.INT;
        return new StackFrameState(verifierTypes2, verifierTypes1, stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors());
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        dataOutputStream.writeByte(this.byteValue);
    }

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        return false;
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        stringBuilder1.append(this.toAssembly());
        String string = Instruction.getDescriptionComment(this.opcode);
        string = Instruction.formatDescription(string, String.valueOf(this.byteValue));
        if (string.length() > 0) {
            stringBuilder1.append("\t" + string);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public final boolean canFallThrough() {
        return true;
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object1, Object object2) {
        return false;
    }

    public int getByteValue() {
        return this.byteValue;
    }

    @Override
    public boolean isIntConstantPush() {
        return true;
    }

    public BipushInstruction(ClassFileInputStream classFileInputStream) throws IOException {
        super(16);
        this.byteValue = classFileInputStream.readByte();
    }

    @Override
    public boolean pushesValue() {
        return true;
    }

    @Override
    public int getStackDelta() {
        return 1;
    }

    public BipushInstruction(int byteValue) {
        super(16);
        this.byteValue = byteValue;
    }
}
