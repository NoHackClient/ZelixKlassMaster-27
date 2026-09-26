package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeLogASTMethodNameChange extends ChangeLogModifierNode {
    public String oldArgumentTypes;
    public String parameterChangeLookupData;
    public String returnType;
    public boolean parametersObfuscated;
    public String newMethodName;
    public String parameterChangeData;
    public String newArgumentTypes;
    public String oldMethodName;

    public void setParameterChangeData(String string) {
        this.parameterChangeData = string;
    }

    public void setParameterChangeLookupData(String string) {
        this.parameterChangeLookupData = string;
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int be, int bf, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).interpret(this, 30872, 34067, 41973, abstractChangeLog);
        }

        if (this.newMethodName == null) {
            this.newMethodName = this.oldMethodName;
        }

        ASTClassChange aSTClassChange = (ASTClassChange) changeLogNode;
        String string4 = this.oldMethodName;
        String string5 = this.oldArgumentTypes;
        String string6 = this.returnType;
        int modifiers = this.getModifiers();
        String string7 = this.newMethodName;
        String string8 = this.newArgumentTypes;
        boolean parametersObfuscated = this.parametersObfuscated;
        String string9 = this.parameterChangeData;
        String string10 = this.parameterChangeLookupData;
        Boolean boolean2 = this.isManufactured();
        String string3 = string10;
        String string2 = string9;
        Boolean boolean1 = parametersObfuscated;
        String string1 = string8;
        String string = string7;
        aSTClassChange.addMethodChange(string4, string5, string6, modifiers, string, string1, boolean1, string2, string3, boolean2);
    }

    public void setNewMethodName(String string) {
        this.newMethodName = string;
    }

    public void setNewSignature(String string, String string1, boolean parametersObfuscated) {
        if (this.newMethodName == null) {
            this.newMethodName = string;
            this.newArgumentTypes = string1;
            this.parametersObfuscated = parametersObfuscated;
        } else {
            this.parametersObfuscated = parametersObfuscated;
        }
    }

    public void setOldSignature(String string, String string1) {
        this.oldMethodName = string;
        this.oldArgumentTypes = string1;
    }

    public void setReturnType(String string) {
        this.returnType = string;
    }

    public ChangeLogASTMethodNameChange() {
        super(17);
    }
}
