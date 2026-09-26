package com.zelix.klassmaster.exceptions;

public class DuplicateFieldException extends ZkmProcessingException {
    public DuplicateFieldException() {
        super("The class already has a field with this name and type");
    }
}
