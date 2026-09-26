package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;

public class ProGuardMappingASTType extends ProGuardMappingNameNode {
    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ProGuardMappingASTQualifiedType) proGuardMappingNode).addNamePart(super.name);
    }

    public ProGuardMappingASTType() {
        super(16);
    }
}
