package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;

public abstract class AllOfSpecifierNode extends AndSpecifierNode implements QualifiedNameMatcher {
    public AllOfSpecifierNode(int ba) {
        super(ba);
    }

    @Override
    public boolean matchesName(String string) {
        int ba = this.children.length;

        for (int i = 0; i < ba; i++) {
            if (!((QualifiedNameMatcher) this.children[i]).matchesName(string)) {
                return false;
            }
        }

        return true;
    }
}
