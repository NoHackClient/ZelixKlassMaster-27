package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public class ProGuardMappingASTNewClassName extends ProGuardMappingSimpleNode implements ProGuardMappingNameHolder {
    public String newClassName;

    @Override
    public void acceptName(Object object) {
        this.newClassName = (String) object;
    }

    public ProGuardMappingASTNewClassName() {
        super(6);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ASTClassNameChange) proGuardMappingNode).setNewName(this.newClassName);
    }
}
