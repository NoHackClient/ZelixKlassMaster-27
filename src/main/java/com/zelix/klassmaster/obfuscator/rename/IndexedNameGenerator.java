package com.zelix.klassmaster.obfuscator.rename;

import java.util.ArrayList;

public class IndexedNameGenerator extends JavaKeywords {
    private ArrayList nameCache = new ArrayList(1024);
    public char[] charBuffer = new char[6];
    public int caseMode = 0;
    public String prefix;
    public final int firstCharRadix;
    public final int subsequentCharRadix;

    public final char getSubsequentChar(int ba) {
        char bb;
        if (this.caseMode == 0) {
            if (ba <= 25) {
                bb = (char) (97 + ba);
            } else if (ba <= 51) {
                bb = (char) (39 + ba);
            } else {
                bb = (char) (ba - 4);
            }
        } else if (ba <= 25) {
            bb = (char) (97 + ba);
        } else {
            bb = (char) (22 + ba);
        }

        return bb;
    }

    public final String getNonKeywordName(int ba) {
        String string;
        do {
            string = this.getNameAt(ba);
        } while (string == null);

        return string;
    }

    public final String getNameAt(int ba) {
        if (ba < this.nameCache.size()) {
            return (String) this.nameCache.get(ba);
        }

        int bb = 0;
        this.charBuffer[bb++] = this.getFirstChar(ba % this.firstCharRadix);

        for (int i = ba / this.firstCharRadix; i > 0; i /= this.subsequentCharRadix) {
            this.charBuffer[bb++] = this.getSubsequentChar(i % this.subsequentCharRadix);
        }

        String string = new String(this.charBuffer, 0, bb);
        if (this.prefix != null) {
            string = this.prefix + string;
        }

        if (JavaKeywords.RESERVED_WORDS.contains(string)) {
            string = null;
        }

        if (ba == this.nameCache.size()) {
            this.nameCache.add(string);
        }

        return string;
    }

    public IndexedNameGenerator() {
        this(null);
    }

    public IndexedNameGenerator(String string) {
        this.prefix = string;
        this.firstCharRadix = 52;
        this.subsequentCharRadix = 62;
    }

    public final char getFirstChar(int ba) {
        return ba <= 25 ? (char) (97 + ba) : (char) (39 + ba);
    }
}
