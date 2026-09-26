package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameSetter;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.ProGuardMemberChangeApplier;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;

public class ProGuardMappingASTFieldNameChange extends ProGuardNameChangeNode implements ProGuardMappingNameSetter, ProGuardMemberChangeApplier {
    public String fieldType;

    public ProGuardMappingASTFieldNameChange() {
        super(3);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
    }

    @Override
    public void acceptTypeName(Object object) {
        this.fieldType = (String) object;
    }

    @Override
    public void applyMemberChange(String string, ProGuardMappingTranslator proGuardMappingTranslator) {
        proGuardMappingTranslator.addFieldMapping(string, this.fieldType, super.oldName, super.newName);
    }
}
