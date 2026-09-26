package com.zelix.klassmaster.obfuscator.rename;

public class NameGeneratorDigit {
    private static final String SAME_OBJECT_MESSAGE = "Cannot specify same Object";
    public NameGeneratorDigit nextDigit;
    public NameGeneratorDigit carryDigit;
    private int index;
    private final char[] chars;
    private final String digitId;

    public void setCarryDigit(NameGeneratorDigit nameGeneratorDigit1) {
        if (nameGeneratorDigit1 == this) {
            throw new IllegalArgumentException(SAME_OBJECT_MESSAGE);
        }

        this.carryDigit = nameGeneratorDigit1;
        nameGeneratorDigit1.setNextDigit(this);
    }

    public void setNextDigit(NameGeneratorDigit nameGeneratorDigit1) {
        this.nextDigit = nameGeneratorDigit1;
    }

    public NameGeneratorDigit(char[] ba) {
        this(ba, "");
    }

    public NameGeneratorDigit(char[] chars, String string) {
        this.chars = chars;
        this.digitId = string;
    }

    public StringBuilder appendTo(StringBuilder stringBuilder) {
        stringBuilder.append(this.chars[this.index]);
        if (this.nextDigit != null) {
            this.nextDigit.appendTo(stringBuilder);
        }

        return stringBuilder;
    }

    public boolean increment() {
        this.index = ++this.index % this.chars.length;
        if (this.index == 0) {
            return this.carryDigit != null ? this.carryDigit.increment() : true;
        } else {
            return false;
        }
    }
}
