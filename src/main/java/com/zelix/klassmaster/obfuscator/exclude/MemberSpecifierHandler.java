package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.script.parser.ast.ASTComplexFieldSpecifier;

public interface MemberSpecifierHandler {
    void setFieldSpecifier(ASTComplexFieldSpecifier aSTComplexFieldSpecifier);

    void setFieldType(String string);
}
