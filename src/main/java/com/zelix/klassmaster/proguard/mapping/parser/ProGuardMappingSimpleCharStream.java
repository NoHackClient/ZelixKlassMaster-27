package com.zelix.klassmaster.proguard.mapping.parser;

import java.io.IOException;
import java.io.Reader;

public class ProGuardMappingSimpleCharStream {
    public static int bufsize;
    public static int tokenBegin;
    public static char[] buffer;
    public static Reader inputStream;
    public static int[] bufcolumn;
    public static int[] bufline;
    public static int available;
    private static final String SECOND_CONSTRUCTOR_ERROR = "\n   ERROR: Second call to the constructor of a static SimpleCharStream.\n       You must either use ReInit() or set the JavaCC option STATIC to false\n       during the generation of this class.";
    public static int bufpos = -1;
    public static int column = 0;
    public static int line = 1;
    public static boolean prevCharIsCR = false;
    public static boolean prevCharIsLF = false;
    public static int maxNextCharInd = 0;
    public static int inBuf = 0;
    public static int tabSize = 8;

    public static int getColumn() {
        return bufline[tokenBegin];
    }

    public static void ExpandBuff(boolean bl) {
        char[] ba = new char[bufsize + 2048];
        int[] bb = new int[bufsize + 2048];
        int[] bc = new int[bufsize + 2048];

        try {
            if (bl) {
                System.arraycopy(buffer, tokenBegin, ba, 0, bufsize - tokenBegin);
                System.arraycopy(buffer, 0, ba, bufsize - tokenBegin, bufpos);
                buffer = ba;
                System.arraycopy(bufline, tokenBegin, bb, 0, bufsize - tokenBegin);
                System.arraycopy(bufline, 0, bb, bufsize - tokenBegin, bufpos);
                bufline = bb;
                System.arraycopy(bufcolumn, tokenBegin, bc, 0, bufsize - tokenBegin);
                System.arraycopy(bufcolumn, 0, bc, bufsize - tokenBegin, bufpos);
                bufcolumn = bc;
                maxNextCharInd = bufpos = bufpos + (bufsize - tokenBegin);
            } else {
                System.arraycopy(buffer, tokenBegin, ba, 0, bufsize - tokenBegin);
                buffer = ba;
                System.arraycopy(bufline, tokenBegin, bb, 0, bufsize - tokenBegin);
                bufline = bb;
                System.arraycopy(bufcolumn, tokenBegin, bc, 0, bufsize - tokenBegin);
                bufcolumn = bc;
                maxNextCharInd = bufpos = bufpos - tokenBegin;
            }
        } catch (Throwable throwable) {
            throw new Error(throwable.getMessage());
        }

        bufsize += 2048;
        available = bufsize;
        tokenBegin = 0;
    }

    public static int getLine() {
        return bufcolumn[tokenBegin];
    }

    public static char readChar() throws IOException {
        if (inBuf > 0) {
            inBuf--;
            if (++bufpos == bufsize) {
                bufpos = 0;
            }

            return buffer[bufpos];
        } else {
            char[] bb;
            if (++bufpos >= maxNextCharInd) {
                FillBuff();
                bb = buffer;
            } else {
                bb = buffer;
            }

            char ba = bb[bufpos];
            UpdateLineColumn(ba);
            return ba;
        }
    }

    public void ReInit(Reader reader1) {
        this.ReInit_v2(reader1);
    }

    public static char[] GetSuffix(int ba) {
        char[] bb = new char[ba];
        if (bufpos + 1 >= ba) {
            System.arraycopy(buffer, bufpos - ba + 1, bb, 0, ba);
        } else {
            System.arraycopy(buffer, bufsize - (ba - bufpos - 1), bb, 0, ba - bufpos - 1);
            System.arraycopy(buffer, 0, bb, ba - bufpos - 1, bufpos + 1);
        }

        return bb;
    }

