package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

public abstract class ZkmScriptParameterNode extends ZkmScriptSimpleNode {
    public abstract String getParameterName();

    public abstract int getValueCount();

    public ZkmScriptParameterNode(int ba) {
        super(ba);
    }

    public abstract String getValue(int ba);
}
