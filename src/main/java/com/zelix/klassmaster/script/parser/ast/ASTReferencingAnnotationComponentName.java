package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTReferencingAnnotationComponentName extends ScriptValueNode {
    public ASTReferencingAnnotationComponentName() {
        super(180);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ASTRenameFilterParameter) object).setReferencingAnnotationComponent(this.value);
    }
}
