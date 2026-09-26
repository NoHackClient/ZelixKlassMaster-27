package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.DescribableSpec;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public abstract class OrSpecifierNode extends ZkmScriptSimpleNode {
    public boolean negated = false;
    private static final String OR_SEPARATOR = " || ";

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }
    }

    public String getSpecText() {
        StringBuilder stringBuilder;
        int ba;
        label44:
        {
            stringBuilder = new StringBuilder();
            ba = this.children.length;
            StringBuilder stringBuilder2;
            char bd;
            if (!this.negated) {
                if (ba <= 1) {
                    break label44;
                }

                stringBuilder2 = stringBuilder;
                bd = '(';
            } else {
                stringBuilder2 = stringBuilder;
                bd = '(';
            }

            stringBuilder2.append(bd);
        }

        for (int i = 0; i < ba; i++) {
            DescribableSpec describableSpec = (DescribableSpec) this.children[i];
            stringBuilder.append(describableSpec.getSpecText());
            if (i < ba - 1) {
                stringBuilder.append(OR_SEPARATOR);
            }
        }

        StringBuilder stringBuilder1;
        char bc;
        if (!this.negated) {
            if (ba <= 1) {
                return (this.negated ? "!" : "") + stringBuilder.toString();
            }

            stringBuilder1 = stringBuilder;
            bc = ')';
        } else {
            stringBuilder1 = stringBuilder;
            bc = ')';
        }

        stringBuilder1.append(bc);
        return (this.negated ? "!" : "") + stringBuilder.toString();
    }

    public void setNegated() {
        this.negated = true;
    }

    public OrSpecifierNode(int ba) {
        super(ba);
    }
}
