package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;

public class WildcardStringMatcher implements DescriptorMatcher {
    public static final DescriptorMatcher MATCH_ALL = new WildcardStringMatcher("*");
    public final String pattern;

    public WildcardStringMatcher(String string) {
        this.pattern = string;
    }

    @Override
    public boolean matchesDescriptor(String string) throws ZkmException, IOException {
        return ZkmStringUtils.matchesWildcard(string, this.pattern);
    }
}
