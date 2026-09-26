package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class IdentityValueHolder extends ObservableHolder {
    public IdentityValueHolder() throws ZkmException, IOException {
        super(null);
    }

    @Override
    public int hashCode() {
        return this.value != null ? System.identityHashCode(this.value) : 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IdentityValueHolder)) {
            return false;
        } else {
            return this.value != null ? this.value == ((IdentityValueHolder) object).getValue() : object == null;
        }
    }

    public IdentityValueHolder(Object object) throws ZkmException, IOException {
        super(object);
    }
}
