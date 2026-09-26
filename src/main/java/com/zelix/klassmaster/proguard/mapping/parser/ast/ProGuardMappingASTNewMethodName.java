package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public class ProGuardMappingASTNewMethodName extends ProGuardMappingSimpleNode implements ProGuardMappingNameHolder {
    public String newMethodName;

    public ProGuardMappingASTNewMethodName() {
        super(9);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ProGuardMappingASTMethodNameChange) proGuardMappingNode).setNewName(this.newMethodName);
    }

    @Override
    public void acceptName(Object object) {
        this.newMethodName = (String) object;
    }
}
