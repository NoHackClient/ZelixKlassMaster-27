package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;

import java.util.ArrayList;
import java.util.List;

public class LoadClassReflectionHelper implements ReflectionLookupTemplate {
    private static final String LOOKUP_METHOD_DESCRIPTOR = "(Ljava/lang/String;)Ljava/lang/String;";

    @Override
    public String getLookupMethodDescriptor() {
        return LOOKUP_METHOD_DESCRIPTOR;
    }

    @Override
    public String[] getHelperOwnerClasses() {
        return null;
    }

    @Override
    public String[] getHelperMethodNames() {
        return null;
    }

    @Override
    public String[] getHelperMethodDescriptors() {
        return null;
    }

    @Override
    public int getExtraStackSize() {
        return 0;
    }

    @Override
    public String getLookupMethodName() {
        return "a";
    }

    @Override
    public List createLookupInstructions(ResolvedMethodRefConstant resolvedMethodRefConstant, Object object) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(SimpleInstruction.forOpcode(95));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(95));
        return arrayList;
    }
}
