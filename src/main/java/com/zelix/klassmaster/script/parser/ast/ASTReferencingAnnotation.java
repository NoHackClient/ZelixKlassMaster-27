package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTReferencingAnnotation extends ZkmScriptSimpleNode implements TypeTextHolder {
    public String annotationTypeName;

    @Override
    public void setTypeText(Object object) {
        this.annotationTypeName = (String) object;
    }

    public ASTReferencingAnnotation() {
        super(179);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        this.jjtGetNumChildren();
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
        ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) object;
        if (this.annotationTypeName != null) {
            aSTRenameFilterParameter.setReferencingAnnotation(this.annotationTypeName);
        }
    }
}
