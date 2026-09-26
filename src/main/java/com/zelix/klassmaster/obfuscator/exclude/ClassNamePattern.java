package com.zelix.klassmaster.obfuscator.exclude;

public interface ClassNamePattern {
    boolean isLiteralName();

    boolean hasPlusTag();

    boolean hasCaretTag();

    double computeSpecificity(Object object, Object object1);

    String getSpecText();

    boolean matchesName(String string);

    String getNamePattern();
}
