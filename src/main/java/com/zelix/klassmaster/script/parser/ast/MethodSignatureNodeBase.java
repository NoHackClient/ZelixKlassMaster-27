package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.MethodArgsPattern;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

public abstract class MethodSignatureNodeBase extends ZkmScriptSimpleNode {
    public MethodArgsPattern argsPattern;
    public ASTComplexAnnotationSpecifier[] parameterAnnotations;

    public MethodSignatureNodeBase(int ba) {
        super(ba);
    }

    public final void setParameterAnnotations(ASTComplexAnnotationSpecifier[] aSTComplexAnnotationSpecifiers) {
        this.parameterAnnotations = aSTComplexAnnotationSpecifiers;
    }

    public final void setArgsPattern(MethodArgsPattern methodArgsPattern1) {
        this.argsPattern = methodArgsPattern1;
    }
}
