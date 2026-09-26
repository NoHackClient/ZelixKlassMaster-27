package com.zelix.klassmaster.script.parser;

import com.zelix.klassmaster.util.ZkmAssert;

public class ZkmScriptToken {
    public static final String[] PARAMETER_DESCRIPTIONS = new String[9];
    public int endColumn;
    public int beginColumn;
    public int kind;
    public ZkmScriptToken specialToken;
    public String image;
    public int lastColumn;
    public int parameterKind;
    public ZkmScriptToken next;
    public int endLine;

    public static final ZkmScriptToken newToken(int ba) {
        switch (ba) {
            default:
                return new ZkmScriptToken();
        }
    }

    public String getParameterDescription() {
        return PARAMETER_DESCRIPTIONS[this.parameterKind];
    }

    @Override
    public final String toString() {
        int flowPredicate = ZkmScriptSimpleNode.getFlowPredicate();
        String string;
        if (flowPredicate == 0) {
            if (this.image == null) {
                return "<" + ZkmAssert.getSimpleClassName(this) + " NULL >";
            }

            string = this.image;
        } else {
            string = this.image;
        }

        return string;
    }

    public void setParameterKind(int parameterKind) {
        this.parameterKind = parameterKind;
    }

    static {
        PARAMETER_DESCRIPTIONS[0] = "";
        PARAMETER_DESCRIPTIONS[2] = " \"package name\" parameter";
        PARAMETER_DESCRIPTIONS[3] = " \"class name\" parameter";
        PARAMETER_DESCRIPTIONS[4] = " \"field name\" parameter";
        PARAMETER_DESCRIPTIONS[5] = " \"method name\" parameter";
        PARAMETER_DESCRIPTIONS[6] = " \"link class name\" parameter";
        PARAMETER_DESCRIPTIONS[7] = " \"link search path\" parameter";
        PARAMETER_DESCRIPTIONS[8] = " \"link method name\" parameter";
    }
}
