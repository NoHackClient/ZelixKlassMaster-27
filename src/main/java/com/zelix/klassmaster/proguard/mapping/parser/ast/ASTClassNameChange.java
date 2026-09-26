package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;

public class ASTClassNameChange extends ProGuardNameChangeNode {
    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
    }

    public ASTClassNameChange() {
        super(2);
    }
}
