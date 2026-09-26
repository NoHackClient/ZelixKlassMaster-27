package com.zelix.klassmaster.classfile.attribute;

import java.util.HashMap;

public class AnnotationValueMap {
    public HashMap valuesByName = new HashMap(13);
    public RuntimeInvisibleAnnotationsAttribute sourceAttribute;

    public RuntimeInvisibleAnnotationsAttribute getSourceAttribute() {
        return this.sourceAttribute;
    }

    public AnnotationValueMap(RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute) {
        this.sourceAttribute = runtimeInvisibleAnnotationsAttribute;
    }

    public String getValue(Object object) {
        return (String) this.valuesByName.get(object);
    }

    public void putValue(Object object, Object object1) {
        this.valuesByName.put(object, object1);
    }

    public boolean hasValue(Object object) {
        return this.valuesByName.containsKey(object);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
