package com.zelix.klassmaster.classfile.attribute;

public class ExceptionTablePcSwitchMap {
    public static final int[] RANGE_FIELD_SWITCH = new int[ExceptionRangeField.allValues().length];

    static {
        try {
            RANGE_FIELD_SWITCH[ExceptionRangeField.START_PC.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            RANGE_FIELD_SWITCH[ExceptionRangeField.END_PC.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            RANGE_FIELD_SWITCH[ExceptionRangeField.HANDLER_PC.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ExceptionTablePcSwitchMap() {
    }
}
