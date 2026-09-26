package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AnnotationMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;

public class ASTComplexAnnotationSpecifier extends AnnotatedSpecifierNode {
    @Override
    public boolean isLiteralName() {
        AnnotationMatcher annotationMatcher = (AnnotationMatcher) this.jjtGetChild(0);
        return annotationMatcher instanceof ASTAnnotation ? ((ASTAnnotation) annotationMatcher).isLiteralName() : false;
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
    }
}
