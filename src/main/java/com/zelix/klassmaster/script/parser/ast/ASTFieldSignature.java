package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.MemberSpecifierHandler;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTFieldSignature extends ZkmScriptSimpleNode implements TypeTextHolder {
    public String fieldType;
    public ASTComplexFieldSpecifier fieldSpecifier;

    public ASTFieldSignature() {
        super(200);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        MemberSpecifierHandler memberSpecifierHandler = (MemberSpecifierHandler) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }

        if (this.fieldSpecifier != null) {
            memberSpecifierHandler.setFieldSpecifier(this.fieldSpecifier);
        }

        if (this.fieldType != null) {
            memberSpecifierHandler.setFieldType(this.fieldType);
        }
    }

    public void setFieldSpecifier(ASTComplexFieldSpecifier aSTComplexFieldSpecifier) {
        this.fieldSpecifier = aSTComplexFieldSpecifier;
    }

    @Override
    public void setTypeText(Object object) {
        this.fieldType = (String) object;
    }
}
