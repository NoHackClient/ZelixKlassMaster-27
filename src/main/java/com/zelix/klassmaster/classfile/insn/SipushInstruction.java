package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class SipushInstruction extends Instruction {
    public int value;

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    @Override
    public boolean isIntConstantPush() {
        return true;
    }

    @Override
    public boolean pushesWithoutPopping() {
        return true;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        stringBuilder1.append(this.toAssembly());
        String string = this.getDescriptionComment();
        string = Instruction.formatDescription(string, String.valueOf(this.value));
        if (string.length() > 0) {
            stringBuilder1.append("\t" + string);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object1, Object object2) {
        return false;
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public String toAssembly() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMnemonic());
        stringBuilder.append(' ');
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }

    public SipushInstruction(int value) {
        super(17);
        this.value = value;
    }

    @Override
    public final boolean isExit() {
        return false;
    }

    @Override
    public boolean pushesValue() {
        return true;
    }

    public SipushInstruction(ClassFileInputStream classFileInputStream) throws IOException {
        super(17);
        this.value = classFileInputStream.readShort();
    }

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        return false;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        dataOutputStream.writeShort(this.value);
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getLocals();
        VerifierType[] verifierTypes1 = stackFrameState.getStack();
        int ba = verifierTypes1.length;
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba + 1);
        System.arraycopy(verifierTypes1, 0, verifierTypes2, 0, ba);
        verifierTypes2[ba] = VerifierType.INT;
        return new StackFrameState(verifierTypes2, verifierTypes, stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors());
    }

    @Override
    public int getStackDelta() {
        return 1;
    }

    @Override
    public final boolean canFallThrough() {
        return true;
    }
}
