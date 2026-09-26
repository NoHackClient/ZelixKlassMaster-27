package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.DescribableSpec;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public abstract class AndSpecifierNode extends ZkmScriptSimpleNode {
    private static final String AND_SEPARATOR = " && ";

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }
    }

    public String getSpecText() {
        StringBuffer stringBuffer = new StringBuffer();
        int ba = this.children.length;
        if (ba > 1) {
            stringBuffer.append('(');
        }

        for (int i = 0; i < ba; i++) {
            DescribableSpec describableSpec = (DescribableSpec) this.children[i];
            stringBuffer.append(describableSpec.getSpecText());
            if (i < ba - 1) {
                stringBuffer.append(AND_SEPARATOR);
            }
        }

        if (ba > 1) {
            stringBuffer.append(')');
        }

        return stringBuffer.toString();
    }

    public AndSpecifierNode(int ba) {
        super(ba);
    }
}