    public static int getEndColumn() {
        return bufline[bufpos];
    }

    public static char BeginToken() throws IOException {
        tokenBegin = -1;
        char ba = readChar();
        tokenBegin = bufpos;
        return ba;
    }

    public void ReInit_v2(Reader reader1) {
        int ba;
        label16:
        {
            inputStream = reader1;
            line = 1;
            column = 1 - 1;
            if (buffer != null) {
                if (4096 == buffer.length) {
                    ba = ((prevCharIsCR = false) ? 1 : 0);
                    break label16;
                }

                ba = bufsize = 4096;
            } else {
                ba = bufsize = 4096;
            }

            available = ba;
            buffer = new char[4096];
            bufline = new int[4096];
            bufcolumn = new int[4096];
            ba = ((prevCharIsCR = false) ? 1 : 0);
        }

        prevCharIsLF = ba != 0;
        maxNextCharInd = 0;
        inBuf = 0;
        tokenBegin = 0;
        bufpos = -1;
    }

    public static String GetImage() {
        return bufpos >= tokenBegin
                ? new String(buffer, tokenBegin, bufpos - tokenBegin + 1)
                : new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
    }

    public ProGuardMappingSimpleCharStream(Reader reader1) {
        this(reader1, 1, 1, 4096);
    }

    public static int getEndLine() {
        return bufcolumn[bufpos];
    }

    public static void FillBuff() throws IOException {
        if (maxNextCharInd == available) {
            if (available == bufsize) {
                if (tokenBegin > 2048) {
                    maxNextCharInd = 0;
                    bufpos = 0;
                    available = tokenBegin;
                } else if (tokenBegin < 0) {
                    maxNextCharInd = 0;
                    bufpos = 0;
                } else {
                    ExpandBuff(false);
                }
            } else if (available > tokenBegin) {
                available = bufsize;
            } else if (tokenBegin - available < 2048) {
                ExpandBuff(true);
            } else {
                available = tokenBegin;
            }
        }

        try {
            int ba;
            if ((ba = inputStream.read(buffer, maxNextCharInd, available - maxNextCharInd)) == -1) {
                inputStream.close();
                throw new IOException();
            }

            maxNextCharInd += ba;
        } catch (IOException iOException) {
            bufpos--;
            backup(0);
            if (tokenBegin == -1) {
                tokenBegin = bufpos;
            }

            throw iOException;
        }
    }

    public static void backup(int ba) {
        inBuf += ba;
        if ((bufpos -= ba) < 0) {
            bufpos = bufpos + bufsize;
        }
    }

    public ProGuardMappingSimpleCharStream(Reader reader1, int ba, int bb, int bc) {
        if (inputStream != null) {
            throw new Error(SECOND_CONSTRUCTOR_ERROR);
        }

        inputStream = reader1;
        line = 1;
        column = 1 - 1;
        bufsize = 4096;
        available = 4096;
        buffer = new char[4096];
        bufline = new int[4096];
        bufcolumn = new int[4096];
    }

    public static void UpdateLineColumn(int ba) {
        column++;
        if (prevCharIsLF) {
            prevCharIsLF = false;
            int bd = line;
            column = 1;
            line = bd + 1;
        } else if (prevCharIsCR) {
            prevCharIsCR = false;
            if (ba == 10) {
                prevCharIsLF = true;
            } else {
                int bb = line;
                column = 1;
                line = bb + 1;
            }
        }

        int[] bc;
        switch (ba) {
            case 9:
                column--;
                column = column + (tabSize - column % tabSize);
                bc = bufline;
                break;
            case 10:
                prevCharIsLF = true;
                bc = bufline;
                break;
            case 11:
            case 12:
            default:
                bc = bufline;
                break;
            case 13:
                prevCharIsCR = true;
                bc = bufline;
        }

        bc[bufpos] = line;
        bufcolumn[bufpos] = column;
    }
}
