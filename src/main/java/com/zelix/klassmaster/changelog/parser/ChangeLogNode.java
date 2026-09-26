package com.zelix.klassmaster.changelog.parser;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface ChangeLogNode {
    void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException;

    ChangeLogNode jjtGetChild(int ba);

    void jjtOpen();

    void dump();

    void jjtSetParent(ChangeLogNode changeLogNode);

    void jjtClose();

    int jjtGetNumChildren();

    void jjtAddChild(ChangeLogNode changeLogNode, int ba);
}
