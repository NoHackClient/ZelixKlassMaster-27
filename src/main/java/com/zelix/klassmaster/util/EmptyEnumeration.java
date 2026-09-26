package com.zelix.klassmaster.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EmptyEnumeration implements Enumeration {
    @Override
    public final Object nextElement() {
        throw new NoSuchElementException();
    }

    @Override
    public final boolean hasMoreElements() {
        return false;
    }
}
