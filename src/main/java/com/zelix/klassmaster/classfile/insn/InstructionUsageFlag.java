package com.zelix.klassmaster.classfile.insn;

public enum InstructionUsageFlag {
    SET_USAGE_NONE(0),
    SET_USAGE_JUMP_DESTINATION(1),
    SET_USAGE_LOCAL_VARIABLE(2),
    SET_USAGE_STACK_MAP(4),
    SET_USAGE_LINE_NUMBER(8),
    SET_USAGE_ANNOTATION(16),
    SET_USAGE_EXCEPTION(128),
    SET_USAGE_EXCEPTION_TRY_START(256),
    SET_USAGE_EXCEPTION_TRY_END(512),
    SET_USAGE_EXCEPTION_HANDLER(1024),
    SET_USAGE_MARKER_X(4096),
    SET_USAGE_MARKER(8192),
    SET_USAGE_IS_VISIBLE(16384),
    SET_USAGE_IS_FIXED(32768);

    private final int bit;

    InstructionUsageFlag(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return this.bit;
    }
}
