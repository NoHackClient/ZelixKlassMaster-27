package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;

import java.util.Iterator;
import java.util.List;

public class ReflectionCallSite {
    public final MethodBytecode methodBytecode;
    private final ReflectionApiMethod apiMethod;
    public final ConstantRefInstruction callInstruction;
    public final String className;
    private final String methodName;
    public List targetClassNames;
    public String memberName;

    public ConstantRefInstruction getCallInstruction() {
        return this.callInstruction;
    }

    public ReflectionApiMethod getApiMethod() {
        return this.apiMethod;
    }

    public MethodBytecode getMethodBytecode() {
        return this.methodBytecode;
    }

    public boolean hasResolvedTargetClasses() {
        if (this.targetClassNames == null) {
            return false;
        }

        Iterator iterator = this.targetClassNames.iterator();

        while (iterator.hasNext()) {
            if ((String) iterator.next() == null) {
                return false;
            }
        }

        return true;
    }

    public String getClassName() {
        return this.className;
    }

    public String getMemberName() {
        return this.memberName;
    }

    public List getTargetClassNames() {
        return this.targetClassNames;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public ReflectionCallSite(
            MethodBytecode methodBytecode1,
            ReflectionApiMethod reflectionApiMethod,
            ConstantRefInstruction constantRefInstruction,
            String string,
            String string1,
            List list1,
            String string2
    ) {
        this.methodBytecode = methodBytecode1;
        this.apiMethod = reflectionApiMethod;
        this.callInstruction = constantRefInstruction;
        this.className = string;
        this.methodName = string1;
        if (list1 != null) {
            for (int i = 0; i < list1.size(); i++) {
                String string3 = (String) list1.get(i);
                if (string3 != null) {
                    list1.set(i, string3.replace('.', '/'));
                }
            }

            this.targetClassNames = list1;
        } else {
            this.targetClassNames = list1;
        }

        this.memberName = string2;
    }
}
