package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.MethodSignature;

public class ChangeLogMethodSignature extends MethodSignature {
    public final boolean parametersObfuscated;

    public ChangeLogMethodSignature(String string, String string1, String string2, boolean parametersObfuscated) {
        super(string, string1, string2);
        this.parametersObfuscated = parametersObfuscated;
    }

    public boolean isParametersObfuscated() {
        return this.parametersObfuscated;
    }
}
