package com.zelix.klassmaster.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClassFileInputStream extends DataInputStream {
    private static String b;
    private static String digestAlgorithm;
    private static long readChunkSize;
    public ByteArrayInputStream byteStream;
    public byte[] digest;

    public byte[] getDigest() {
        return this.digest != null ? this.digest.clone() : null;
    }

    @Override
    public void reset() {
        this.byteStream.reset();
    }

    public static ClassFileInputStream fromStream(InputStream inputStream1, int ba) throws IOException {
        Boolean boolean1 = true;
        return fromStream(inputStream1, ba, boolean1);
    }

    public static ClassFileInputStream fromBytes(byte[] ba, boolean bl) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ba);
        return new ClassFileInputStream(byteArrayInputStream, ba, bl);
    }

    public static String Q() {
        return b;
    }

    public static ClassFileInputStream fromStream(InputStream inputStream1, int ba, Boolean boolean1) throws IOException {
        try {
            byte[] bb = new byte[ba];
            int bc = 0;

            int bd;
            while (bc < ba && (bd = inputStream1.read(bb, bc, Math.min((int) readChunkSize, ba - bc))) != -1) {
                bc += bd;
            }

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bb);
            return new ClassFileInputStream(byteArrayInputStream, bb, boolean1);
        } finally {
            inputStream1.close();
        }
    }

    public static void n() {
        b = "Lz93oc";
    }

    private ClassFileInputStream(ByteArrayInputStream byteArrayInputStream, byte[] ba, boolean bl) {
        super(byteArrayInputStream);
        this.byteStream = byteArrayInputStream;
        if (bl) {
            try {
                MessageDigest messageDigest1 = MessageDigest.getInstance(digestAlgorithm);
                this.digest = messageDigest1.digest(ba);
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                this.digest = new byte[0];
            }
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        if (Q() != null) {
            n();
        }

        digestAlgorithm = "SHA-1";
        readChunkSize = -5685789525918343168L;
    }
}
