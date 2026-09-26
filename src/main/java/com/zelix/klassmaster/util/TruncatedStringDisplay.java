package com.zelix.klassmaster.util;

public class TruncatedStringDisplay {
    private static int[] staticIntArray;
    private int maxLength;
    public final String text;
    private static long defaultMaxLength;

    public static void setStaticIntArray(int[] ba) {
        staticIntArray = ba;
    }

    public TruncatedStringDisplay(String string) {
        this.maxLength = (int) defaultMaxLength;
        this.text = string;
    }

    public static int[] getStaticIntArray() {
        return staticIntArray;
    }

    @Override
    public String toString() {
        return this.text != null ? ZkmUtils.escapeAndTruncate(this.text, this.maxLength) : this.text;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        if (getStaticIntArray() != null) {
            setStaticIntArray(new int[3]);
        }

        defaultMaxLength = 6883329835150671871L;
    }
}
