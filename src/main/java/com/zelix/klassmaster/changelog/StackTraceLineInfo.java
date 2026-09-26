package com.zelix.klassmaster.changelog;

public interface StackTraceLineInfo {
    String getClassName();

    String getLineText();

    boolean isValidFrame();

    boolean hasLineNumber();

    int getLineNumber(Object object, Object object1);
}
