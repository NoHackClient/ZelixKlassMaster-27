package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

import java.util.List;

public class ProGuardMappingASTMethodSignature extends ProGuardMappingSimpleNode implements ProGuardMappingNameHolder {
    public List argumentTypes;
    public String methodName;

    public void setArgumentTypes(List list1) {
        this.argumentTypes = list1;
    }

    @Override
    public void acceptName(Object object) {
        this.methodName = (String) object;
    }

    public ProGuardMappingASTMethodSignature() {
        super(11);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ProGuardMappingASTMethodNameChange proGuardMappingASTMethodNameChange = (ProGuardMappingASTMethodNameChange) proGuardMappingNode;
        proGuardMappingASTMethodNameChange.setOldName(this.methodName);
        proGuardMappingASTMethodNameChange.setArgumentTypes(this.argumentTypes);
    }
}
