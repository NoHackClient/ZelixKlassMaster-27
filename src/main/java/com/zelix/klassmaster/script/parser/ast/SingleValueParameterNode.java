package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public abstract class SingleValueParameterNode extends ZkmScriptParameterNode {
    public ScriptValueNode valueNode;
    public String parameterName;

    public void setParameterName(String string) {
        this.parameterName = string;
    }

    public SingleValueParameterNode(int ba) {
        super(ba);
    }

    @Override
    public int getValueCount() {
        return this.valueNode != null ? 1 : 0;
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        this.valueNode = (ScriptValueNode) this.jjtGetChild(0);
    }

    @Override
    public String getValue(int ba) {
        if (ba == 0) {
            return this.valueNode.getValue();
        } else {
            throw new IllegalArgumentException(String.valueOf(ba));
        }
    }

    @Override
    public String getParameterName() {
        return this.parameterName;
    }
}
