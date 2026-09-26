package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;

public abstract class DelegatingParameterNode extends ZkmScriptParameterNode {
    public SingleValueParameterNode valueParameter;

    @Override
    public int getValueCount() {
        return this.valueParameter.getValueCount();
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.valueParameter = (SingleValueParameterNode) this.jjtGetChild(0);
        this.valueParameter.execute(this, scriptEnvironment1);
    }

    public DelegatingParameterNode(int ba) {
        super(ba);
    }

    @Override
    public String getParameterName() {
        return this.valueParameter.getParameterName();
    }

    @Override
    public String getValue(int ba) {
        return this.valueParameter.getValue(ba);
    }
}
