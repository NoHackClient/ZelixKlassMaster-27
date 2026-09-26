package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;

public abstract class OrNameSpecifierNode extends OrSpecifierNode implements QualifiedNameMatcher {
    public OrNameSpecifierNode(int ba) {
        super(ba);
    }

    @Override
    public boolean matchesName(String string) {
        boolean bl = false;
        int ba = this.children.length;

        for (int i = 0; i < ba; i++) {
            if (((QualifiedNameMatcher) this.children[i]).matchesName(string)) {
                bl = true;
                break;
            }
        }

        return super.negated ^ bl;
    }
}
