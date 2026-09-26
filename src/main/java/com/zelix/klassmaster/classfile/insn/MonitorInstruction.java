package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class MonitorInstruction extends Instruction {
    public VerifierType lockedObjectType;

    public VerifierType getLockedObjectType() {
        return this.lockedObjectType;
    }

    @Override
    public final void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        stringBuilder1.append(stringBuilder);
        stringBuilder1.append(stringBuilder);
        stringBuilder1.append(this.getMnemonic());
        String string = Instruction.getDescriptionComment(this.opcode);
        if (string.length() > 0) {
            stringBuilder1.append("\t" + string);
        }

        printWriter.println(stringBuilder1.toString());
    }

    @Override
    public boolean canFallThrough() {
        return true;
    }

    @Override
    public boolean consumesStackSlot(int ba, int bb) {
        return ba >= bb;
    }

    @Override
    public int getStackDelta() {
        return -1;
    }

    @Override
    public boolean isExit() {
        return false;
    }

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    @Override
    public boolean requiresTypedValueAt(Object object, Object object1, Object object2) {
        return false;
    }

    @Override
    public boolean isMonitor() {
        return true;
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getLocals();
        VerifierType[] verifierTypes1 = stackFrameState.getStack();
        int ba = verifierTypes1.length;
        SubroutineLocalsBitSet subroutineLocalsBitSet = stackFrameState.getSubroutineLocals();
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba - 1);
        System.arraycopy(verifierTypes1, 0, verifierTypes2, 0, ba - 1);
        this.lockedObjectType = verifierTypes1[verifierTypes1.length - 1];
        StackFrameState stackFrameState1 = new StackFrameState(verifierTypes2, verifierTypes, subroutineLocalsBitSet, stackFrameState.getHeldMonitors());
        this.updateHeldMonitors(stackFrameState1);
        return stackFrameState1;
    }

    @Override
    public String toAssembly() {
        return this.getMnemonic();
    }

    @Override
    public boolean pushesValue() {
        return false;
    }

    public MonitorInstruction(int ba) {
        super(ba);
    }

    @Override
    public boolean pushesWithoutPopping() {
        return false;
    }

    public abstract void updateHeldMonitors(StackFrameState stackFrameState);

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
