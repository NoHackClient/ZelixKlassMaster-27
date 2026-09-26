package com.zelix.klassmaster.classfile.insn;

public enum InstructionUsageClearFlag {
    CLEAR_USAGE_JUMP_DESTINATION(-2),
    CLEAR_USAGE_LOCAL_VARIABLE(-3),
    CLEAR_USAGE_STACK_MAP(-5),
    CLEAR_USAGE_LINE_NUMBER(-9),
    CLEAR_USAGE_ANNOTATION(-17),
    CLEAR_USAGE_EXCEPTION(-129),
    CLEAR_USAGE_EXCEPTION_TRY_START(-257),
    CLEAR_USAGE_EXCEPTION_TRY_END(-513),
    CLEAR_USAGE_EXCEPTION_HANDLER(-1025),
    CLEAR_USAGE_MARKER(-8193),
    CLEAR_USAGE_IS_VISIBLE(-16385),
    CLEAR_USAGE_IS_FIXED(-32769);

    public final int mask;

    InstructionUsageClearFlag(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return this.mask;
    }
}
