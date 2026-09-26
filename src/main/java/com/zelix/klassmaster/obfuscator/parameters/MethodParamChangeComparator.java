package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.MethodInfo;

import java.util.Comparator;

public class MethodParamChangeComparator implements Comparator {
    public final ParameterKeyGenerator keyGenerator;

    public int compareNodes(MethodParamChangeNode methodParamChangeNode, MethodParamChangeNode methodParamChangeNode1) {
        StringBuilder stringBuilder = new StringBuilder();
        MethodInfo methodInfo1 = methodParamChangeNode.getInitMethod();
        stringBuilder.append(methodInfo1.getClassName());
        stringBuilder.append((char) -273874441);
        stringBuilder.append(methodInfo1.getSourceNameAndDescriptor());
        StringBuilder stringBuilder1 = new StringBuilder();
        MethodInfo methodInfo2 = methodParamChangeNode1.getInitMethod();
        stringBuilder1.append(methodInfo2.getClassName());
        stringBuilder1.append((char) 825166776);
        stringBuilder1.append(methodInfo2.getSourceNameAndDescriptor());
        return stringBuilder.toString().compareTo(stringBuilder1.toString());
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareNodes((MethodParamChangeNode) object, (MethodParamChangeNode) object1);
    }

    public MethodParamChangeComparator(ParameterKeyGenerator parameterKeyGenerator) {
        this.keyGenerator = parameterKeyGenerator;
    }
}
