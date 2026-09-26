package com.zelix.klassmaster.license;

public class ObfuscatedLongDecoder {
    public static long decodeLong(String string) {
        char[] ba = string.toCharArray();
        char[] bb = new char[ba.length];

        for (int i = 0; i < ba.length; i++) {
            char bd = ba[i];
            if (bd >= '0' && bd <= '9') {
                if (bd > '0') {
                    bd = (char) (':' - bd + 48);
                }
            } else if (bd >= 'A' && bd <= 'J') {
                bd = (char) (bd - 17);
            } else if (bd >= 'K' && bd <= 'T') {
                bd = (char) (bd - 27);
            } else if (bd >= 'U' && bd <= 'Z') {
                bd = (char) (bd - '%');
            } else if (bd >= 'a' && bd <= 'd') {
                bd = (char) (bd - '+');
            } else if (bd >= 'e' && bd <= 'n') {
                bd = (char) (bd - '5');
            } else if (bd >= 'o' && bd <= 'x') {
                bd = (char) (bd - '?');
            }

            bb[i] = bd;
        }

        return Long.parseLong(new String(bb));
    }

    private ObfuscatedLongDecoder() {
    }
}
