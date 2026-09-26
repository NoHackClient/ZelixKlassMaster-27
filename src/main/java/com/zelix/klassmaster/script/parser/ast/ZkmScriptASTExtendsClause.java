package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ZkmScriptASTExtendsClause extends ZkmScriptSimpleNode implements TypeTextHolder {
    public String superclassName;

    public ZkmScriptASTExtendsClause() {
        super(153);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }

        ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) zkmScriptNode;
        if (this.superclassName != null) {
            aSTRenameFilterParameter.setExtendsClassName(this.superclassName);
        }

        if (ba > 1) {
            for (int i = 0; i < ba; i++) {
                ZkmScriptNode zkmScriptNode1 = this.jjtGetChild(i);
                if (zkmScriptNode1 instanceof ASTComplexAnnotationSpecifier) {
                    aSTRenameFilterParameter.setExtendsAnnotation((ASTComplexAnnotationSpecifier) zkmScriptNode1);
                }
            }
        }
    }

    @Override
    public void setTypeText(Object object) {
        this.superclassName = (String) object;
    }
}
