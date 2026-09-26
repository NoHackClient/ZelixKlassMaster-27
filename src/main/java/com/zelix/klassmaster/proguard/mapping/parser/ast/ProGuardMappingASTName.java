package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;

public class ProGuardMappingASTName extends ProGuardMappingNameNode {
    public ProGuardMappingASTName() {
        super(15);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ProGuardMappingNameHolder) proGuardMappingNode).acceptName(super.name);
    }
}
