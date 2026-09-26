package com.zelix.klassmaster.obfuscator.reflection;

public enum ReflectionTargetScope {
    ALL_CLASSES,
    ALL_PUBLIC_METHODS,
    ALL_PUBLIC_FIELDS,
    ALL_METHODS_OF_CLASS,
    ALL_FIELDS_OF_CLASS,
    ALL_INSTANCE_FIELDS_OF_CLASS,
    ALL_LIST_RESOUCE_BUNDLE,
    ALL_STATIC_METHODS_ACCESSIBLE_FROM_CLASS,
    ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS,
    ALL_STATIC_FIELDS_ACCESSIBLE_FROM_CLASS,
    ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS,
    NONE;

    public static final ReflectionTargetScope[] ALL_VALUES = new ReflectionTargetScope[]{
            ALL_CLASSES,
            ALL_PUBLIC_METHODS,
            ALL_PUBLIC_FIELDS,
            ALL_METHODS_OF_CLASS,
            ALL_FIELDS_OF_CLASS,
            ALL_INSTANCE_FIELDS_OF_CLASS,
            ALL_LIST_RESOUCE_BUNDLE,
            ALL_STATIC_METHODS_ACCESSIBLE_FROM_CLASS,
            ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS,
            ALL_STATIC_FIELDS_ACCESSIBLE_FROM_CLASS,
            ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS,
            NONE
    };

    public static ReflectionTargetScope[] allValues() {
        return ALL_VALUES.clone();
    }
}
