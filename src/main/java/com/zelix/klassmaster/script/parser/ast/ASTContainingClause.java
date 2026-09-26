package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.DescribableSpec;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTContainingClause extends ZkmScriptSimpleNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        if (this.jjtGetNumChildren() > 0) {
            this.jjtGetChild(0).execute(this, scriptEnvironment1);
        }

        ((ASTRenameFilterParameter) object).setContainingClause(this);
    }

    public final boolean containsMatchingMember(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        return this.jjtGetNumChildren() > 0 ? ((ASTComplexMemberSpecifier) this.jjtGetChild(0)).hasMatchingMember(classFileBase, classHierarchyQuery) : true;
    }

    public String toScriptText() {
        java.lang.StringBuilder stringBuilder1 = null;
        StringBuilder stringBuilder = new StringBuilder();
        int flowPredicate = ZkmScriptSimpleNode.getFlowPredicate();
        int bb = this.jjtGetNumChildren();
        int ba = flowPredicate;
        int bc = 0;

        while (true) {
            if (bc < bb) {
                DescribableSpec describableSpec = (DescribableSpec) this.jjtGetChild(bc);
                if (ba == 0) {
                    stringBuilder1 = stringBuilder;
                    if (ba != 0) {
                        break;
                    }

                    stringBuilder.append(describableSpec.getSpecText());
                    if (bc < bb - 1) {
                        stringBuilder.append(" && ");
                    }

                    bc++;
                }

                if (ba == 0) {
                    continue;
                }
            }

            stringBuilder1 = new StringBuilder().append("containing{").append(stringBuilder.toString()).append('}');
            break;
        }

        return stringBuilder1.toString();
    }

    public ASTContainingClause() {
        super(147);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
