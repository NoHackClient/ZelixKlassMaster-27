package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.DescribableSpec;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public abstract class ComplexSpecifierNode extends ZkmScriptSimpleNode {
    public abstract boolean isLiteralName();

    public ComplexSpecifierNode(int ba) {
        super(ba);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
    }

    public final String getChildDescription() {
        return ((DescribableSpec) this.jjtGetChild(0)).getSpecText();
    }

    public String getSpecText() {
        return ((DescribableSpec) this.jjtGetChild(0)).getSpecText();
    }
}
