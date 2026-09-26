package com.zelix.klassmaster.obfuscator.reflection;

public enum MemberScope {
    INSTANCE,
    STATIC,
    BOTH;

    public static final MemberScope[] ALL_VALUES = new MemberScope[]{INSTANCE, STATIC, BOTH};

    public static MemberScope[] allValues() {
        return ALL_VALUES.clone();
    }
}
