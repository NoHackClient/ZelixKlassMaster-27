package com.zelix.klassmaster.util;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public interface MultiMap {
    boolean isEmpty();

    boolean removeValue(Object object, Object object1);

    List getValues(Object object);

    int getKeyCount();

    Set entrySet();

    boolean containsKey(Object object);

    Set keySet();

    void addValue(Object object, Object object1);

    Enumeration allValues();

    Enumeration keys();

    boolean containsValue(Object object, Object object1);

    List removeKey(Object object);

    Enumeration valuesOf(Object object);

    int getValueCount();

    void clear();
}
