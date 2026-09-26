package com.zelix.klassmaster.license;

import com.zelix.klassmaster.exceptions.ZkmRuntimeException;

public class LicenseExpiredException extends ZkmRuntimeException {
    public LicenseExpiredException(String string) {
        super(string);
    }
}
