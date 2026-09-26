package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsrInstruction extends BranchInstruction {
    @Override
    public boolean isJsr() {
        return true;
    }

    public JsrInstruction(LabelInstruction labelInstruction) {
        super(168, labelInstruction);
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getStack();
        VerifierType[] verifierTypes1 = stackFrameState.getLocals();
        int ba = verifierTypes.length;
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba + 1);
        System.arraycopy(verifierTypes, 0, verifierTypes2, 0, ba);
        verifierTypes2[ba] = VerifierType.RETURN_ADDRESS;
        return new StackFrameState(verifierTypes2, verifierTypes1, stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors());
    }

    public JsrInstruction(ClassFileInputStream classFileInputStream, int ba, ListMultimap listMultimap) throws IOException {
        super(168, classFileInputStream, ba, listMultimap);
    }

    @Override
    public List expandWideJump() {
        int relativeOffset = this.getRelativeOffset();
        if (relativeOffset >= -32768 && relativeOffset <= 32767) {
            return null;
        }

        ArrayList arrayList = new ArrayList(1);
        arrayList.add(new WideJumpInstruction(201, this.targetLabel));
        return arrayList;
    }
}
