package com.zelix.klassmaster.proguard;

import com.zelix.klassmaster.util.StringKeyTransform;

public class LineCommentStripper implements StringKeyTransform {
    private static final String COMMENT_PREFIX = "//";

    @Override
    public String transformLine(String string) {
        int ba = string.indexOf(COMMENT_PREFIX);
        return ba > -1 ? string.substring(0, ba) : string;
    }
}
