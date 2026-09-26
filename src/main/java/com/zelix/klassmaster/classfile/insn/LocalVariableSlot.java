package com.zelix.klassmaster.classfile.insn;

public class LocalVariableSlot implements LocalVariableIndex {
    private boolean wideFirstHalf;
    private boolean wideSecondHalf;
    private int index = -1;
    public final int insertionKind;

    public void clearWideFirstHalf() {
        this.wideFirstHalf = false;
    }

    public void markWideSecondHalf() {
        this.wideSecondHalf = true;
    }

    public LocalVariableSlot(int ba) {
        this(ba, 0);
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    public void markWideFirstHalf() {
        this.wideFirstHalf = true;
    }

    @Override
    public boolean isInserted() {
        return this.insertionKind != 0;
    }

    public boolean needsWideIndex() {
        return this.index > 255;
    }

    public LocalVariableSlot(int ba, boolean bl, boolean bl1) {
        this(ba, bl, bl1, 0);
    }

    public void clearWideSecondHalf() {
        this.wideSecondHalf = false;
    }

    public LocalVariableSlot(int index, int insertionKind) {
        this.index = index;
        this.insertionKind = insertionKind;
    }

    @Override
    public int replaceIndex(int index) {
        int bb = this.index;
        this.index = index;
        return bb;
    }

    @Override
    public boolean isWideSecondHalf() {
        return this.wideSecondHalf;
    }

    public LocalVariableSlot(int index, boolean wideFirstHalf, boolean wideSecondHalf, int insertionKind) {
        this.index = index;
        this.insertionKind = insertionKind;
        this.wideFirstHalf = wideFirstHalf;
        this.wideSecondHalf = wideSecondHalf;
    }

    @Override
    public boolean isWideFirstHalf() {
        return this.wideFirstHalf;
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
