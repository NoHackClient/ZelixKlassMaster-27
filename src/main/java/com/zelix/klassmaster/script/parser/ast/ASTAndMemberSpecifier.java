package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.DescribableSpec;
import com.zelix.klassmaster.obfuscator.exclude.MemberSpecifierMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTAndMemberSpecifier extends ZkmScriptSimpleNode implements MemberSpecifierMatcher {
    public boolean negated = false;
    private static final String AND_SEPARATOR = " && ";

    @Override
    public boolean hasMatchingMember(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        int ba = this.children.length;

        for (int i = 0; i < ba; i++) {
            if (!((MemberSpecifierMatcher) this.children[i]).hasMatchingMember(classFileBase, classHierarchyQuery)) {
                return false;
            }
        }

        return true;
    }

    public ASTAndMemberSpecifier() {
        super(151);
    }

    @Override
    public String getSpecText() {
        java.lang.StringBuffer stringBuffer2 = null;
        java.lang.StringBuffer stringBuffer1 = null;
        int ba;
        StringBuffer stringBuffer;
        int bb;
        int bd;
        label70:
        {
            label69:
            {
                label73:
                {
                    bd = ZkmScriptSimpleNode.getFlowPredicate();
                    stringBuffer = new StringBuffer();
                    ba = bd;
                    bb = this.children.length;
                    bd = (this.negated ? 1 : 0);
                    if (ba == 0) {
                        if (this.negated) {
                            stringBuffer1 = stringBuffer;
                            byte be = 40;
                            break label73;
                        }

                        bd = bb;
                    }

                    if (ba != 0) {
                        break label70;
                    }

                    if (bd <= 1) {
                        break label69;
                    }

                    stringBuffer1 = stringBuffer;
                    byte bi = 40;
                }

                stringBuffer1.append('(');
            }

            bd = 0;
        }

        int bc = bd;

        while (true) {
            if (bc < bb) {
                DescribableSpec describableSpec = (DescribableSpec) this.children[bc];
                if (ba == 0) {
                    if (ba != 0) {
                        stringBuffer2 = stringBuffer;
                        byte bh = 41;
                        break;
                    }

                    stringBuffer.append(describableSpec.getSpecText());
                    if (bc < bb - 1) {
                        stringBuffer.append(AND_SEPARATOR);
                    }

                    bc++;
                }

                if (ba == 0) {
                    continue;
                }
            }

            bd = (this.negated ? 1 : 0);
            if (ba == 0) {
                if (this.negated) {
                    stringBuffer2 = stringBuffer;
                    byte bg = 41;
                    break;
                }

                bd = bb;
            }

            if (bd <= 1) {
                return (this.negated ? "!" : "") + stringBuffer.toString();
            }

            stringBuffer2 = stringBuffer;
            byte bf = 41;
            break;
        }

        stringBuffer2.append(')');
        return (this.negated ? "!" : "") + stringBuffer.toString();
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }
    }
}
