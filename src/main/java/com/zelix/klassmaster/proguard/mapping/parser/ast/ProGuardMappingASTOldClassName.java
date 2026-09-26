package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public class ProGuardMappingASTOldClassName extends ProGuardMappingSimpleNode implements ProGuardMappingNameHolder {
    public String oldClassName;

    @Override
    public void acceptName(Object object) {
        this.oldClassName = (String) object;
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ASTClassNameChange) proGuardMappingNode).setOldName(this.oldClassName);
    }

    public ProGuardMappingASTOldClassName() {
        super(5);
    }
}
