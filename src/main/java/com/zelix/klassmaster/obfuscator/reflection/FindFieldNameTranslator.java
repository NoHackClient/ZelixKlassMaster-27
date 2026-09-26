package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;

import java.util.ArrayList;
import java.util.List;

public class FindFieldNameTranslator implements ReflectionLookupTemplate {
    private static final String LOOKUP_METHOD_DESCRIPTOR = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;";

    @Override
    public int getExtraStackSize() {
        return 2;
    }

    @Override
    public String[] getHelperOwnerClasses() {
        return null;
    }

    @Override
    public String getLookupMethodDescriptor() {
        return LOOKUP_METHOD_DESCRIPTOR;
    }

    @Override
    public String getLookupMethodName() {
        return "c";
    }

    @Override
    public String[] getHelperMethodNames() {
        return null;
    }

    @Override
    public List createLookupInstructions(ResolvedMethodRefConstant resolvedMethodRefConstant, Object object) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(SimpleInstruction.forOpcode(91));
        arrayList.add(SimpleInstruction.forOpcode(87));
        arrayList.add(SimpleInstruction.forOpcode(95));
        arrayList.add(SimpleInstruction.forOpcode(91));
        arrayList.add(SimpleInstruction.forOpcode(95));
        arrayList.add(SimpleInstruction.forOpcode(90));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(91));
        arrayList.add(SimpleInstruction.forOpcode(88));
        return arrayList;
    }

    @Override
    public String[] getHelperMethodDescriptors() {
        return null;
    }
}
