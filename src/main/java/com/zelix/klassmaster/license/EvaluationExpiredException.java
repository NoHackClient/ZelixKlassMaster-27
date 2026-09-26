package com.zelix.klassmaster.license;

import com.zelix.klassmaster.exceptions.ZkmRuntimeException;

public class EvaluationExpiredException extends ZkmRuntimeException {
    public EvaluationExpiredException(String string) {
        super(string);
    }
}
