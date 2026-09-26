package com.zelix.klassmaster.classfile.insn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeInsertion implements Comparable {
    public int sequence = 0;
    public List instructions;
    public int position;
    public int replacedCount;
    public int extraStackSize;

    public boolean insertBeforeOpcode(List list1) {
        boolean bl = false;
        ArrayList arrayList = new ArrayList(this.instructions.size() + list1.size());
        Iterator iterator = this.instructions.iterator();

        while (iterator.hasNext()) {
            Instruction instruction1 = (Instruction) iterator.next();
            if (instruction1.getOpcode() == 191) {
                arrayList.addAll(list1);
                bl = true;
            }

            arrayList.add(instruction1);
        }

        if (bl) {
            this.instructions = arrayList;
        }

        return bl;
    }

    public CodeInsertion(List list1, int position, int replacedCount, int extraStackSize) {
        if (list1 != null) {
            this.instructions = list1;
        }

        this.position = position;
        this.replacedCount = replacedCount;
        this.extraStackSize = extraStackSize;
    }

    public final int compareByPosition(CodeInsertion codeInsertion1) {
        if (this.position < codeInsertion1.position) {
            return -1;
        }

        if (this.position == codeInsertion1.position) {
            if (this.sequence < codeInsertion1.sequence) {
                return -1;
            } else {
                return this.sequence == codeInsertion1.sequence ? 0 : 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByPosition((CodeInsertion) object);
    }

    public int getPosition() {
        return this.position;
    }

    public final void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setExtraStackSize() {
        this.extraStackSize = 1;
    }

    public List getInstructions() {
        return this.instructions;
    }

    public int getExtraStackSize() {
        return this.extraStackSize;
    }

    public CodeInsertion(Instruction instruction1, int position, int replacedCount, int extraStackSize) {
        this.instructions = new ArrayList();
        if (instruction1 != null) {
            this.instructions.add(instruction1);
        }

        this.position = position;
        this.replacedCount = replacedCount;
        this.extraStackSize = extraStackSize;
    }

    public int getReplacedCount() {
        return this.replacedCount;
    }
}
