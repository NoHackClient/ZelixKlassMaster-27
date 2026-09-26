package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.AnnotationMatcher;

import java.util.Set;

public abstract class AnnotationConjunctionNode extends AndSpecifierNode implements AnnotationMatcher {
    @Override
    public boolean matchesAnyAnnotation(Set set1) {
        int ba = this.children.length;

        for (int i = 0; i < ba; i++) {
            if (!((AnnotationMatcher) this.children[i]).matchesAnyAnnotation(set1)) {
                return false;
            }
        }

        return true;
    }

    public AnnotationConjunctionNode() {
        super(177);
    }
}
