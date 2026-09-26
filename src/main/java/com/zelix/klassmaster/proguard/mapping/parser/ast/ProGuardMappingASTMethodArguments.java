package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameSetter;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

import java.util.ArrayList;
import java.util.List;

public class ProGuardMappingASTMethodArguments extends ProGuardMappingSimpleNode implements ProGuardMappingNameSetter {
    public List argumentTypes = new ArrayList();

    @Override
    public void acceptTypeName(Object object) {
        this.argumentTypes.add(object);
    }

    public ProGuardMappingASTMethodArguments() {
        super(12);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ((ProGuardMappingASTMethodSignature) proGuardMappingNode).setArgumentTypes(this.argumentTypes);
    }
}
