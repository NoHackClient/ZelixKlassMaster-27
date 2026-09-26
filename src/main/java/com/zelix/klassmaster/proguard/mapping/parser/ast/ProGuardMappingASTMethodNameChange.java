package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameSetter;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.ProGuardMemberChangeApplier;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;

import java.util.List;

public class ProGuardMappingASTMethodNameChange extends ProGuardNameChangeNode implements ProGuardMappingNameSetter, ProGuardMemberChangeApplier {
    public List argumentTypes;
    public String returnType;

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
    }

    @Override
    public void applyMemberChange(String string, ProGuardMappingTranslator proGuardMappingTranslator) {
        proGuardMappingTranslator.addMethodMapping(string, this.returnType, super.oldName, super.newName, this.argumentTypes);
    }

    public void setArgumentTypes(List list1) {
        this.argumentTypes = list1;
    }

    @Override
    public void acceptTypeName(Object object) {
        this.returnType = (String) object;
    }

    public ProGuardMappingASTMethodNameChange() {
        super(4);
    }
}
