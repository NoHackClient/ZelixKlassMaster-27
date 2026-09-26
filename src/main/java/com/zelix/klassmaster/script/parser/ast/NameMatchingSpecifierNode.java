package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;

public abstract class NameMatchingSpecifierNode extends ComplexSpecifierNode implements QualifiedNameMatcher {
    @Override
    public final boolean matchesName(String string) {
        return ((QualifiedNameMatcher) this.jjtGetChild(0)).matchesName(string);
    }

    public NameMatchingSpecifierNode(int ba) {
        super(ba);
    }
}
