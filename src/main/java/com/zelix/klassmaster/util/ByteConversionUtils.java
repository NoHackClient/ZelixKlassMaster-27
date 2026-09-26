package com.zelix.klassmaster.util;

public class ByteConversionUtils {
    public static final int lowByte(int ba) {
        return ba & 0xFF;
    }

    public static byte[] intToBytes(int ba) {
        return new byte[]{(byte) (ba >>> 24), (byte) (ba >>> 16), (byte) (ba >>> 8), (byte) ba};
    }

    public static byte[] longToBytes(long ba) {
        return new byte[]{
                (byte) (ba >>> 56), (byte) (ba >>> 48), (byte) (ba >>> 40), (byte) (ba >>> 32), (byte) (ba >>> 24), (byte) (ba >>> 16), (byte) (ba >>> 8), (byte) ba
        };
    }

    public static final int toUnsignedByte(int ba) {
        return ba < 0 ? 256 + ba : ba;
    }

    public static final int bytesToUnsignedShort(int ba, int bb) {
        return (ba & 0xFF) << 8 | bb & 0xFF;
    }

    public static long bytesToLong(byte[] ba) {
        return (ba[0] & 255L) << 56
                | (ba[1] & 255L) << 48
                | (ba[2] & 255L) << 40
                | (ba[3] & 255L) << 32
                | (ba[4] & 255L) << 24
                | (ba[5] & 255L) << 16
                | (ba[6] & 255L) << 8
                | ba[7] & 255L;
    }

    public static final int highByte(int ba) {
        return (ba & 0xFF00) >> 8;
    }

    private ByteConversionUtils() {
    }
}
