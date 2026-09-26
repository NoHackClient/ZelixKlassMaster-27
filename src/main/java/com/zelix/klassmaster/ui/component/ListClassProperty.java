package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.util.IntegerCache;

import java.util.Map;

public class ListClassProperty extends ClassPropertyNode {
    public int cursor;
    public Map indexMap;
    public IntegerCache integerCache;

    public final void resetCursor() {
        this.cursor = 0;
    }

    public ListClassProperty(String string, ClassFileComponent classFileComponent, IntegerCache integerCache1) {
        super(string, classFileComponent);
        this.integerCache = integerCache1;
    }
}
