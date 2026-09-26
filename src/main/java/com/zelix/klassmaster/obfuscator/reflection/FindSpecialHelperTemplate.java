package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;

import java.util.ArrayList;
import java.util.List;

public class FindSpecialHelperTemplate implements ReflectionLookupTemplate {
    public static final String[] HELPER_OWNER_CLASSES = new String[]{"java/lang/invoke/MethodType"};
    public static final String[] HELPER_METHOD_NAMES = new String[]{"parameterArray"};
    public static final String[] HELPER_METHOD_DESCRIPTORS = new String[]{"()[Ljava/lang/Class;"};

    @Override
    public String[] getHelperOwnerClasses() {
        return HELPER_OWNER_CLASSES.clone();
    }

    @Override
    public String getLookupMethodName() {
        return "b";
    }

    @Override
    public int getExtraStackSize() {
        return 2;
    }

    @Override
    public String getLookupMethodDescriptor() {
        return "(Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/String;";
    }

    @Override
    public String[] getHelperMethodNames() {
        return HELPER_METHOD_NAMES.clone();
    }

    @Override
    public String[] getHelperMethodDescriptors() {
        return HELPER_METHOD_DESCRIPTORS.clone();
    }

    @Override
    public List createLookupInstructions(ResolvedMethodRefConstant resolvedMethodRefConstant, Object object) {
        ResolvedMethodRefConstant[] resolvedMethodRefConstants = (ResolvedMethodRefConstant[]) object;
        ArrayList arrayList = new ArrayList();
        arrayList.add(SimpleInstruction.forOpcode(93));
        arrayList.add(SimpleInstruction.forOpcode(95));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstants[0]));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(91));
        arrayList.add(SimpleInstruction.forOpcode(87));
        return arrayList;
    }
}
