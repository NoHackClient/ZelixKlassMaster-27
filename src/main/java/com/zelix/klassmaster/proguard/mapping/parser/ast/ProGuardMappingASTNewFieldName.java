package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public class ProGuardMappingASTNewFieldName extends ProGuardMappingSimpleNode implements ProGuardMappingNameHolder {
    public String newFieldName;

    public ProGuardMappingASTNewFieldName() {
        super(8);
    }

    @Override
    public void acceptName(Object object) {
        this.newFieldName = (String) object;
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ProGuardMappingASTFieldNameChange) proGuardMappingNode).setNewName(this.newFieldName);
    }
}
