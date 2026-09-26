package com.zelix.klassmaster.obfuscator.references;

import com.zelix.klassmaster.classfile.insn.LocalVariableList;

import java.util.ArrayList;

public class InstructionListBuffer {
    public final ArrayList instructions;
    public final LocalVariableList localVariables;

    public LocalVariableList getLocalVariables() {
        return this.localVariables;
    }

    public ArrayList getInstructions() {
        return this.instructions;
    }

    public InstructionListBuffer(int ba, String string) {
        this.instructions = new ArrayList(ba);
        this.localVariables = new LocalVariableList(true, string, 5);
    }
}
