


package com.zelix.klassmaster.util;

import java.util.Set;

public interface NamedSet
        extends Set {
    public String getSetName();

    public Object clone();
}
