package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;

import java.util.ArrayList;

public class GeneratedCodeBlock {
    private static final String VOID_DESCRIPTOR = "()V";
    public int nextLocalIndex;
    public final ArrayList instructions = new ArrayList(30000);
    public ExceptionHandlerSpec[] exceptionHandlers = new ExceptionHandlerSpec[0];
    public final LocalVariableList localVariables = new LocalVariableList(true, VOID_DESCRIPTOR, 5);

    public int getLocalCount() {
        return this.nextLocalIndex;
    }

    public void setExceptionHandlers(ExceptionHandlerSpec[] exceptionHandlerSpecs) {
        this.exceptionHandlers = exceptionHandlerSpecs;
    }

    public LocalVariableList getLocalVariables() {
        return this.localVariables;
    }

    public int allocateLocal() {
        return this.nextLocalIndex++;
    }

    public ArrayList getInstructions() {
        return this.instructions;
    }

    public ExceptionHandlerSpec[] getExceptionHandlers() {
        return this.exceptionHandlers;
    }
}
