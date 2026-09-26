package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.MemberSpecifierMatcher;

import java.io.IOException;

public class ASTBracketedMemberSpecifier extends OrSpecifierNode implements MemberSpecifierMatcher {
    @Override
    public boolean hasMatchingMember(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        boolean bl = false;
        int ba = this.children.length;

        for (int i = 0; i < ba; i++) {
            if (((MemberSpecifierMatcher) this.children[i]).hasMatchingMember(classFileBase, classHierarchyQuery)) {
                bl = true;
                break;
            }
        }

        return super.negated ^ bl;
    }

    public ASTBracketedMemberSpecifier() {
        super(150);
    }
}
