package com.zelix.klassmaster.classfile.insn;

import java.util.ArrayList;
import java.util.List;

public class LabeledCodeInsertion extends CodeInsertion {
    public boolean existingBranchTarget;
    public LabelInstruction targetLabel;

    public LabeledCodeInsertion(boolean existingBranchTarget, LabelInstruction labelInstruction, int ba, int bb) {
        super((Instruction) null, ba, bb, 0);
        this.existingBranchTarget = existingBranchTarget;
        this.targetLabel = labelInstruction;
    }

    public void appendInstructions(List list1) {
        int ba = list1.size();

        for (int i = 0; i < ba; i++) {
            this.instructions.add(list1.get(i));
        }
    }

    public void prependInstructions(List list1) {
        if (this.instructions.size() == 0) {
            for (int i = 0; i < list1.size(); i++) {
                this.instructions.add(list1.get(i));
            }
        } else {
            ArrayList arrayList = new ArrayList(this.instructions.size() + list1.size());

            for (int i = 0; i < list1.size(); i++) {
                arrayList.add(list1.get(i));
            }

            for (int i = 0; i < this.instructions.size(); i++) {
                arrayList.add(this.instructions.get(i));
            }

            this.instructions = arrayList;
        }
    }

    public boolean isExistingBranchTarget() {
        return this.existingBranchTarget;
    }

    public LabelInstruction getTargetLabel() {
        return this.targetLabel;
    }
}
