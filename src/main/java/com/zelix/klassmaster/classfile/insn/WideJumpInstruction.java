package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class WideJumpInstruction extends BranchInstruction {
    @Override
    public List expandWideJump() {
        return null;
    }

    public WideJumpInstruction(int ba, LabelInstruction labelInstruction) {
        super(ba, labelInstruction);
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getLocals();
        VerifierType[] verifierTypes1 = stackFrameState.getStack();
        int ba = verifierTypes1.length;
        Set set1 = stackFrameState.getHeldMonitors();
        switch (this.opcode) {
            case 200:
                return new StackFrameState(verifierTypes1, verifierTypes, stackFrameState.getSubroutineLocals(), set1);
            case 201:
                VerifierType[] verifierTypes2 = VerifierType.createArray(ba + 1);
                System.arraycopy(verifierTypes1, 0, verifierTypes2, 0, ba);
                verifierTypes2[ba] = VerifierType.RETURN_ADDRESS;
                return new StackFrameState(verifierTypes2, verifierTypes, stackFrameState.getSubroutineLocals(), set1);
            default:
                Integer integer = this.opcode;
                ZkmAssert.assertTrueWithCode(integer);
                return null;
        }
    }

    @Override
    public boolean isJsr() {
        return this.getOpcode() == 201;
    }

    public WideJumpInstruction(int ba, ClassFileInputStream classFileInputStream, int bb, ListMultimap listMultimap) throws IOException {
        super(ba, classFileInputStream, bb, listMultimap);
    }

    @Override
    public int getLength() {
        return 5;
    }

    @Override
    public int readTargetOffset(ClassFileInputStream classFileInputStream) throws IOException {
        int ba = classFileInputStream.readInt();
        return this.bytecodeOffset + ba;
    }

    @Override
    public void writeRelativeOffset(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.getRelativeOffset());
    }

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
