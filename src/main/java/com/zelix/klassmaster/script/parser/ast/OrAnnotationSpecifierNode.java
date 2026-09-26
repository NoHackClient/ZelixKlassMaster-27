package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.AnnotationMatcher;

import java.util.Set;

public abstract class OrAnnotationSpecifierNode extends OrSpecifierNode implements AnnotationMatcher {
    @Override
    public boolean matchesAnyAnnotation(Set set1) {
        boolean bl = false;
        int ba = this.children.length;

        for (int i = 0; i < ba; i++) {
            if (((AnnotationMatcher) this.children[i]).matchesAnyAnnotation(set1)) {
                bl = true;
                break;
            }
        }

        return super.negated ^ bl;
    }

    public OrAnnotationSpecifierNode() {
        super(176);
    }
}
