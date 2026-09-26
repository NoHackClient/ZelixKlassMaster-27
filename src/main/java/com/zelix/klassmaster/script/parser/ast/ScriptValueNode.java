package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public abstract class ScriptValueNode extends ZkmScriptSimpleNode {
    public String value;

    public final void setValue(String string) {
        this.value = string;
    }

    public ScriptValueNode(int ba) {
        super(ba);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
    }

    public final String getValue() {
        return this.value;
    }
}
