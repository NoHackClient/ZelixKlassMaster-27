package com.zelix.klassmaster.util;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public interface SetValuedMap {
    int getKeyCount();

    boolean containsValue(Object object, Object object1);

    boolean isEmpty();

    Set getValues(Object object);

    boolean removeValue(Object object, Object object1);

    Set keySet();

    boolean containsKey(Object object);

    List getValueList(Object object);

    Set removeKey(Object object);

    void retainValues(Object[] objects);

    Enumeration keys();

    boolean addValue(Object object, Object object1);

    void clear();
}
