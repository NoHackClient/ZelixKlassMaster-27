package com.zelix.klassmaster.obfuscator.reflection;

public class ReflectionScopeSwitchMap {
    public static final int[] TARGET_SCOPE_SWITCH = new int[ReflectionTargetScope.allValues().length];
    public static final int[] TARGET_KIND_SWITCH;

    static {
        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_CLASSES.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError15) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_LIST_RESOUCE_BUNDLE.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError14) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_PUBLIC_FIELDS.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError13) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_FIELDS_OF_CLASS.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError12) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_INSTANCE_FIELDS_OF_CLASS.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError11) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_STATIC_FIELDS_ACCESSIBLE_FROM_CLASS.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError10) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError9) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_PUBLIC_METHODS.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_STATIC_METHODS_ACCESSIBLE_FROM_CLASS.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS.ordinal()] = 10;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_METHODS_OF_CLASS.ordinal()] = 11;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.NONE.ordinal()] = 12;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        TARGET_KIND_SWITCH = new int[ReflectionTargetKind.allValues().length];

        try {
            TARGET_KIND_SWITCH[ReflectionTargetKind.DEFAULT_CONSTRUCTOR_CALL_TYPE.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            TARGET_KIND_SWITCH[ReflectionTargetKind.SPECIFIC_CONSTRUCTOR_CALL_TYPE.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            TARGET_KIND_SWITCH[ReflectionTargetKind.ALL_CONSTRUCTORS_CALL_TYPE.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            TARGET_KIND_SWITCH[ReflectionTargetKind.METHOD_HANDLE_TYPE.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ReflectionScopeSwitchMap() {
    }
}
