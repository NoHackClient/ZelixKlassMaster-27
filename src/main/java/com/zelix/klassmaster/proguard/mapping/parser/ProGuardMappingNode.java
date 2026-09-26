package com.zelix.klassmaster.proguard.mapping.parser;

import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;

public interface ProGuardMappingNode {
    void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator);

    void jjtClose();

    int jjtGetNumChildren();

    void jjtAddChild(ProGuardMappingNode proGuardMappingNode, int ba);

    ProGuardMappingNode jjtGetChild(int ba);

    void jjtOpen();

    void jjtSetParent(ProGuardMappingNode proGuardMappingNode);
}
