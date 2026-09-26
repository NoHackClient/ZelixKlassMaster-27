package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.util.ArrayList;

public class ASTChangeLogInParameter extends SingleValueParameterNode {
    public ArrayList fileNames = new ArrayList();
    public boolean loose = false;

    @Override
    public String getValue(int ba) {
        if (ba < this.fileNames.size()) {
            return (String) this.fileNames.get(ba);
        } else {
            throw new IllegalArgumentException(String.valueOf(ba));
        }
    }

    @Override
    public int getValueCount() {
        return this.fileNames != null ? this.fileNames.size() : 0;
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            ZkmScriptASTStringLiteral zkmScriptASTStringLiteral = (ZkmScriptASTStringLiteral) this.jjtGetChild(i);
            this.fileNames.add(zkmScriptASTStringLiteral.getValue());
        }
    }

    public void setLoose() {
        this.loose = true;
    }

    public ASTChangeLogInParameter() {
        super(95);
    }
}
