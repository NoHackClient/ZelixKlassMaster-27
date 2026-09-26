package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;

import java.util.List;
import java.util.Map;

public abstract class MemberSignatureBase {
    public final String name;
    public final String parameterDescriptor;

    public final String getNameWithParameters(Map map1) {
        return this.name + this.formatParameters(map1);
    }

    public boolean isConstructor() {
        return this.name.equals("<init>");
    }

    public MemberSignatureBase(String string, String string1) {
        this.name = string.intern();
        this.parameterDescriptor = MethodSignature.getParameterPart(string1).intern();
    }

    public MemberSignatureBase(MemberSignatureBase memberSignatureBase1) {
        this.name = memberSignatureBase1.getName();
        this.parameterDescriptor = memberSignatureBase1.getParameterDescriptor();
    }

    public boolean isStaticInitializer() {
        return this.name.equals("<clinit>");
    }

    public final String getName() {
        return this.name;
    }

    public final String formatParameters(Map map1) {
        return MethodSignature.formatParameterTypes(this.parameterDescriptor, map1);
    }

    public final String formatSignature() {
        List list1 = ConstantPoolEntry.getParameterJavaTypes(this.parameterDescriptor);
        StringBuilder stringBuilder = new StringBuilder();
        int ba = list1.size();

        for (int i = 0; i < ba; i++) {
            stringBuilder.append((String) list1.get(i));
            if (i < ba - 1) {
                stringBuilder.append(", ");
            }
        }

        return this.name + "(" + stringBuilder.toString() + ")";
    }

    public abstract String getReturnDescriptor();

    public final String getParameterDescriptor() {
        return this.parameterDescriptor;
    }
}
