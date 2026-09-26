package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.util.ZkmStringUtils;

public class WildcardNameNode extends ScriptValueNode implements QualifiedNameMatcher {
    @Override
    public final boolean matchesName(String string) {
        return ZkmStringUtils.matchesWildcard(string, this.value);
    }

    public WildcardNameNode(int ba) {
        super(ba);
    }

    @Override
    public final String getSpecText() {
        return this.value;
    }

    public boolean isExactName() {
        return this.value.indexOf("*") == -1;
    }
}
