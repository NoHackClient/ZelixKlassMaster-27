package com.zelix.klassmaster.classfile.constpool;

public enum ConstantPoolTag {
    UTF8(1),
    INTEGER(3),
    FLOAT(4),
    LONG(5),
    DOUBLE(6),
    CLASS(7),
    STRING(8),
    FIELDREF(9),
    METHODREF(10),
    INTERFACE_METHODREF(11),
    NAME_AND_TYPE(12),
    METHOD_HANDLE(15),
    METHOD_TYPE(16),
    CONSTANT_DYNAMIC(17),
    INVOKE_DYNAMIC(18),
    MODULE(19),
    PACKAGE(20),
    NULL(0);

    private static int flowKey;
    public static final ConstantPoolTag[] ALL_TAGS = new ConstantPoolTag[]{
            UTF8,
            INTEGER,
            FLOAT,
            LONG,
            DOUBLE,
            CLASS,
            STRING,
            FIELDREF,
            METHODREF,
            INTERFACE_METHODREF,
            NAME_AND_TYPE,
            METHOD_HANDLE,
            METHOD_TYPE,
            CONSTANT_DYNAMIC,
            INVOKE_DYNAMIC,
            MODULE,
            PACKAGE,
            NULL
    };
    public final int tagValue;

    public static int getFlowSeed() {
        return getFlowKey() == 0 ? 50 : 0;
    }

    public static void setFlowKey() {
        flowKey = 89;
    }

    ConstantPoolTag(int tagValue) {
        this.tagValue = tagValue;
    }

    public static int getFlowKey() {
        return flowKey;
    }

    public static ConstantPoolTag[] getAllTags() {
        return ALL_TAGS.clone();
    }

    public int getTagValue() {
        return this.tagValue;
    }

    static {
        if (getFlowSeed() != 0) {
            setFlowKey();
        }
    }
}
