package com.zelix.klassmaster.script.parser;

import java.io.IOException;
import java.io.Reader;

public class ZkmScriptSimpleCharStream {
    public int tokenBegin;
    public int bufpos = -1;
    public int column = 0;
    public int line = 1;
    public boolean prevCharIsCR = false;
    public boolean prevCharIsLF = false;
    public int maxNextCharInd = 0;
    public int inBuf = 0;
    public int tabSize = 8;
    public Reader inputStream;
    public int bufsize;
    public int available;
    public char[] buffer;
    public int[] bufline;
    public int[] bufcolumn;

    public void ExpandBuff(boolean bl) {
        char[] ba = new char[this.bufsize + 2048];
        int[] bb = new int[this.bufsize + 2048];
        int[] bc = new int[this.bufsize + 2048];

        try {
            if (bl) {
                System.arraycopy(this.buffer, this.tokenBegin, ba, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.buffer, 0, ba, this.bufsize - this.tokenBegin, this.bufpos);
                this.buffer = ba;
                System.arraycopy(this.bufline, this.tokenBegin, bb, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufline, 0, bb, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufline = bb;
                System.arraycopy(this.bufcolumn, this.tokenBegin, bc, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufcolumn, 0, bc, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufcolumn = bc;
                this.maxNextCharInd = this.bufpos = this.bufpos + (this.bufsize - this.tokenBegin);
            } else {
                System.arraycopy(this.buffer, this.tokenBegin, ba, 0, this.bufsize - this.tokenBegin);
                this.buffer = ba;
                System.arraycopy(this.bufline, this.tokenBegin, bb, 0, this.bufsize - this.tokenBegin);
                this.bufline = bb;
                System.arraycopy(this.bufcolumn, this.tokenBegin, bc, 0, this.bufsize - this.tokenBegin);
                this.bufcolumn = bc;
                this.maxNextCharInd = this.bufpos = this.bufpos - this.tokenBegin;
            }
        } catch (Throwable throwable) {
            throw new Error(throwable.getMessage());
        }

        this.bufsize += 2048;
        this.available = this.bufsize;
        this.tokenBegin = 0;
    }

    public int getColumn() {
        return this.bufline[this.bufpos];
    }

    public int getLine() {
        return this.bufline[this.tokenBegin];
    }

    public String GetImage() {
        return this.bufpos >= this.tokenBegin
                ? new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1)
                : new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
    }

    public char BeginToken() throws IOException {
        this.tokenBegin = -1;
        char ba = this.readChar();
        this.tokenBegin = this.bufpos;
        return ba;
    }

    public void FillBuff() throws IOException {
        if (this.maxNextCharInd == this.available) {
            if (this.available == this.bufsize) {
                if (this.tokenBegin > 2048) {
                    this.maxNextCharInd = 0;
                    this.bufpos = 0;
                    this.available = this.tokenBegin;
                } else if (this.tokenBegin < 0) {
                    this.maxNextCharInd = 0;
                    this.bufpos = 0;
                } else {
                    this.ExpandBuff(false);
                }
            } else if (this.available > this.tokenBegin) {
                this.available = this.bufsize;
            } else if (this.tokenBegin - this.available < 2048) {
                this.ExpandBuff(true);
            } else {
                this.available = this.tokenBegin;
            }
        }

        try {
            int ba;
            if ((ba = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1) {
                this.inputStream.close();
                throw new IOException();
            }

            this.maxNextCharInd += ba;
        } catch (IOException iOException) {
            this.bufpos--;
            this.backup(0);
            if (this.tokenBegin == -1) {
                this.tokenBegin = this.bufpos;
            }

            throw iOException;
        }
    }

    public char[] GetSuffix(int ba) {
        char[] bb = new char[ba];
        if (this.bufpos + 1 >= ba) {
            System.arraycopy(this.buffer, this.bufpos - ba + 1, bb, 0, ba);
        } else {
            System.arraycopy(this.buffer, this.bufsize - (ba - this.bufpos - 1), bb, 0, ba - this.bufpos - 1);
            System.arraycopy(this.buffer, 0, bb, ba - this.bufpos - 1, this.bufpos + 1);
        }

        return bb;
    }

    public void backup(int ba) {
        this.inBuf += ba;
        if ((this.bufpos -= ba) < 0) {
            this.bufpos = this.bufpos + this.bufsize;
        }
    }

    public int getEndColumn() {
        return this.bufcolumn[this.bufpos];
    }

    public ZkmScriptSimpleCharStream(Reader reader1) {
        this(reader1, 1, 1, 4096);
    }

    public void UpdateLineColumn(int ba) {
        this.column++;
        if (this.prevCharIsLF) {
            this.prevCharIsLF = false;
            int line = this.line;
            this.column = 1;
            this.line = line + 1;
        } else if (this.prevCharIsCR) {
            this.prevCharIsCR = false;
            if (ba == 10) {
                this.prevCharIsLF = true;
            } else {
                int bb = this.line;
                this.column = 1;
                this.line = bb + 1;
            }
        }

        int[] bc;
        switch (ba) {
            case 9:
                this.column--;
                this.column = this.column + (this.tabSize - this.column % this.tabSize);
                bc = this.bufline;
                break;
            case 10:
                this.prevCharIsLF = true;
                bc = this.bufline;
                break;
            case 11:
            case 12:
            default:
                bc = this.bufline;
                break;
            case 13:
                this.prevCharIsCR = true;
                bc = this.bufline;
        }

        bc[this.bufpos] = this.line;
        this.bufcolumn[this.bufpos] = this.column;
    }

    public char readChar() throws IOException {
        if (this.inBuf > 0) {
            this.inBuf--;
            if (++this.bufpos == this.bufsize) {
                this.bufpos = 0;
            }

            return this.buffer[this.bufpos];
        } else {
            if (++this.bufpos >= this.maxNextCharInd) {
                this.FillBuff();
            }

            char ba = this.buffer[this.bufpos];
            this.UpdateLineColumn(ba);
            return ba;
        }
    }

    public int getEndLine() {
        return this.bufcolumn[this.tokenBegin];
    }

    public ZkmScriptSimpleCharStream(Reader reader1, int ba, int bb, int bc) {
        this.inputStream = reader1;
        this.line = 1;
        this.column = 1 - 1;
        this.available = this.bufsize = 4096;
        this.buffer = new char[4096];
        this.bufline = new int[4096];
        this.bufcolumn = new int[4096];
    }
}
