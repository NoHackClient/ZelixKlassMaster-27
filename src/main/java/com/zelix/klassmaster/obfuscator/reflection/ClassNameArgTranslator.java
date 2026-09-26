package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;

import java.util.ArrayList;
import java.util.List;

public class ClassNameArgTranslator implements ReflectionLookupTemplate {
    private static String lookupMethodDescriptor;
    private static long invokeOpcodeKey;

    @Override
    public int getExtraStackSize() {
        return 0;
    }

    @Override
    public List createLookupInstructions(ResolvedMethodRefConstant resolvedMethodRefConstant, Object object) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ConstantRefInstruction((int) invokeOpcodeKey, resolvedMethodRefConstant));
        return arrayList;
    }

    @Override
    public String[] getHelperMethodNames() {
        return null;
    }

    @Override
    public String getLookupMethodDescriptor() {
        return lookupMethodDescriptor;
    }

    @Override
    public String[] getHelperMethodDescriptors() {
        return null;
    }

    @Override
    public String[] getHelperOwnerClasses() {
        return null;
    }

    @Override
    public String getLookupMethodName() {
        return "a";
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        lookupMethodDescriptor = "(Ljava/lang/String;)Ljava/lang/String;";
        invokeOpcodeKey = -1928321804871401288L;
    }
}
