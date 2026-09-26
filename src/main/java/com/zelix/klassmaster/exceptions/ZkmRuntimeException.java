package com.zelix.klassmaster.exceptions;

public class ZkmRuntimeException extends RuntimeException {
    public ZkmRuntimeException(String string) {
        super(string);
    }

    public ZkmRuntimeException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
