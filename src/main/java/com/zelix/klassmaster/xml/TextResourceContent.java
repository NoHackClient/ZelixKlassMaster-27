package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;

public class TextResourceContent {
    public final ObservableHolder encoding;
    public final MutableInt skipByteCount;
    public final MutableInt bomLength;
    public final MutableInt byteOrder;
    public final String resourceName;
    public final String content;

    public TextResourceContent(
            ObservableHolder observableHolder, MutableInt mutableInt, MutableInt mutableInt1, MutableInt mutableInt2, String string, String string1
    ) {
        this.encoding = observableHolder;
        this.skipByteCount = mutableInt;
        this.bomLength = mutableInt1;
        this.byteOrder = mutableInt2;
        this.resourceName = string;
        this.content = string1;
    }
}
