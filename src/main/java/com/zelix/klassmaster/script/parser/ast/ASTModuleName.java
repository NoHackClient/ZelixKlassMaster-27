package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASTModuleName extends ZkmScriptSimpleNode implements QualifiedNameMatcher {
    public String moduleName;
    public List nameComponents = new ArrayList();

    @Override
    public boolean matchesName(String string) {
        return ZkmStringUtils.matchesWildcard(string, this.moduleName);
    }

    public ASTModuleName() {
        super(189);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        int ba = this.jjtGetNumChildren();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < ba; i++) {
            String string = ((ScriptValueNode) this.jjtGetChild(i)).getValue();
            this.nameComponents.add(string);
            stringBuilder.append(string);
            if (i < ba - 1) {
                stringBuilder.append(".");
            }
        }

        this.moduleName = stringBuilder.toString();
    }

    public List getNameComponents() {
        int ba = this.jjtGetNumChildren();
        ArrayList arrayList = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ASTModuleNameComponent aSTModuleNameComponent = (ASTModuleNameComponent) this.jjtGetChild(i);
            arrayList.add(aSTModuleNameComponent.getValue());
        }

        return arrayList;
    }

    @Override
    public String getSpecText() {
        return this.moduleName + "/";
    }
}
