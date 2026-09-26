package com.zelix.klassmaster.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Digester {
    public byte[] digest;
    public final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

    public Sha256Digester(String string) throws IOException, NoSuchAlgorithmException {
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(string), 32768);
            this.digestStream(bufferedInputStream);
        } catch (Throwable throwable) {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            throw throwable;
        }
        bufferedInputStream.close();
    }

    public byte[] getDigest() {
        return this.digest.clone();
    }

    public void digestStream(BufferedInputStream bufferedInputStream) throws IOException {
        byte[] ba = new byte[32768];
        int bd = bufferedInputStream.read(ba);

        while (true) {
            int bb = bd;
            if (bb > 0) {
                int bc = bb;
                this.messageDigest.update(ba, 0, bc);
            }

            if (bb == -1) {
                bufferedInputStream.close();
                this.digest = this.messageDigest.digest();
                return;
            }

            bd = bufferedInputStream.read(ba);
        }
    }

    public String getHexDigest() {
        byte[] digest = this.getDigest();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < digest.length; i++) {
            String string = Integer.toHexString(digest[i] & 255);
            if (string.length() == 1) {
                string = "0" + string;
            }

            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

    public Sha256Digester(BufferedInputStream bufferedInputStream) throws IOException, NoSuchAlgorithmException {
        this.digestStream(bufferedInputStream);
    }
}
