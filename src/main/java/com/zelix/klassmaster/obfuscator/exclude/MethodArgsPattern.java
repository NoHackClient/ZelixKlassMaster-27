package com.zelix.klassmaster.obfuscator.exclude;

import java.util.regex.Pattern;

public class MethodArgsPattern {
    private static final String ANY_ARGS = "(*)";
    public final int argCount;
    public final String argsText;
    public final Pattern argsRegex;

    public MethodArgsPattern(int argCount, String string, Pattern pattern1) {
        this.argCount = argCount;
        this.argsText = string;
        this.argsRegex = pattern1;
    }

    public boolean isAnyArgs() {
        return this.argsText.equals(ANY_ARGS);
    }
}
