package com.zelix.klassmaster.obfuscator.parameters;

import java.util.Comparator;

public class KeyNodeComparator implements Comparator {
    public final ParameterKeyGenerator keyGenerator;

    @Override
    public int compare(Object object, Object object1) {
        return this.compareNodes((LongKeyNode) object, (LongKeyNode) object1);
    }

    public int compareNodes(LongKeyNode longKeyNode, LongKeyNode longKeyNode1) {
        return longKeyNode.getIndex() - longKeyNode1.getIndex();
    }

    public KeyNodeComparator(ParameterKeyGenerator parameterKeyGenerator) {
        this.keyGenerator = parameterKeyGenerator;
    }
}
