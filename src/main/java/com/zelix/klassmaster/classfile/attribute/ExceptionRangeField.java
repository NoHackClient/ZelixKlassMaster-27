package com.zelix.klassmaster.classfile.attribute;

public enum ExceptionRangeField {
    START_PC,
    END_PC,
    HANDLER_PC;

    public static final ExceptionRangeField[] ALL = new ExceptionRangeField[]{START_PC, END_PC, HANDLER_PC};

    public static ExceptionRangeField[] allValues() {
        return ALL.clone();
    }
}
