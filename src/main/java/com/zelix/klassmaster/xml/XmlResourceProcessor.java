package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class XmlResourceProcessor {
    private static final String[] ASCII_CHAR_STRINGS = new String[]{
            "\u0000",
            "\u0001",
            "\u0002",
            "\u0003",
            "\u0004",
            "\u0005",
            "\u0006",
            "\u0007",
            "\b",
            "\t",
            "\n",
            "\u000b",
            "\f",
            "\r",
            "\u000e",
            "\u000f",
            "\u0010",
            "\u0011",
            "\u0012",
            "\u0013",
            "\u0014",
            "\u0015",
            "\u0016",
            "\u0017",
            "\u0018",
            "\u0019",
            "\u001a",
            "\u001b",
            "\u001c",
            "\u001d",
            "\u001e",
            "\u001f",
            " ",
            "!",
            "\"",
            "#",
            "$",
            "%",
            "&",
            "'",
            "(",
            ")",
            "*",
            "+",
            ",",
            "-",
            ".",
            "/",
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            ":",
            ";",
            "<",
            "=",
            ">",
            "?",
            "@",
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z",
            "[",
            "\\",
            "]",
            "^",
            "_",
            "`",
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q",
            "r",
            "s",
            "t",
            "u",
            "v",
            "w",
            "x",
            "y",
            "z",
            "{",
            "|",
            "}",
            "~",
            "\u007f"
    };
    public String currentTagName;
    public XmlElementNode currentElement;
    public Map methodReferences;
    public Map classReferences;
    public TwoKeyMap methodNameReferences;
    public Map fieldReferences;
    private int parserState = 1;
    public int attributeState = 1;
    private char quoteChar = '~';
    public StringBuilder characterData = new StringBuilder();
    public StringBuilder tagNameBuffer = new StringBuilder();
    public boolean tagNameStarted = false;
    public boolean tagNameComplete = false;
    private StringBuilder tokenBuffer = new StringBuilder();
    public StringBuilder processingInstruction = new StringBuilder();
    private boolean hasNonWhitespaceText = false;
    public StringBuilder pendingWhitespace = new StringBuilder();
    public final XmlContentHandler contentHandler;
    public final String resourceName;

    public XmlResourceProcessor(
            InputStream inputStream1,
            OutputStream outputStream,
            XmlContentHandler xmlContentHandler,
            String string,
            int ba,
            int bb,
            int bc,
            String string1,
            boolean bl
    ) throws ZkmException, IOException {
        String string2;
        if (string == null) {
            string2 = "UTF-8";
        } else {
            string2 = string;
        }

        ZkmFileUtils.readBytes(inputStream1, ba);
        PushbackReader pushbackReader = new PushbackReader(new BufferedReader(new InputStreamReader(inputStream1, string2)), 7);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.contentHandler = xmlContentHandler;
        this.resourceName = string1;
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, string2)));
        ArrayList arrayList = new ArrayList();
        this.parse(pushbackReader, arrayList, printWriter, bl);
        this.flushOutput(arrayList, printWriter);
        printWriter.close();
        byte[] bd = byteArrayOutputStream.toByteArray();
        bd = prependByteOrderMark(bd, bb, bc);
        outputStream.write(bd);
        outputStream.flush();
    }

    public void parse(PushbackReader pushbackReader, List list1, PrintWriter printWriter, boolean bl) throws ZkmException, IOException {
        boolean bl1 = false;
        int ba = 0;

        while (ba != -1 && (ba = pushbackReader.read()) != -1) {
            char bb = (char) ba;
            boolean bl2 = bb == ' ' || bb == '\t' || bb == '\n' || bb == '\r';
            label490:
            switch (this.parserState) {
                case 1:
                    if (bb == '<') {
                        this.hasNonWhitespaceText = false;
                        String string = this.characterData.toString();
                        if (string.length() > 0) {
                            this.handleCharacterData(string, this.currentTagName, list1, printWriter);
                            this.characterData.setLength(0);
                        }

                        if (list1 != null) {
                            list1.add(this.pendingWhitespace.toString());
                            this.pendingWhitespace.setLength(0);
                            list1.add(ASCII_CHAR_STRINGS[bb]);
                        }

                        if ((ba = pushbackReader.read()) != -1) {
                            char bn = (char) ba;
                            if (bn == '!') {
                                if (list1 != null) {
                                    list1.add(ASCII_CHAR_STRINGS[bn]);
                                }

                                if ((ba = pushbackReader.read()) != -1) {
                                    char bd = (char) ba;
                                    if (bd == '-') {
                                        if ((ba = pushbackReader.read()) != -1) {
                                            char be = (char) ba;
                                            if (be == '-') {
                                                if (list1 != null) {
                                                    list1.add("--");
                                                }

                                                StringBuilder stringBuilder1 = new StringBuilder();
                                                this.readComment(stringBuilder1, pushbackReader);
                                                if (list1 != null) {
                                                    if (!bl) {
                                                        list1.add(stringBuilder1.toString());
                                                    } else {
                                                        list1.remove(list1.size() - 1);
                                                        list1.remove(list1.size() - 1);
                                                        list1.remove(list1.size() - 1);
                                                    }
                                                }
                                            } else {
                                                pushbackReader.unread(be);
                                                pushbackReader.unread(bd);
                                                this.setParserState(3);
                                            }
                                        } else if (list1 != null) {
                                            list1.add(ASCII_CHAR_STRINGS[bd]);
                                        }
                                    } else if (bd == '[') {
                                        if ((ba = pushbackReader.read()) != -1) {
                                            char bp = (char) ba;
                                            if (bp == 'C') {
                                                if ((ba = pushbackReader.read()) != -1) {
                                                    char br = (char) ba;
                                                    if (br == 'D') {
                                                        if ((ba = pushbackReader.read()) != -1) {
                                                            char bf = (char) ba;
                                                            if (bf == 'A') {
                                                                if ((ba = pushbackReader.read()) != -1) {
                                                                    char bg = (char) ba;
                                                                    if (bg == 'T') {
                                                                        if ((ba = pushbackReader.read()) != -1) {
                                                                            char bh = (char) ba;
                                                                            if (bh == 'A') {
                                                                                if ((ba = pushbackReader.read()) != -1) {
                                                                                    char bi = (char) ba;
                                                                                    if (bi == '[') {
                                                                                        XmlResourceProcessor xmlResourceProcessor1;
                                                                                        byte ck;
                                                                                        if (list1 != null) {
                                                                                            list1.add("[CDATA[");
                                                                                            xmlResourceProcessor1 = this;
                                                                                            long cd = 85343572972665L;
                                                                                            ck = 6;
                                                                                        } else {
                                                                                            xmlResourceProcessor1 = this;
                                                                                            long ce = 85343572972665L;
                                                                                            ck = 6;
                                                                                        }

                                                                                        Integer integer = Integer.valueOf(ck);
                                                                                        xmlResourceProcessor1.setParserState(integer);
                                                                                    } else {
                                                                                        pushbackReader.unread(bi);
                                                                                        pushbackReader.unread(bh);
                                                                                        pushbackReader.unread(bg);
                                                                                        pushbackReader.unread(bf);
                                                                                        pushbackReader.unread(br);
                                                                                        pushbackReader.unread(bp);
                                                                                        pushbackReader.unread(bd);
                                                                                        this.setParserState(3);
                                                                                    }
                                                                                } else if (list1 != null) {
                                                                                    list1.add("[CDATA");
                                                                                }
                                                                            } else {
                                                                                pushbackReader.unread(bh);
                                                                                pushbackReader.unread(bg);
                                                                                pushbackReader.unread(bf);
                                                                                pushbackReader.unread(br);
                                                                                pushbackReader.unread(bp);
                                                                                pushbackReader.unread(bd);
                                                                                this.setParserState(3);
                                                                            }
                                                                        } else if (list1 != null) {
                                                                            list1.add("[CDAT");
                                                                        }
                                                                    } else {
                                                                        pushbackReader.unread(bg);
                                                                        pushbackReader.unread(bf);
                                                                        pushbackReader.unread(br);
                                                                        pushbackReader.unread(bp);
                                                                        pushbackReader.unread(bd);
                                                                        this.setParserState(3);
                                                                    }
                                                                } else if (list1 != null) {
                                                                    list1.add("[CDA");
                                                                }
                                                            } else {
                                                                pushbackReader.unread(bf);
                                                                pushbackReader.unread(br);
                                                                pushbackReader.unread(bp);
                                                                pushbackReader.unread(bd);
                                                                this.setParserState(3);
                                                            }
                                                        } else if (list1 != null) {
                                                            list1.add("[CD");
                                                        }
                                                    } else {
                                                        pushbackReader.unread(br);
                                                        pushbackReader.unread(bp);
                                                        pushbackReader.unread(bd);
                                                        this.setParserState(3);
                                                    }
                                                } else if (list1 != null) {
                                                    list1.add("[C");
                                                }
                                            } else {
                                                pushbackReader.unread(bp);
                                                pushbackReader.unread(bd);
                                                this.setParserState(3);
                                            }
                                        } else if (list1 != null) {
                                            list1.add(bd);
                                        }
                                    } else if (bd == 'D') {
                                        if ((ba = pushbackReader.read()) != -1) {
                                            char bq = (char) ba;
                                            if (bq == 'O') {
                                                if ((ba = pushbackReader.read()) != -1) {
                                                    char bs = (char) ba;
                                                    if (bs == 'C') {
                                                        if ((ba = pushbackReader.read()) != -1) {
                                                            char bt = (char) ba;
                                                            if (bt == 'T') {
                                                                if ((ba = pushbackReader.read()) != -1) {
                                                                    char bu = (char) ba;
                                                                    if (bu == 'Y') {
                                                                        if ((ba = pushbackReader.read()) != -1) {
                                                                            char bv = (char) ba;
                                                                            if (bv == 'P') {
                                                                                if ((ba = pushbackReader.read()) != -1) {
                                                                                    char bw = (char) ba;
                                                                                    if (bw == 'E') {
                                                                                        if (list1 != null) {
                                                                                            list1.add("DOCTYPE");
                                                                                        }

                                                                                        this.setParserState(4);
                                                                                    } else {
                                                                                        pushbackReader.unread(bw);
                                                                                        pushbackReader.unread(bv);
                                                                                        pushbackReader.unread(bu);
                                                                                        pushbackReader.unread(bt);
                                                                                        pushbackReader.unread(bs);
                                                                                        pushbackReader.unread(bq);
                                                                                        pushbackReader.unread(bd);
                                                                                        this.setParserState(3);
                                                                                    }
                                                                                } else if (list1 != null) {
                                                                                    list1.add("DOCTYP");
                                                                                }
                                                                            } else {
                                                                                pushbackReader.unread(bv);
                                                                                pushbackReader.unread(bu);
                                                                                pushbackReader.unread(bt);
                                                                                pushbackReader.unread(bs);
                                                                                pushbackReader.unread(bq);
                                                                                pushbackReader.unread(bd);
                                                                                this.setParserState(3);
                                                                            }
                                                                        } else if (list1 != null) {
                                                                            list1.add("DOCTY");
                                                                        }
                                                                    } else {
                                                                        pushbackReader.unread(bu);
                                                                        pushbackReader.unread(bt);
                                                                        pushbackReader.unread(bs);
                                                                        pushbackReader.unread(bq);
                                                                        pushbackReader.unread(bd);
                                                                        this.setParserState(3);
                                                                    }
                                                                } else if (list1 != null) {
                                                                    list1.add("DOCT");
                                                                }
                                                            } else {
                                                                pushbackReader.unread(bt);
                                                                pushbackReader.unread(bs);
                                                                pushbackReader.unread(bq);
                                                                pushbackReader.unread(bd);
                                                                this.setParserState(3);
                                                            }
                                                        } else if (list1 != null) {
                                                            list1.add("DOC");
                                                        }
                                                    } else {
                                                        pushbackReader.unread(bs);
                                                        pushbackReader.unread(bq);
                                                        pushbackReader.unread(bd);
                                                        this.setParserState(3);
                                                    }
                                                } else if (list1 != null) {
                                                    list1.add("DO");
                                                }
                                            } else {
                                                pushbackReader.unread(bq);
                                                pushbackReader.unread(bd);
                                                this.setParserState(3);
                                            }
                                        } else if (list1 != null) {
                                            list1.add(ASCII_CHAR_STRINGS[bd]);
                                        }
                                    } else {
                                        pushbackReader.unread(bd);
                                        this.setParserState(3);
                                    }
                                }
                            } else if (bn == '?') {
                                if (list1 != null) {
                                    list1.add(ASCII_CHAR_STRINGS[bn]);
                                }

                                this.setParserState(2);
                            } else if (bn == '/') {
                                XmlResourceProcessor xmlResourceProcessor2;
                                byte ch;
                                if (list1 != null) {
                                    list1.add(ASCII_CHAR_STRINGS[bn]);
                                    xmlResourceProcessor2 = this;
                                    long cf = 85343572972665L;
                                    ch = 8;
                                } else {
                                    xmlResourceProcessor2 = this;
                                    long cg = 85343572972665L;
                                    ch = 8;
                                }

                                Integer integer1 = Integer.valueOf(ch);
                                xmlResourceProcessor2.setParserState(integer1);
                            } else {
                                pushbackReader.unread(bn);
                                this.setParserState(7);
                            }
                        }
                    } else if (bl2) {
                        if (!this.hasNonWhitespaceText) {
                            if (list1 != null) {
                                list1.add(ASCII_CHAR_STRINGS[bb]);
                            }
                        } else {
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append(bb);

                            do {
                                ba = pushbackReader.read();
                                if (ba != -1) {
                                    char bo = (char) ba;
                                    if (bo != ' ' && bo != '\t' && bo != '\n' && bo != '\r') {
                                        if (bo == '<') {
                                            pushbackReader.unread(bo);
                                            this.pendingWhitespace.setLength(0);
                                            this.pendingWhitespace.append(stringBuffer.toString());
                                        } else {
                                            stringBuffer.append(bo);
                                            this.characterData.append(stringBuffer);
                                        }
                                        break label490;
                                    }

                                    stringBuffer.append(bo);
                                } else {
                                    this.characterData.append(stringBuffer);
                                }
                            } while (ba != -1);
                        }
                    } else {
                        this.hasNonWhitespaceText = true;
                        this.characterData.append(bb);
                    }
                    break;
                case 2:
                    if (bb == '?') {
                        if ((ba = pushbackReader.read()) != -1) {
                            char bm = (char) ba;
                            if (bm == '>') {
                                this.handleProcessingInstruction(this.processingInstruction, list1);
                                if (list1 != null) {
                                    list1.add("?>");
                                }

                                this.setParserState(1);
                            } else {
                                this.processingInstruction.append(bb);
                                pushbackReader.unread(bm);
                            }
                        }
                    } else {
                        this.processingInstruction.append(bb);
                    }
                    break;
                case 3:
                    char bz;
                    byte cc;
                    if (list1 != null) {
                        list1.add(bb < ASCII_CHAR_STRINGS.length ? ASCII_CHAR_STRINGS[bb] : bb);
                        bz = bb;
                        cc = 62;
                    } else {
                        bz = bb;
                        cc = 62;
                    }

                    if (bz == cc) {
                        this.setParserState(1);
                    }
                    break;
                case 4:
                    char by;
                    byte cb;
                    if (list1 != null) {
                        list1.add(bb < ASCII_CHAR_STRINGS.length ? ASCII_CHAR_STRINGS[bb] : bb);
                        by = bb;
                        cb = 62;
                    } else {
                        by = bb;
                        cb = 62;
                    }

                    if (by == cb) {
                        this.setParserState(1);
                    } else if (bb == '[') {
                        this.setParserState(5);
                    }
                    break;
                case 5:
                    if (list1 != null) {
                        list1.add(bb < ASCII_CHAR_STRINGS.length ? ASCII_CHAR_STRINGS[bb] : bb);
                    }

                    if (this.isQuoteChar(bb)) {
                        bl1 = !bl1;
                        if (bl1) {
                            this.quoteChar = bb;
                        }
                    } else if (!bl1 && bb == ']') {
                        this.setParserState(4);
                    }
                    break;
                case 6:
                    char bx;
                    byte ca;
                    if (list1 != null) {
                        list1.add(bb < ASCII_CHAR_STRINGS.length ? ASCII_CHAR_STRINGS[bb] : bb);
                        bx = bb;
                        ca = 93;
                    } else {
                        bx = bb;
                        ca = 93;
                    }

                    if (bx == ca && (ba = pushbackReader.read()) != -1) {
                        char bk = (char) ba;
                        if (bk == ']') {
                            if ((ba = pushbackReader.read()) != -1) {
                                char bc = (char) ba;
                                if (bc == '>') {
                                    if (list1 != null) {
                                        list1.add("]>");
                                    }

                                    this.setParserState(1);
                                } else {
                                    pushbackReader.unread(bc);
                                    pushbackReader.unread(bk);
                                }
                            } else if (list1 != null) {
                                list1.add(bk < ASCII_CHAR_STRINGS.length ? ASCII_CHAR_STRINGS[bk] : bk);
                            }
                        } else {
                            pushbackReader.unread(bk);
                        }
                    }
                    break;
                case 7:
                    char ci;
                    byte cj;
                    if (this.isQuoteChar(bb)) {
                        bl1 = !bl1;
                        if (bl1) {
                            this.quoteChar = bb;
                            ci = bb;
                            cj = 62;
                        } else {
                            ci = bb;
                            cj = 62;
                        }
                    } else {
                        ci = bb;
                        cj = 62;
                    }

                    if (ci == cj) {
                        if (!bl1) {
                            switch (this.attributeState) {
                                case 1:
                                    this.tokenBuffer.append(bb);
                                    this.currentElement.addRawText(this.tokenBuffer.toString());
                                    this.tokenBuffer.setLength(0);
                                    break;
                                case 2:
                                    this.currentElement.addName(this.tokenBuffer.toString());
                                    if (!this.tagNameComplete) {
                                        this.tagNameComplete = true;
                                        this.tagNameBuffer.append(this.tokenBuffer);
                                    }

                                    this.tokenBuffer.setLength(0);
                                    this.tokenBuffer.append(bb);
                                    this.currentElement.addRawText(this.tokenBuffer.toString());
                                    this.tokenBuffer.setLength(0);
                            }

                            this.dispatchTag(1, list1, printWriter);
                            this.setParserState(1);
                            break;
                        }

                        ci = bb;
                        cj = 47;
                    } else {
                        ci = bb;
                        cj = 47;
                    }

                    if (ci == cj && !bl1) {
                        if ((ba = pushbackReader.read()) != -1) {
                            char bj = (char) ba;
                            if (bj == '>') {
                                switch (this.attributeState) {
                                    case 1:
                                        this.tokenBuffer.append(bb);
                                        this.tokenBuffer.append(bj);
                                        this.currentElement.addRawText(this.tokenBuffer.toString());
                                        this.tokenBuffer.setLength(0);
                                        break;
                                    case 2:
                                        this.currentElement.addName(this.tokenBuffer.toString());
                                        if (!this.tagNameComplete) {
                                            this.tagNameComplete = true;
                                            this.tagNameBuffer.append(this.tokenBuffer);
                                        }

                                        this.tokenBuffer.setLength(0);
                                        this.tokenBuffer.append(bb);
                                        this.tokenBuffer.append(bj);
                                        this.currentElement.addRawText(this.tokenBuffer.toString());
                                        this.tokenBuffer.setLength(0);
                                }

                                this.dispatchTag(3, list1, printWriter);
                                this.setParserState(1);
                            } else {
                                this.tokenBuffer.append(bb);
                                pushbackReader.unread(bj);
                            }
                        }
                    } else {
                        switch (this.attributeState) {
                            case 1:
                                if (!bl2 && bb != '=' && bb != this.quoteChar) {
                                    this.currentElement.addRawText(this.tokenBuffer.toString());
                                    this.tokenBuffer.setLength(0);
                                    this.tokenBuffer.append(bb);
                                    this.attributeState = 2;
                                } else {
                                    this.tokenBuffer.append(bb);
                                    if (bb == this.quoteChar) {
                                        this.currentElement.addRawText(this.tokenBuffer.toString());
                                        this.tokenBuffer.setLength(0);
                                        this.attributeState = 3;
                                    }
                                }
                                break;
                            case 2:
                                if (!bl2 && bb != '=') {
                                    this.tokenBuffer.append(bb);
                                    break;
                                }

                                this.currentElement.addName(this.tokenBuffer.toString());
                                if (!this.tagNameComplete) {
                                    this.tagNameComplete = true;
                                    this.tagNameBuffer.append(this.tokenBuffer);
                                }

                                this.tokenBuffer.setLength(0);
                                this.tokenBuffer.append(bb);
                                this.attributeState = 1;
                                break;
                            case 3:
                                if (bb == this.quoteChar) {
                                    this.currentElement.addAttributeValue(this.tokenBuffer.toString());
                                    this.tokenBuffer.setLength(0);
                                    this.tokenBuffer.append(bb);
                                    this.attributeState = 1;
                                } else {
                                    this.tokenBuffer.append(bb);
                                }
                        }

                        if (this.tokenBuffer.length() == "<!--".length() && this.tokenBuffer.toString().equals("<!--")) {
                            this.readComment(this.tokenBuffer, pushbackReader);
                            this.currentElement.addRawText(this.tokenBuffer.toString());
                            this.tokenBuffer.setLength(0);
                            this.attributeState = 1;
                        }
                    }
                    break;
                case 8:
                    if (bb == '>') {
                        this.dispatchTag(2, list1, printWriter);
                        if (list1 != null) {
                            if (this.pendingWhitespace.length() > 0) {
                                list1.add(this.pendingWhitespace.toString());
                                this.pendingWhitespace.setLength(0);
                            }

                            list1.add(bb < ASCII_CHAR_STRINGS.length ? ASCII_CHAR_STRINGS[bb] : bb);
                        }

                        this.setParserState(1);
                    } else if (bl2) {
                        if (this.tagNameStarted && !this.tagNameComplete) {
                            this.tagNameComplete = true;
                            this.pendingWhitespace.setLength(0);
                        }

                        if (list1 != null) {
                            if (this.tagNameStarted) {
                                this.pendingWhitespace.append(bb);
                            } else {
                                list1.add(bb < ASCII_CHAR_STRINGS.length ? ASCII_CHAR_STRINGS[bb] : bb);
                            }
                        }
                    } else if (bb == '<') {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(bb);
                        this.readComment(stringBuilder, pushbackReader);
                        this.pendingWhitespace.append(stringBuilder.toString());
                    } else {
                        this.tagNameStarted = true;
                        if (!this.tagNameComplete) {
                            this.tagNameBuffer.append(bb);
                        }
                    }
            }

            if (!bl1 && this.quoteChar != '~') {
                this.quoteChar = '~';
            }
        }

        if (this.parserState == 1 && this.characterData.length() > 0) {
            String string1 = this.characterData.toString();
            List list2 = list1;
            this.handleCharacterData(string1, (String) null, list2, printWriter);
        }
    }

    public static String readFileContent(File file1, ObservableHolder observableHolder, MutableInt mutableInt, MutableInt mutableInt1, MutableInt mutableInt2) throws ZkmException, IOException {
        String string = detectEncoding(new FileInputStream(file1), mutableInt, mutableInt1, mutableInt2);
        observableHolder.setValue(string);
        return ZkmFileUtils.readFileAsString(file1, string);
    }

    public void handleProcessingInstruction(StringBuilder stringBuilder, List list1) {
        if (list1 != null) {
            this.contentHandler.rewriteProcessingInstruction(stringBuilder.toString().trim(), list1);
        } else {
            this.contentHandler.analyzeProcessingInstruction(stringBuilder.toString().trim());
        }
    }

    public static String detectByteOrderMark(PushbackInputStream pushbackInputStream, MutableInt mutableInt, MutableInt mutableInt1, MutableInt mutableInt2) throws IOException {
        mutableInt1.setValue(0);
        mutableInt.setValue(0);
        mutableInt2.setValue(Integer.MAX_VALUE);
        String string = "UTF-8";
        byte[] ba = new byte[4];
        int bb = ZkmFileUtils.readFully(pushbackInputStream, ba);
        if (bb == -1) {
            bb = 0;
            pushbackInputStream.unread(ba);
        } else {
            pushbackInputStream.unread(ba);
        }

        if (bb > 1) {
            int bc = ba[0] & 255;
            int bd = ba[1] & 255;
            int be = ba[2] & 255;
            int bf = ba[3] & 255;
            if (bc != 60 || bd != 63 || be != 120 || bf != 109) {
                if (bc == 239 && bd == 187 && be == 191) {
                    mutableInt1.setValue(3);
                    mutableInt.setValue(3);
                } else if (bc == 255 && bd == 254) {
                    string = "UTF-16";
                    mutableInt1.setValue(2);
                    mutableInt2.setValue(0);
                } else if (bc == 254 && bd == 255) {
                    string = "UTF-16";
                    mutableInt1.setValue(2);
                    mutableInt2.setValue(1);
                } else if (bc == 60 && bd == 0 && be == 63 && bf == 0) {
                    string = "UTF-16LE";
                    mutableInt2.setValue(0);
                } else if (bc == 0 && bd == 60 && be == 0 && bf == 63) {
                    string = "UTF-16BE";
                    mutableInt2.setValue(1);
                }
            }
        }

        return string;
    }

    public void handleStartTag(XmlElementNode xmlElementNode, List list1, PrintWriter printWriter) throws ZkmException, IOException {
        if (list1 != null) {
            this.contentHandler.rewriteStartTag(xmlElementNode, list1);
            if (this.contentHandler.shouldFlushOutput()) {
                this.flushOutput(list1, printWriter);
            }
        } else {
            this.contentHandler.analyzeStartTag(xmlElementNode, this.classReferences, this.fieldReferences, this.methodReferences, this.methodNameReferences);
        }
    }

    private void handleCharacterData(String string, String string1, List list1, PrintWriter printWriter) throws ZkmException, IOException {
        String string2 = string1;
        if (string2 == null) {
            String string3 = string.trim();
            if (string3.length() > 1 && string3.charAt(0) == '&' && string3.charAt(string3.length() - 1) == ';') {
                string2 = "!ENTITY";
            }
        }

        if (list1 != null) {
            this.contentHandler.rewriteCharacterData(string, string2, list1);
            if (this.contentHandler.shouldFlushOutput()) {
                this.flushOutput(list1, printWriter);
            }
        } else {
            this.contentHandler.analyzeCharacterData(string, string2, this.classReferences, this.fieldReferences);
        }
    }

    private boolean isQuoteChar(int ba) {
        return this.quoteChar != '~' ? ba == this.quoteChar : ba == 39 || ba == 34;
    }

    public void handleEmptyElementTag(XmlElementNode xmlElementNode, List list1, PrintWriter printWriter) throws ZkmException, IOException {
        if (list1 != null) {
            this.contentHandler.rewriteEmptyElementTag(xmlElementNode, list1);
            if (this.contentHandler.shouldFlushOutput()) {
                this.flushOutput(list1, printWriter);
            }
        } else {
            this.contentHandler
                    .analyzeEmptyElementTag(xmlElementNode, this.classReferences, this.fieldReferences, this.methodReferences, this.methodNameReferences);
        }
    }

    public XmlResourceProcessor(
            InputStream inputStream1, XmlContentHandler xmlContentHandler, String string, int ba, Map map1, Map map2, Map map3, TwoKeyMap twoKeyMap, String string1
    ) throws ZkmException, IOException {
        String string2;
        if (string == null) {
            string2 = "UTF-8";
        } else {
            string2 = string;
        }

        ZkmFileUtils.readBytes(inputStream1, ba);
        PushbackReader pushbackReader = new PushbackReader(new BufferedReader(new InputStreamReader(inputStream1, string2)), 7);
        this.contentHandler = xmlContentHandler;
        this.resourceName = string1;
        this.classReferences = map1;
        this.fieldReferences = map2;
        this.methodReferences = map3;
        this.methodNameReferences = twoKeyMap;
        Boolean boolean1 = false;
        Object object = null;
        this.parse(pushbackReader, (List) null, (PrintWriter) object, boolean1);
    }

    public final void setParserState(int parserState) {
        this.parserState = parserState;
        switch (this.parserState) {
            case 1:
                this.tagNameBuffer.setLength(0);
                this.tagNameStarted = false;
                this.tagNameComplete = false;
                this.pendingWhitespace.setLength(0);
                break;
            case 2:
                this.processingInstruction.setLength(0);
                break;
            case 7:
                this.attributeState = 1;
                this.currentElement = new XmlElementNode();
                this.tokenBuffer.setLength(0);
            default:
                this.characterData.setLength(0);
        }
    }

    public static String readZipEntryContent(
            ZipFile zipFile1, ZipEntry zipEntry1, ObservableHolder observableHolder, MutableInt mutableInt, MutableInt mutableInt1, MutableInt mutableInt2
    ) throws ZkmException, IOException {
        String string = detectEncoding(zipFile1.getInputStream(zipEntry1), mutableInt, mutableInt1, mutableInt2);
        observableHolder.setValue(string);
        return ZkmFileUtils.readEntryAsString(zipFile1, zipEntry1, string);
    }

    public void dispatchTag(int ba, List list1, PrintWriter printWriter) throws ZkmException, IOException {
        this.tagNameComplete = true;
        this.currentTagName = this.tagNameBuffer.toString().trim();
        switch (ba) {
            case 1:
                this.handleStartTag(this.currentElement, list1, printWriter);
                break;
            case 2:
                this.handleEndTag(this.currentTagName, list1, printWriter);
                break;
            case 3:
                this.handleEmptyElementTag(this.currentElement, list1, printWriter);
        }
    }

    public static byte[] prependByteOrderMark(byte[] ba, int bb, int bc) {
        byte[] bd = ba;
        if (bb > 0) {
            switch (bb) {
                case 2:
                    if (bd.length < 2 || bd[0] != -2 && bd[0] != -1) {
                        byte[] bg = new byte[2];
                        if (bc == 1) {
                            bg[0] = -2;
                            bg[1] = -1;
                        } else if (bc == 0) {
                            bg[0] = -1;
                            bg[1] = -2;
                        }

                        byte[] bf = new byte[bd.length + 2];
                        bf[0] = bg[0];
                        bf[1] = bg[1];
                        System.arraycopy(bd, 0, bf, 2, bd.length);
                        bd = bf;
                    }
                    break;
                case 3:
                    if (bd.length < 3 || bd[0] != -17) {
                        byte[] be = new byte[bd.length + 3];
                        be[0] = -17;
                        be[1] = -69;
                        be[2] = -65;
                        System.arraycopy(bd, 0, be, 3, bd.length);
                        bd = be;
                    }
            }
        }

        return bd;
    }

    public static String detectEncoding(final InputStream in, final MutableInt mutableInt, final MutableInt mutableInt2, final MutableInt mutableInt3) {
        String detectByteOrderMark = null;
        String trim = null;
        PushbackReader pushbackReader = null;
        final PushbackInputStream in2 = new PushbackInputStream(in, 4);
        try {
            int n = 1;
            final StringBuffer sb = new StringBuffer();
            detectByteOrderMark = detectByteOrderMark(in2, mutableInt, mutableInt2, mutableInt3);
            ZkmFileUtils.readBytes(in2, mutableInt.getValue());
            pushbackReader = new PushbackReader(new BufferedReader(new InputStreamReader(in2, detectByteOrderMark)), 7);
            int read;
            while ((read = pushbackReader.read()) != -1) {
                final char c = (char) read;
                if (c != ' ' && c != '\t' && c != '\n') {
                    if (c == '\r') {
                        continue;
                    }
                    if (n != 0) {
                        n = 0;
                        final int read2;
                        if (c == '<' && (read2 = pushbackReader.read()) != -1 && (char) read2 == '?') {
                            continue;
                        }
                        break;
                    } else if (c == '?') {
                        final int read3;
                        if ((read3 = pushbackReader.read()) != -1) {
                            final char c2 = (char) read3;
                            if (c2 == '>') {
                                break;
                            }
                            pushbackReader.unread(c2);
                            sb.append(c);
                        } else {
                            sb.append(c);
                        }
                    } else {
                        sb.append(c);
                    }
                }
            }
            final String string = sb.toString();
            final int index = string.indexOf("encoding=\"");
            if (index > -1) {
                final int n2 = index + "encoding=\"".length();
                if (n2 < string.length()) {
                    final int index2 = string.indexOf(34, n2);
                    if (index2 > -1) {
                        trim = string.substring(n2, index2).trim();
                        if (trim.length() == 0) {
                            trim = null;
                        }
                    }
                }
            }
            try {
                pushbackReader.close();
            } catch (final IOException ex) {
            }
        } catch (final IOException ex2) {
        } finally {
            if (pushbackReader != null) {
                try {
                    pushbackReader.close();
                } catch (final IOException ex3) {
                }
            } else if (in != null) {
                try {
                    in.close();
                } catch (final IOException ex4) {
                }
            }
        }
        if (trim == null) {
            trim = detectByteOrderMark;
        }
        if (trim.equals("UTF-16")) {
            if (mutableInt2.getValue() == 2) {
                mutableInt.setValue(2);
            }
            if (mutableInt3.getValue() == 0) {
                trim = "UTF-16LE";
            } else if (mutableInt3.getValue() == 1) {
                trim = "UTF-16BE";
            }
        }
        return trim;
    }

    public void flushOutput(List list1, PrintWriter printWriter) throws ZkmException, IOException {
        this.contentHandler.beforeFlushOutput();

        for (Object object : list1) {
            printWriter.print(object);
        }

        list1.clear();
    }

    public final void handleEndTag(String string, List list1, PrintWriter printWriter) throws ZkmException, IOException {
        if (string.length() > 0) {
            this.contentHandler.handleEndTag(string, list1);
            if (list1 != null && this.contentHandler.shouldFlushOutput()) {
                this.flushOutput(list1, printWriter);
            }
        }
    }

    private void readComment(StringBuilder stringBuilder, PushbackReader pushbackReader) throws XmlContentException, IOException {
        int ba;
        while ((ba = pushbackReader.read()) != -1) {
            char bb = (char) ba;
            stringBuilder.append(bb);
            if (bb == '-' && (ba = pushbackReader.read()) != -1) {
                char bc = (char) ba;
                if (bc == '-') {
                    if ((ba = pushbackReader.read()) != -1) {
                        char bd = (char) ba;
                        if (bd == '>') {
                            stringBuilder.append("->");
                            return;
                        }

                        pushbackReader.unread(bd);
                        pushbackReader.unread(bc);
                    } else {
                        pushbackReader.unread(bc);
                    }
                } else {
                    pushbackReader.unread(bc);
                }
            }
        }

        throw new XmlContentException("Unterminated comment in '" + this.resourceName + "'");
    }
}
