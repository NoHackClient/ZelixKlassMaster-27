package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.FileFilterComponentSink;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTAndFileFilterComponent extends ZkmScriptSimpleNode implements FileFilterComponentSink {
    @Override
    public void addFileFilterComponent(Object object) {
        FileFilterNodeBase fileFilterNodeBase = (FileFilterNodeBase) object;
        ((ASTFileFilter) this.jjtGetParent()).addAndTerm(fileFilterNodeBase);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.jjtGetNumChildren();
        ((FileFilterNodeBase) this.jjtGetChild(0)).execute(this, scriptEnvironment1);
    }

    public ASTAndFileFilterComponent() {
        super(10);
    }
}
