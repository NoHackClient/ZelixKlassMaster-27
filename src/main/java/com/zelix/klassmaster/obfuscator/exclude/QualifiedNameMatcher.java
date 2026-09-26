package com.zelix.klassmaster.obfuscator.exclude;

public interface QualifiedNameMatcher extends DescribableSpec {
    boolean matchesName(String string);
}
