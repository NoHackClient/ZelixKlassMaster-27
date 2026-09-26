package com.zelix.klassmaster.proguard.config.parser;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface ProGuardConfigNode {
    void jjtSetParent(ProGuardConfigNode proGuardConfigNode);

    void jjtClose();

    void jjtOpen();

    void translate(Object object, Object object1) throws ZkmException, IOException;

    ProGuardConfigNode jjtGetChild(int ba);

    ProGuardConfigNode jjtGetParent();

    void jjtAddChild(ProGuardConfigNode proGuardConfigNode, int ba);

    int jjtGetNumChildren();
}
