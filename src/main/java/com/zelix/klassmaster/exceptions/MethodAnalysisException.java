package com.zelix.klassmaster.exceptions;

public class MethodAnalysisException extends ZkmProcessingException {
    public final String methodName;
    private static final String DETAIL_SEPARATOR = " : '";

    public MethodAnalysisException(String string, String string1) {
        super(string);
        this.methodName = string1;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + DETAIL_SEPARATOR + this.getMethodName() + "'";
    }

    public String getMethodName() {
        return this.methodName;
    }
}
