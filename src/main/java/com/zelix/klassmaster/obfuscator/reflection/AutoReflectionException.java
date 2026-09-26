package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.exceptions.ZkmProcessingException;

public class AutoReflectionException extends ZkmProcessingException {
    public AutoReflectionException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
