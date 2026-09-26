package com.zelix.klassmaster.script.parser;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ast.SummarizingStatementNode;

import java.io.IOException;

public interface ZkmScriptNode {
    int jjtGetNumChildren();

    void jjtOpen();

    void jjtClose();

    ZkmScriptNode jjtGetChild(int ba);

    void jjtAddChild(ZkmScriptNode zkmScriptNode, int ba);

    ZkmScriptNode jjtGetParent();

    SummarizingStatementNode getSummarizingStatement();

    void jjtSetParent(ZkmScriptNode zkmScriptNode);

    void execute(Object object, Object object1) throws ZkmException, IOException;
}
