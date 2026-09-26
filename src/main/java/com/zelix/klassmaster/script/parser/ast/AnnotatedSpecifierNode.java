package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.AnnotationMatcher;

import java.util.Set;

public abstract class AnnotatedSpecifierNode extends ComplexSpecifierNode implements AnnotationMatcher {
    @Override
    public final boolean matchesAnyAnnotation(Set set1) {
        return ((AnnotationMatcher) this.jjtGetChild(0)).matchesAnyAnnotation(set1);
    }

    public AnnotatedSpecifierNode() {
        super(175);
    }
}
