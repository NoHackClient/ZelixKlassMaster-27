package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AnnotationSpecifierHolder;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTMethodParameter extends ZkmScriptSimpleNode implements TypeTextHolder, AnnotationSpecifierHolder {
    public ASTComplexAnnotationSpecifier annotationSpecifier;
    public String typeName;

    public ASTMethodParameter() {
        super(214);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }

        ZkmScriptASTMethodArguments zkmScriptASTMethodArguments = (ZkmScriptASTMethodArguments) zkmScriptNode;
        zkmScriptASTMethodArguments.setTypeText(this.typeName);
        zkmScriptASTMethodArguments.addArgumentAnnotation(this.annotationSpecifier);
    }

    @Override
    public void setAnnotationSpecifier(ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) {
        this.annotationSpecifier = aSTComplexAnnotationSpecifier;
    }

    @Override
    public void setTypeText(Object object) {
        this.typeName = (String) object;
    }
}
