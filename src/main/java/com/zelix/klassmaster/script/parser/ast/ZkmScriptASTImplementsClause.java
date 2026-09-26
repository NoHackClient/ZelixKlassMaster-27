package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.util.ArrayList;

public class ZkmScriptASTImplementsClause extends ZkmScriptSimpleNode implements TypeTextHolder {
    public ArrayList interfaceNames = new ArrayList();

    @Override
    public void setTypeText(Object object) {
        this.interfaceNames.add(object);
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
        int bc = 0;
        int be = 0;

        for (ArrayList arrayList = this.interfaceNames; be < arrayList.size(); arrayList = this.interfaceNames) {
            aSTRenameFilterParameter.addImplementsName((String) this.interfaceNames.get(bc));
            be = ++bc;
        }

        boolean bl = false;

        for (int i = 0; i < ba; i++) {
            ZkmScriptNode zkmScriptNode1 = this.jjtGetChild(i);
            if (zkmScriptNode1 instanceof ASTComplexAnnotationSpecifier) {
                aSTRenameFilterParameter.addImplementsAnnotation((ASTComplexAnnotationSpecifier) zkmScriptNode1);
                bl = true;
            } else if (zkmScriptNode1 instanceof ASTQualifiedClassName) {
                if (!bl) {
                    aSTRenameFilterParameter.addImplementsAnnotation(null);
                }

                bl = false;
            }
        }
    }

    public ZkmScriptASTImplementsClause() {
        super(154);
    }
}
