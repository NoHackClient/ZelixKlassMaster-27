package com.zelix.klassmaster.exceptions;

public class ClassFileLoadException extends ZkmProcessingException {
    public String className;

    public ClassFileLoadException(String string, String string1) {
        super(string1);
        this.className = string.replace('/', '.');
    }

    public String getClassName() {
        return this.className;
    }

    public ClassFileLoadException(String string) {
        super(string);
    }
}
