package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.util.ZkmStringUtils;

public class SimpleNamePattern implements ClassNamePattern {
    public String namePattern;

    @Override
    public String getSpecText() {
        return this.namePattern;
    }

    @Override
    public boolean hasPlusTag() {
        return false;
    }

    @Override
    public boolean isLiteralName() {
        return this.namePattern.indexOf("*") == -1;
    }

    @Override
    public boolean hasCaretTag() {
        return false;
    }

    public SimpleNamePattern(String string) {
        this.namePattern = string;
    }

    @Override
    public boolean matchesName(String string) {
        return ZkmStringUtils.matchesWildcard(string, this.namePattern);
    }

    @Override
    public double computeSpecificity(Object object, Object object1) {
        double ba = 1.0;
        if (!ASTRenameFilterParameter.isAnyWildcard(this.namePattern)) {
            if (!ASTRenameFilterParameter.containsWildcard(this.namePattern)) {
                ba *= 0.1;
            } else {
                ba *= 0.5;
            }
        }

        return ba;
    }

    @Override
    public String getNamePattern() {
        return this.namePattern;
    }
}
