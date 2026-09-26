package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.classfile.insn.LocalVariableList;

import java.util.ArrayList;

public class GeneratedMethodChunk {
    public final ArrayList instructions;
    public final LocalVariableList localVariables;

    public GeneratedMethodChunk(int ba) {
        this.instructions = new ArrayList(ba);
        this.localVariables = new LocalVariableList(false, "()V", 5);
    }

    public LocalVariableList getLocalVariables() {
        return this.localVariables;
    }

    public ArrayList getInstructions() {
        return this.instructions;
    }
}
