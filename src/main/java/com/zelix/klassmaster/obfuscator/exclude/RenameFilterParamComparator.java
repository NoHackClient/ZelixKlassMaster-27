package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;

import java.util.Comparator;

public class RenameFilterParamComparator implements Comparator {
    @Override
    public int compare(Object object, Object object1) {
        return this.compareParameters((ASTRenameFilterParameter) object, (ASTRenameFilterParameter) object1);
    }

    public int compareParameters(ASTRenameFilterParameter aSTRenameFilterParameter, ASTRenameFilterParameter aSTRenameFilterParameter1) {
        if (aSTRenameFilterParameter.computeSpecifierKind() < aSTRenameFilterParameter1.computeSpecifierKind()) {
            return -1;
        } else {
            return aSTRenameFilterParameter.computeSpecifierKind() > aSTRenameFilterParameter1.computeSpecifierKind()
                    ? 1
                    : aSTRenameFilterParameter.compareSpecificity(aSTRenameFilterParameter1);
        }
    }
}
