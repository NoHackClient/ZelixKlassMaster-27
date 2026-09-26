package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public class ProGuardMappingASTOldFieldName extends ProGuardMappingSimpleNode implements ProGuardMappingNameHolder {
    public String oldFieldName;

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ProGuardMappingASTFieldNameChange) proGuardMappingNode).setOldName(this.oldFieldName);
    }

    public ProGuardMappingASTOldFieldName() {
        super(7);
    }

    @Override
    public void acceptName(Object object) {
        this.oldFieldName = (String) object;
    }
}
