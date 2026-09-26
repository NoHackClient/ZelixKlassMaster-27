package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.MemberSpecifierMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTComplexMemberSpecifier extends ZkmScriptSimpleNode implements MemberSpecifierMatcher {
    @Override
    public final boolean hasMatchingMember(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        return ((MemberSpecifierMatcher) this.jjtGetChild(0)).hasMatchingMember(classFileBase, classHierarchyQuery);
    }

    public ASTComplexMemberSpecifier() {
        super(149);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
    }

    @Override
    public String getSpecText() {
        new StringBuffer();
        return ((MemberSpecifierMatcher) this.jjtGetChild(0)).getSpecText();
    }
}
