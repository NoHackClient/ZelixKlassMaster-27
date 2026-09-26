package com.zelix.klassmaster.obfuscator.references;

import com.zelix.klassmaster.exceptions.ZkmProcessingException;

public class ReferenceObfuscationException extends ZkmProcessingException {
    public ReferenceObfuscationException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
