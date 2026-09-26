package com.zelix.klassmaster.obfuscator.reflection;

public class ReflectionKindSwitchMap {
    public static final int[] MEMBER_SCOPE_SWITCH = new int[MemberScope.allValues().length];
    public static final int[] TARGET_SCOPE_SWITCH;

    static {
        try {
            MEMBER_SCOPE_SWITCH[MemberScope.INSTANCE.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError13) {
        }

        try {
            MEMBER_SCOPE_SWITCH[MemberScope.STATIC.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError12) {
        }

        TARGET_SCOPE_SWITCH = new int[ReflectionTargetScope.allValues().length];

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_CLASSES.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError11) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_PUBLIC_METHODS.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError10) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_PUBLIC_FIELDS.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError9) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_METHODS_OF_CLASS.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_INSTANCE_FIELDS_OF_CLASS.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_FIELDS_OF_CLASS.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_STATIC_METHODS_ACCESSIBLE_FROM_CLASS.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_STATIC_FIELDS_ACCESSIBLE_FROM_CLASS.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS.ordinal()] = 10;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.ALL_LIST_RESOUCE_BUNDLE.ordinal()] = 11;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            TARGET_SCOPE_SWITCH[ReflectionTargetScope.NONE.ordinal()] = 12;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ReflectionKindSwitchMap() {
    }
}
