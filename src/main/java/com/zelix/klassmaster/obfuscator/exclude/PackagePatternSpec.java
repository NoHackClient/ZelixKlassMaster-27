package com.zelix.klassmaster.obfuscator.exclude;

import java.util.List;

public interface PackagePatternSpec {
    boolean isLiteralName();

    boolean hasDotSuffix();

    boolean matchesName(String string);

    List getNameSegments();

    double computeSpecificity();

    boolean hasCaretTag();

    String getSpecText();

    String getNamePattern();
}
