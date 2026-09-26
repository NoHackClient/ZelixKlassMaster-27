package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.exceptions.ClassLookupException;

public class TraceClassNotFoundException extends ClassLookupException {
    public String className;

    public TraceClassNotFoundException(String string, String string1) {
        super(string1);
        this.className = string.replace('/', '.');
    }

    public String getClassName() {
        return this.className;
    }
}
