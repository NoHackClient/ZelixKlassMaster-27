package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GotoInstruction extends BranchInstruction {
    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        return new StackFrameState(
                stackFrameState.getStack(), stackFrameState.getLocals(), stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors()
        );
    }

    public GotoInstruction(ClassFileInputStream classFileInputStream, int ba, ListMultimap listMultimap) throws IOException {
        super(167, classFileInputStream, ba, listMultimap);
    }

    public GotoInstruction(LabelInstruction labelInstruction) {
        super(167, labelInstruction);
    }

    @Override
    public List expandWideJump() {
        int relativeOffset = this.getRelativeOffset();
        if (relativeOffset >= -32768 && relativeOffset <= 32767) {
            return null;
        }

        ArrayList arrayList = new ArrayList(1);
        arrayList.add(new WideJumpInstruction(200, this.targetLabel));
        return arrayList;
    }
}
